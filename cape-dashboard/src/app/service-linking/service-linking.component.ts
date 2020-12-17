import { Component, OnInit, ViewChild, TemplateRef, AfterViewInit } from '@angular/core';
import { ServiceLinkingService } from './service-linking.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorDialogService } from '../pages/error-dialog/error-dialog.service';
import { NbDialogService, NbDialogRef, NbGlobalLogicalPosition, NbToastrService } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { HttpResponse } from '@angular/common/http';
import { NgxConfigureService } from 'ngx-configure';
import { LinkingFromEnum } from '../model/service-linking/LinkingFromEnum';



@Component({
  selector: 'service-linking',
  templateUrl: './service-linking.component.html',
  styleUrls: ['./service-linking.component.css']
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
  private serviceName: string;
  private surrogateId: string;
  private returnUrl: string;


  constructor(private configService: NgxConfigureService, private service: ServiceLinkingService, private router: Router,
    private route: ActivatedRoute, private translateService: TranslateService,
    private dialogService: NbDialogService, private errorDialogService: ErrorDialogService, private toastrService: NbToastrService) {

    this.operatorId = this.configService.config.system.operatorId;
  }


  ngOnInit(): void {

    const queryParams = this.route.snapshot.queryParams;

    if (queryParams.serviceId === '' || queryParams.surrogateId === '' || queryParams.returnUrl === '') {
      this.errorDialogService.openErrorDialog(new Error('One or more mandatory linking parameters are missing!'));
      return;
    }

    this.locale = queryParams.locale || 'en';
    this.initialLocale = this.translateService.currentLang;
    this.translateService.use(this.locale);
    this.serviceId = queryParams.serviceId;
    this.serviceName = queryParams.serviceName;
    this.surrogateId = queryParams.surrogateId;
    this.returnUrl = queryParams.returnUrl;
    this.linkingFrom = queryParams.linkingFrom;
    this.accountId = localStorage.getItem('accountId');
  }


  async ngAfterViewInit() {

    this.translateService.use(this.initialLocale);

    if (this.linkingFrom === LinkingFromEnum.Operator) {
      if (!this.serviceId) {
        this.errorDialogService.openErrorDialog(new Error(this.translateService.instant('general.services.noServiceIdOperatorLink')));
        await (async () => new Promise(resolve => setTimeout(resolve, 2000)))();
        this.router.navigate(['/']);
      } else
        this.startLinking(false);
    }
  }


  public startLinking(forceLinking: boolean = false) {

    if (this.linkingFrom === LinkingFromEnum.Service) this.startLinkingFromServiceAfterOperatorLogin(forceLinking);
    else
      this.startLinkingFromOperatorRedirectToServiceLogin(forceLinking);
  }


  public async startLinkingFromOperatorRedirectToServiceLogin(forceLinking: boolean = false) {

    try {
      const response: HttpResponse<unknown> = await this.service.startLinkingFromOperatorRedirectToServiceLogin(this.accountId, this.serviceId, forceLinking);

      const location: string = response.headers.get('Location');

      const url = new URLSearchParams(location);

      const code = url.get('code');
      const operatorId = url.get('operatorId');
      const returnUrl = url.get('returnUrl');
      const linkingFrom = url.get('linkingFrom');

      if (this.operatorId !== operatorId) {
        this.errorDialogService.openErrorDialog(new Error(this.translateService.instant('general.services.operatorIdMismatch')));

      } else {

        window.onmessage = async (event) => {

          const result: string = event.data.result;
          const message: string = event.data.message;
          const resSurrogateId: string = event.data.surrogateId;
          const resServiceId: string = event.data.serviceId;
          const resReturnUrl: string = event.data.returnUrl;
          console.log(JSON.stringify(event.data));
          if (resServiceId !== this.serviceId || resReturnUrl !== returnUrl)
            this.errorDialogService.openErrorDialog(new Error('Service ID or Return Url from Service do not match with the sent ones!'));
          else if (result === 'SUCCESS') {
            this.router.navigate(['/pages/services/linkedServices'], { queryParams: { toastrMessage: message } });
          } else if (result === 'CANCELLED')
            this.router.navigate(['/pages/services/availableServices'], { queryParams: { toastrMessage: message } });
        };


        // Open a new tab redirecting to the Service Login
        window.open(`${location}${location.includes('?') ? '&' : ''}locale=${this.locale}`, '_blank');
      }

    } catch (error) {
      if (error.status === 409 && !error.error.message.includes('Current State: COMPLETED')) {
        this.openedDialog = this.dialogService.open(this.errorWithOptionDialog, {
          context: {
            error: error
          }, hasScroll: false
        });

      } else
        this.errorDialogService.openErrorDialog(error);
    }

  }

  public async startLinkingFromServiceAfterOperatorLogin(forceLinking: boolean = false) {

    try {

      const response: any = await this.service.startLinkingFromServiceAfterOperatorLogin(
        this.accountId, this.surrogateId, this.serviceId, this.returnUrl, this.linkingFrom, forceLinking);

      const successMessage: string = await this.getLocalizedLabel('general.services.linkingSuccessfulMessage', this.locale);

      this.openedDialog = this.dialogService.open(this.linkedDialog, {
        context: {
          message: successMessage
        }, hasScroll: false
      });
    } catch (error) {
      if (error.status === 409 && !error.error.message.includes('Current State: COMPLETED')) {
        this.openedDialog = this.dialogService.open(this.errorWithOptionDialog, {
          context: {
            error: error
          }, hasScroll: false
        });

      } else
        this.errorDialogService.openErrorDialog(error);
    }
  }

  private getLocalizedLabel(field: string, locale: string): Promise<string> {

    // Set temporarily the language coming from Service locale, in order to get the localized Success message
    const currentLang = this.translateService.currentLang;
    this.translateService.use(locale);
    const translatedLabel = this.translateService.get(field, { serviceId: this.serviceId, serviceName: this.serviceName }).toPromise();
    this.translateService.use(currentLang);
    return translatedLabel;
  }

  closeAndReturnToService(message: string) {
    window.onunload = () => {
      window.opener.postMessage({
        result: 'SUCCESS',
        message: message, surrogateId: this.surrogateId, serviceId: this.serviceId, returnUrl: this.returnUrl
      }, this.returnUrl);
    };

    window.close();
  }

  close(message: string) {

    window.opener.postMessage({
      result: 'SUCCESS',
      message: message,
      surrogateId: this.surrogateId,
      serviceId: this.serviceId,
      returnUrl: this.returnUrl
    }, this.returnUrl);
    this.openedDialog.close();
    this.router.navigate(['/']);
  }

  cancel() {

    window.opener.postMessage({
      result: 'CANCELLED',
      message: this.translateService.instant('general.services.linkingCancelledMessage', { serviceName: this.serviceName, serviceId: this.serviceId }),
      surrogateId: this.surrogateId,
      serviceId: this.serviceId,
      returnUrl: this.returnUrl
    }, this.returnUrl);

    //this.openedDialog.close();
    window.close();
    location.replace(location.href.split('?')[0]);

  }


}
