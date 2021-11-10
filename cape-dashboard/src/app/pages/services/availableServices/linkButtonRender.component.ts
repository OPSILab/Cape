import { Component, Input, OnInit, ChangeDetectorRef, ViewChild, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';
import { ErrorDialogService } from '../../../error-dialog/error-dialog.service';
import { LinkedServicesService } from '../linkedServices/linkedServices.service';
import { LinkingFromEnum } from '../../../model/service-linking/LinkingFromEnum';
import { AvailableServiceRow } from './availableServices.component';
import { ServiceLinkingService } from '../../../service-linking/service-linking.service';
import { NgxConfigureService } from 'ngx-configure';
import { AppConfig } from '../../../model/appConfig';
import { NbDialogRef } from '@nebular/theme';

@Component({
  template: `
    <button nbButton ghost [disabled]="isAlreadyLinked" shape="round" size="small" status="primary" (click)="startServiceLinking()">
      <i class="material-icons">link</i>
    </button>
    <ng-template #linkedDialog let-data let-ref="dialogRef">
      <nb-card>
        <nb-card-header class="d-flex justify-content-between">
          <h5 class="h5 mr-3">{{ 'general.services.linkingSuccessfulHeader' | translate }}</h5>
          <button nbButton ghost shape="rectangle" size="small" (click)="ref.close()">
            <i class="material-icons">close</i>
          </button>
        </nb-card-header>
        <nb-card-body class="m-3">
          {{ data.message }}
        </nb-card-body>
        <nb-card-footer class="d-flex justify-content-end">
          <button nbButton ghost shape="rectangle" size="medium" (click)="ref.close()">
            {{ 'general.close' | translate }}
          </button>
          <button nbButton ghost shape="rectangle" size="medium" (click)="closeAndGoToService(ref, data.returnUrl)">
            {{ 'general.services.closeAndGoToServiceButton' | translate }}
          </button>
        </nb-card-footer>
      </nb-card>
    </ng-template>

    <ng-template #errorWithOptionDialog let-data let-ref="dialogRef">
      <nb-card accent="danger" style="max-width: 95vw; max-height: 95vh">
        <nb-card-header class="d-flex justify-content-between">
          <h5>Error</h5>
          <button nbButton appearance="outline" shape="rectangle" size="tiny" status="info" class="close" (click)="router.navigate(['/'])">
            <i class="material-icons">close</i>
          </button>
        </nb-card-header>
        <nb-card-body class="m-3">
          <div class="row">{{ data.error.error ? data.error.error.message : data.error.message }}</div>
          <div class="row mt-1 justify-content-center">
            <strong>Status: {{ data.error.status }}</strong>
          </div>
        </nb-card-body>
        <nb-card-footer>
          <div class="row justify-content-center">
            <button nbButton appearance="outline" shape="rectangle" status="info" (click)="startLinking(true)">
              {{ 'general.services.forceLinkingButton' | translate }}
            </button>
          </div>
        </nb-card-footer>
      </nb-card>
    </ng-template>
  `,
})
export class LinkButtonRenderComponent implements OnInit {
  @Input() value: AvailableServiceRow;

  isAlreadyLinked = true;
  config: AppConfig;
  @ViewChild('linkedDialog', { static: true }) linkedDialog: TemplateRef<unknown>;
  @ViewChild('errorWithOptionDialog', { static: true }) errorWithOptionDialog: TemplateRef<unknown>;
  private openedDialog: NbDialogRef<unknown> = undefined;

  constructor(
    private linkedServicesService: LinkedServicesService,
    private serviceLinkingService: ServiceLinkingService,
    private configService: NgxConfigureService,
    private router: Router,
    private errorDialogService: ErrorDialogService,
    private cdr: ChangeDetectorRef
  ) {}

  async ngOnInit(): Promise<void> {
    this.config = this.configService.config as AppConfig;
    try {
      await this.linkedServicesService.getServiceLinkByServiceId(this.value.serviceId);
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error.status === 404) {
        this.isAlreadyLinked = false;
        this.cdr.detectChanges();
      } else this.errorDialogService.openErrorDialog(error);
    }
  }

  startServiceLinking = async (): Promise<void> => {
    if (!this.value.serviceInstance.isBackendLinking) {
      void this.router.navigate(['/serviceLinking'], {
        queryParams: {
          serviceId: this.value.serviceId,
          linkingFrom: LinkingFromEnum.Operator,
          locale: localStorage.getItem('currentLocale') || this.value.locale,
        },
      });
    } else {
      const sdkUrl = this.value.serviceInstance.serviceUrls.libraryDomain;
      const operatorId = this.config.system.operatorId;
      const returnUrl = this.value.serviceInstance.serviceUrls.linkingRedirectUri;

      await this.serviceLinkingService.automaticLinkFromOperator(
        sdkUrl,
        operatorId,
        this.value.serviceId,
        this.value.name,
        localStorage.getItem('accountId'),
        returnUrl,
        this.linkedDialog,
        this.errorWithOptionDialog
      );
      this.isAlreadyLinked = true;
      this.cdr.detectChanges();
    }
  };

  public closeAndGoToService(dialogRef: NbDialogRef<unknown>, returnUrl: string): void {
    dialogRef.close();
    window.open(returnUrl, '_blank');
  }
}
