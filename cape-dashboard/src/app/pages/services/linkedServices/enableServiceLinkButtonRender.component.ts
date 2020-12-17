import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ViewCell } from 'ng2-smart-table';
import { LinkedServicesService } from './linkedServices.service';
import { Router } from '@angular/router';
import { SlStatusEnum } from '../../../model/service-linking/serviceLinkStatusRecordPayload';
import { LoginService } from '../../../login/login.service';
import { ErrorDialogService } from '../../error-dialog/error-dialog.service';
import { NbToastrService, NbGlobalLogicalPosition } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';

@Component({
  template: `
<nb-toggle [(checked)]="toggleValue" status="primary" (checkedChange)="onToggle()"></nb-toggle>
  `
})
export class EnableServiceLinkButtonRenderComponent implements OnInit {

  public renderValue;
  toggleValue = false;

  @Input() value;

  constructor(protected service: LinkedServicesService, private loginService: LoginService,
    private errorDialogService: ErrorDialogService, private toastrService: NbToastrService,
    private translate: TranslateService, private router: Router) { }

  ngOnInit() {
    this.renderValue = this.value;
    this.toggleValue = this.value.status === SlStatusEnum.Active;

  }

  async onToggle() {

    try {
      if (!this.toggleValue) {

        await this.service.disableServiceLink(this.renderValue.serviceId, this.renderValue.slrId);
        this.toastrService.primary('', this.translate.instant('general.services.servicelink_disabled_message', { serviceName: this.renderValue.serviceName }),
          { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 4500 });

      } else {

        await this.service.enableServiceLink(this.renderValue.serviceId, this.renderValue.slrId);
        this.toastrService.primary('', this.translate.instant('general.services.servicelink_enabled_message', { serviceName: this.renderValue.serviceName }),
          { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 4500 });
      }

    } catch (error) {

      if (error.error.statusCode === '401') {
        this.loginService.logout();
        this.router.navigate(['/login']);
      } else
        this.errorDialogService.openErrorDialog(error);
    }
  }


}
