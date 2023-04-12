import { AfterViewInit, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ConsentForm } from 'cape-sdk-angular';
import { FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { NbDialogRef, NbToastrService, NbGlobalLogicalPosition } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { CapeSdkAngularService, ConsentRecordEvent } from 'cape-sdk-angular';
import { CapeSdkDialogService } from 'cape-sdk-angular';

@Component({
  selector: 'lib-app-consent-form',
  templateUrl: './consent-form.component.html',
  styleUrls: ['./consent-form.component.scss'],
})
export class ConsentFormComponent2 implements OnInit {
  consentForm: ConsentForm;
  userConsentForm: FormGroup;
  sdkUrl: string;
  locale: string; 
  showAdditionalOptions = false;

  constructor(
    public dialogRef: NbDialogRef<ConsentFormComponent2>,
    private fb: FormBuilder,
    private capeService: CapeSdkAngularService,
    private toastrService: NbToastrService,
    private translateService: TranslateService,
    private errorDialogService: CapeSdkDialogService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.userConsentForm = this.fb.group({
      shareWith: new FormGroup(
        Object.fromEntries(
          this.consentForm.usage_rules.shareWith.map((shareWith) => [
            shareWith.orgName,
            new FormControl({ value: true, disabled: shareWith.required }),
          ])
        )
      ),
      collectionOperatorId: new FormControl(''),
    });

    if (this.consentForm.resource_set.datasets[0] !== undefined)
      this.userConsentForm.addControl(
        'dataMapping',
        new FormGroup(
          Object.fromEntries(
            this.consentForm.resource_set.datasets[0].dataMappings.map((concept) => [
              concept.name,
              new FormControl({ value: concept.required, disabled: concept.required }),
            ])
          )
        )
      );

    this.translateService.use(this.locale);
    this.cdr.detectChanges();
  }

  async giveConsent(): Promise<void> {
    if (this.consentForm.resource_set.datasets[0] !== undefined) {
      const dataMappingControls = new Map(
        Object.entries((this.userConsentForm.controls.dataMapping as FormGroup).controls).map((obj) => [obj[0], obj[1]])
      );
      this.consentForm.resource_set.datasets[0].dataMappings = this.consentForm.resource_set.datasets[0].dataMappings.filter(
        (concept) => dataMappingControls.get(concept.name)?.value
      );
    }

    const shareWithControls = new Map(Object.entries((this.userConsentForm.controls.shareWith as FormGroup).controls).map((obj) => [obj[0], obj[1]]));

    this.consentForm.usage_rules.shareWith = this.consentForm.usage_rules.shareWith.filter(
      (shareWith) => shareWithControls.get(shareWith.orgName)?.value
    );

    // Set in the consentForm (if any) collection operator Id from input control
    this.consentForm.collection_operator_id = (this.userConsentForm.controls.collectionOperatorId as FormControl).value as string;
    try {
      const newConsentRecordSigned = await this.capeService.giveConsent(this.sdkUrl, this.consentForm);
      this.dialogRef.close(true);
      this.toastrService.primary('', this.translateService.instant('general.consent.giveConsentSuccessful'), {
        position: NbGlobalLogicalPosition.BOTTOM_END,
        duration: 3500,
      });
      this.capeService.emitConsentRecordEvent({
        crId: newConsentRecordSigned.payload.common_part.cr_id,
        serviceId: newConsentRecordSigned.payload.common_part.subject_id,
        status: newConsentRecordSigned.consentStatusList.pop().payload,
        consentRecord: newConsentRecordSigned,
      } as ConsentRecordEvent);
    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }
  }
}
