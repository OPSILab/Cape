import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { ServiceLinkingService } from './service-linking.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorDialogService } from '../pages/error-dialog/error-dialog.service';
import { NbDialogService, NbDialogRef } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { HttpResponse } from '@angular/common/http';
import { NgxConfigureService } from 'ngx-configure';
import { LinkingFromEnum } from '../model/service-linking/LinkingFromEnum';
import { AppConfig } from '../model/appConfig';

@Component({
  selector: 'service-linking',
  templateUrl: './service-linking.component.html',
  styleUrls: ['./service-linking.component.css'],
})
export class ServiceLinkingComponent implements OnInit, AfterViewInit {
  @ViewChild('linkedDialog', { static: true }) linkedDialog;
  @ViewChild('errorWithOptionDialog', { static: true }) errorWithOptionDialog;
  private openedDialog: NbDialogRef<unknown> = undefined;

  private operatorId: string;
  public linkingFrom: LinkingFromEnum;
  private locale: string;
  private initialLocale: string;
  private accountId: string;
  private serviceId: string;
  public serviceName: string;
  private surrogateId: string;
  private returnUrl: string;

  constructor(
    private configService: NgxConfigureService,
    private service: ServiceLinkingService,
    public router: Router,
    private route: ActivatedRoute,
    private translateService: TranslateService,
    private dialogService: NbDialogService,
    private errorDialogService: ErrorDialogService
  ) {
    this.operatorId = (this.configService.config as AppConfig).system.operatorId;
  }

  ngOnInit(): void {
    const queryParams = this.route.snapshot.queryParams;

    if (queryParams.serviceId === '' || queryParams.surrogateId === '' || queryParams.returnUrl === '') {
      this.errorDialogService.openErrorDialog(new Error('One or more mandatory linking parameters are missing!'));
      return;
    }

    this.locale = (queryParams.locale || 'en') as string;
    this.initialLocale = this.translateService.currentLang;
    this.translateService.use(this.locale);
    this.serviceId = queryParams.serviceId as string;
    this.serviceName = queryParams.serviceName as string;
    this.surrogateId = queryParams.surrogateId as string;
    this.returnUrl = queryParams.returnUrl as string;
    this.linkingFrom = queryParams.linkingFrom as LinkingFromEnum;
    this.accountId = localStorage.getItem('accountId');
  }

  async ngAfterViewInit(): Promise<void> {
    this.translateService.use(this.initialLocale);

    if (this.linkingFrom === LinkingFromEnum.Operator) {
      if (!this.serviceId) {
        this.errorDialogService.openErrorDialog(new Error(this.translateService.instant('general.services.noServiceIdOperatorLink')));
        await (async () => new Promise((resolve) => setTimeout(resolve, 2000)))();
        void this.router.navigate(['/']);
      } else void this.startLinking(false);
    }
  }

  public startLinking(forceLinking: boolean = false): void {
    if (this.linkingFrom === LinkingFromEnum.Service) void this.startLinkingFromServiceAfterOperatorLogin(forceLinking);
    else void this.startLinkingFromOperatorRedirectToServiceLogin(forceLinking);
  }

  public async startLinkingFromOperatorRedirectToServiceLogin(forceLinking: boolean = false): Promise<void> {
    try {
      const response: HttpResponse<unknown> = await this.service.startLinkingFromOperatorRedirectToServiceLogin(
        this.accountId,
        this.serviceId,
        forceLinking
      );

      const location: string = response.headers.get('Location');

      const url = new URLSearchParams(location);

      // const code = url.get('code');
      const operatorId = url.get('operatorId');
      const returnUrl = url.get('returnUrl');
      // const linkingFrom = url.get('linkingFrom');

      if (this.operatorId !== operatorId) {
        this.errorDialogService.openErrorDialog(new Error(this.translateService.instant('general.services.operatorIdMismatch')));
      } else {
        window.onmessage = (event: MessageEvent<{ result: string; message: string; serviceId: string; returnUrl: string }>) => {
          const result: string = event.data.result;
          const message: string = event.data.message;
          // const resSurrogateId: string = event.data.surrogateId;
          const resServiceId: string = event.data.serviceId;
          const resReturnUrl: string = event.data.returnUrl;
          if (resServiceId !== this.serviceId || resReturnUrl !== returnUrl)
            this.errorDialogService.openErrorDialog(new Error('Service ID or Return Url from Service do not match with the sent ones!'));
          else if (result === 'SUCCESS') {
            void this.router.navigate(['/pages/services/linkedServices'], {
              queryParams: { toastrMessage: message },
            });
          } else if (result === 'CANCELLED')
            void this.router.navigate(['/pages/services/availableServices'], {
              queryParams: { toastrMessage: message },
            });
        };

        // Open a new tab redirecting to the Service Login
        window.open(`${location}${location.includes('?') ? '&' : ''}locale=${this.locale}`, '_blank');
      }
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
      if (error.status === 409 && !error.error.message.includes('Current State: COMPLETED')) {
        this.openedDialog = this.dialogService.open(this.errorWithOptionDialog, {
          context: {
            // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
            error: error,
          },
          hasScroll: false,
        });
      } else this.errorDialogService.openErrorDialog(error);
    }
  }

  public async startLinkingFromServiceAfterOperatorLogin(forceLinking: boolean = false): Promise<void> {
    try {
      await this.service.startLinkingFromServiceAfterOperatorLogin(
        this.accountId,
        this.surrogateId,
        this.serviceId,
        this.returnUrl,
        this.linkingFrom,
        forceLinking
      );

      const successMessage: string = await this.getLocalizedLabel('general.services.linkingSuccessfulMessage', this.locale);

      this.openedDialog = this.dialogService.open(this.linkedDialog, {
        context: {
          message: successMessage,
        },
        hasScroll: false,
      });
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
      if (error.status === 409 && !error.error.message.includes('Current State: COMPLETED')) {
        this.openedDialog = this.dialogService.open(this.errorWithOptionDialog, {
          context: {
            // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
            error: error,
          },
          hasScroll: false,
        });
      } else this.errorDialogService.openErrorDialog(error);
    }
  }

  private async getLocalizedLabel(field: string, locale: string): Promise<string> {
    // Set temporarily the language coming from Service locale, in order to get the localized Success message
    const currentLang = this.translateService.currentLang;
    this.translateService.use(locale);
    const translatedLabel: string = (await this.translateService
      .get(field, { serviceId: this.serviceId, serviceName: this.serviceName })
      .toPromise()) as string;
    this.translateService.use(currentLang);
    return translatedLabel;
  }

  closeAndReturnToService(message: string): void {
    window.onunload = () => {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
      window.opener.postMessage(
        {
          result: 'SUCCESS',
          message: message,
          surrogateId: this.surrogateId,
          serviceId: this.serviceId,
          returnUrl: this.returnUrl,
        },
        this.returnUrl
      );
    };

    window.close();
  }

  close(message: string): void {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
    window.opener.postMessage(
      {
        result: 'SUCCESS',
        message: message,
        surrogateId: this.surrogateId,
        serviceId: this.serviceId,
        returnUrl: this.returnUrl,
      },
      this.returnUrl
    );
    this.openedDialog.close();
    void this.router.navigate(['/']);
  }

  cancel(): void {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
    window.opener.postMessage(
      {
        result: 'CANCELLED',
        message: this.translateService.instant('general.services.linkingCancelledMessage', {
          serviceName: this.serviceName,
          serviceId: this.serviceId,
        }) as string,
        surrogateId: this.surrogateId,
        serviceId: this.serviceId,
        returnUrl: this.returnUrl,
      },
      this.returnUrl
    );

    //this.openedDialog.close();
    window.close();
    location.replace(location.href.split('?')[0]);
  }
}
