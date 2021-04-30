import { Component, OnInit, Input } from '@angular/core';
import { LinkedServicesService } from './linkedServices.service';
import { SlStatusEnum } from '../../../model/service-linking/serviceLinkStatusRecordPayload';
import { LoginService } from '../../../auth/login/login.service';
import { ErrorDialogService } from '../../../error-dialog/error-dialog.service';
import { NbToastrService, NbGlobalLogicalPosition } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { LinkedServiceRow } from './linkedServices.component';

@Component({
  template: ` <nb-toggle [(checked)]="toggleValue" status="primary" (checkedChange)="onToggle()"></nb-toggle> `,
})
export class EnableServiceLinkButtonRenderComponent implements OnInit {
  @Input() value: LinkedServiceRow;
  toggleValue = false;

  constructor(
    protected service: LinkedServicesService,
    private loginService: LoginService,
    private errorDialogService: ErrorDialogService,
    private toastrService: NbToastrService,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    this.toggleValue = this.value.status === SlStatusEnum.Active;
  }

  async onToggle(): Promise<void> {
    try {
      if (!this.toggleValue) {
        await this.service.disableServiceLink(this.value.service_id, this.value.link_id);
        this.toastrService.primary(
          '',
          this.translate.instant('general.services.servicelink_disabled_message', { serviceName: this.value.service_name }),
          { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 4500 }
        );
      } else {
        await this.service.enableServiceLink(this.value.service_id, this.value.link_id);
        this.toastrService.primary(
          '',
          this.translate.instant('general.services.servicelink_enabled_message', { serviceName: this.value.service_name }),
          { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 4500 }
        );
      }
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error.error.statusCode === '401') {
        this.loginService.logout().catch((error) => this.errorDialogService.openErrorDialog(error));
      } else this.errorDialogService.openErrorDialog(error);
    }
  }
}
