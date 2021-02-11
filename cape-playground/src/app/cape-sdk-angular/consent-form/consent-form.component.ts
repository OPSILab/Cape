import { Component, OnInit } from '@angular/core';
import { ConsentForm } from '../model/consent/consentForm';
import { FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { NbDialogRef, NbToastrService, NbGlobalLogicalPosition } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { CapeSdkAngularService, ConsentRecordEvent } from '../cape-sdk-angular.service';
import { ErrorDialogService } from '../error-dialog/error-dialog.service';
import { ConsentStatusEnum } from '../model/consent/consentStatusRecordPayload';

@Component({
  selector: 'app-consent-form',
  templateUrl: './consent-form.component.html',
  styleUrls: ['./consent-form.component.scss']
})
export class ConsentFormComponent implements OnInit {

  consentForm: ConsentForm;
  userConsentForm: FormGroup;
  sdkUrl: string;

  constructor(public dialogRef: NbDialogRef<ConsentFormComponent>, private fb: FormBuilder,
    private capeService: CapeSdkAngularService, private toastrService: NbToastrService,
    private translateService: TranslateService, private errorDialogService: ErrorDialogService) { }

  ngOnInit(): void {

    this.userConsentForm = this.fb.group({
      dataMapping: new FormGroup(Object.fromEntries(this.consentForm.resource_set.datasets[0].dataMappings.map(concept =>
        [concept.name, new FormControl({ value: true, disabled: concept.required })]))),
      shareWith: new FormGroup(Object.fromEntries(this.consentForm.usage_rules.shareWith.map(shareWith => [shareWith.orgName, new FormControl({ value: true, disabled: shareWith.required })])))
    });

  }


  async giveConsent() {

    const dataMappingControls = new Map(Object.entries((this.userConsentForm.controls.dataMapping as FormGroup).controls).map(obj => [obj[0], obj[1]]));
    const shareWithControls = new Map(Object.entries((this.userConsentForm.controls.shareWith as FormGroup).controls).map(obj => [obj[0], obj[1]]));

    this.consentForm.resource_set.datasets[0].dataMappings = this.consentForm.resource_set.datasets[0].dataMappings.filter(concept => dataMappingControls.get(concept.name)?.value);
    this.consentForm.usage_rules.shareWith = this.consentForm.usage_rules.shareWith.filter(shareWith => shareWithControls.get(shareWith.orgName)?.value);

    try {

      const newConsentRecordSigned = await this.capeService.giveConsent(this.sdkUrl, this.consentForm);
      this.toastrService.primary('', this.translateService.instant('general.consent.giveConsentSuccessful'), { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 3500 });
      this.capeService.emitConsentRecordEvent({
        crId: newConsentRecordSigned.payload.common_part.cr_id,
        serviceId: newConsentRecordSigned.payload.common_part.subject_id,
        status: newConsentRecordSigned.consentStatusList.pop().payload,
        consentRecord: newConsentRecordSigned
      } as ConsentRecordEvent);

    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }
  }

}
