import { Component } from '@angular/core';
import { NbDialogRef, NbDialogService } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { RoleEnum, CapeSdkAngularService, ConsentFormComponent, CapeSdkDialogService } from 'cape-sdk-angular';

@Component({
  selector: 'ngx-dialog-privacynotice',
  templateUrl: 'dialog-privacynotice.component.html',
  styleUrls: ['dialog-privacynotice.component.scss'],
})
export class DialogPrivacyNoticeComponent {
  sdkUrl: string;
  accountId: string;
  operatorId: string;
  dashboardUrl: string;
  serviceId: string;
  serviceRole: RoleEnum;
  purposeId: string;

  isSubmitDisabled = true;

  constructor(
    private errorDialogService: CapeSdkDialogService,
    protected ref: NbDialogRef<DialogPrivacyNoticeComponent>,
    private translateService: TranslateService,
    private dialogService: NbDialogService,
    private capeService: CapeSdkAngularService
  ) {}

  cancel() {
    this.ref.close();
  }

  submit() {
    this.ref.close(true);
  }

  toggle(checked: boolean) {
    this.isSubmitDisabled = !checked;
  }

  async openConsentForm() {
    this.translateService.use(sessionStorage.getItem('currentLocale'));
    try {
      this.dialogService.open(ConsentFormComponent, {
        hasScroll: true,
        autoFocus: true,
        closeOnBackdropClick: true,
        context: {
          sdkUrl: this.sdkUrl,
          consentForm: await this.capeService.fetchConsentForm(this.sdkUrl, this.accountId, this.serviceId, this.operatorId, this.purposeId, this.serviceRole),
          locale: sessionStorage.getItem('currentLocale') as string,
          showAdditionalOptions: true,
        },
      });
    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }
  }
}
