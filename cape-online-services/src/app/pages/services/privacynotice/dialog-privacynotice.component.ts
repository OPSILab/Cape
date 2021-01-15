import { Component } from '@angular/core';
import { NbDialogRef, NbDialogService } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { CapeSdkAngularService } from '../../../cape-sdk-angular/cape-sdk-angular.service';
import { ConsentFormComponent } from '../../../cape-sdk-angular/consent-form/consent-form.component';
import { ErrorDialogService } from '../../error-dialog/error-dialog.service';

@Component({
  selector: 'ngx-dialog-privacynotice',
  templateUrl: 'dialog-privacynotice.component.html',
  styleUrls: ['dialog-privacynotice.component.scss'],
})
export class DialogPrivacyNoticeComponent {
  sdkUrl: string;
  accountId:string;
  operatorId: string;
  dashboardUrl: string;
  serviceId: string;
  purposeId: string;
  sourceDatasetId: string;
  sourceServiceId: string;
  
  isSubmitDisabled=true;

 



  constructor(private errorDialogService: ErrorDialogService,protected ref: NbDialogRef<DialogPrivacyNoticeComponent>, private translateService: TranslateService, private dialogService: NbDialogService, private capeService: CapeSdkAngularService) {}

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

    try {
      this.dialogService.open(ConsentFormComponent,
        {
          hasScroll: true,
          autoFocus: true,
          closeOnBackdropClick: true,
          context: {
            sdkUrl: this.sdkUrl,
            consentForm: await this.capeService.fetchConsentForm(this.sdkUrl, this.accountId, this.serviceId, this.operatorId, this.purposeId, this.sourceServiceId, this.sourceDatasetId)
          }
        });
              
     
    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }
  }
}
