import { Component, Input, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { NbDialogService } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { ConsentRecordSigned } from '../../model/consents/consentRecordSigned';
import { ConsentRecordSinkRoleSpecificPart } from '../../model/consents/consentRecordSinkRoleSpecificPart';

@Component({
  templateUrl: 'consentInfoRender.component.html',
})
export class ConsentInfoLinkRenderComponent implements OnInit {
  @Input() value: ConsentRecordSigned;

  @ViewChild('consentInfoModal', { static: true }) consentInfoModalRef: TemplateRef<unknown>;

  constructor(private modalService: NbDialogService, private translateService: TranslateService) {}

  ngOnInit() {}

  showConsentInfoModal() {
    this.modalService.open(this.consentInfoModalRef, {
      context: {
        consent_record: JSON.stringify(this.value),
        cr_id: this.value.payload.common_part.cr_id,
        issued: this.value.payload.common_part.iat,
        status: this.value.payload.common_part.consent_status,
        service: this.value.payload.common_part.subject_name,
        source: this.value.payload.common_part.source_subject_name,
        surrogateId: this.value.payload.common_part.surrogate_id,
        purposeCat: (this.value.payload.role_specific_part as ConsentRecordSinkRoleSpecificPart).usage_rules.purposeCategory,
        processingCat: (this.value.payload.role_specific_part as ConsentRecordSinkRoleSpecificPart).usage_rules.processingCategories,
        concepts: this.getConcepts(
          this.value.payload.common_part.rs_description.resource_set.datasets[
            this.value.payload.common_part.rs_description.resource_set.datasets.length - 1
          ].dataMappings
        ),
        consentSignedToken: this.value.signature,
        notarizationStatus: 'n/a',
        notarizationLink: 'n/a',
        policy: (this.value.payload.role_specific_part as ConsentRecordSinkRoleSpecificPart).usage_rules.policy.policyRef,
        shareWith: (this.value.payload.role_specific_part as ConsentRecordSinkRoleSpecificPart).usage_rules.shareWith,
        storage: (this.value.payload.role_specific_part as ConsentRecordSinkRoleSpecificPart).usage_rules.storage,
        collectionMethod: (this.value.payload.role_specific_part as ConsentRecordSinkRoleSpecificPart).usage_rules.collectionMethod,
        termination: (this.value.payload.role_specific_part as ConsentRecordSinkRoleSpecificPart).usage_rules.termination,
        history: this.value.consentStatusList,
      },
      hasScroll: true,
    });
  }

  getConcepts(json) {
    var concepts = [];
    for (var i = 0; i < json.length; i++) {
      concepts.push(json[i].name);
    }

    console.log(concepts);
    return concepts;
  }
}
