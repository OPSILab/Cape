import { Component, Input, OnInit } from '@angular/core';
import { ViewCell } from 'ng2-smart-table';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConsentInfoModalComponent } from './consentInfo-modal/consentInfo-modal.component';
import { NbDialogService } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';


@Component({
  template: 
  `
    <button nbButton ghost shape="rectangle" size="small" status="primary"
      (click)="showConsentInfoModal()"><i class="material-icons">info</i>
    </button>
  `
  ,
})
export class ConsentInfoLinkRenderComponent implements OnInit {

  public renderValue;

  @Input() value;

  constructor(private modalService: NbDialogService,  private translateService: TranslateService) { }

  ngOnInit() {
    this.renderValue = this.value;

  }


  showConsentInfoModal() {
    
   

    this.modalService.open(ConsentInfoModalComponent, {
      context: {
        consent_record: JSON.stringify(this.renderValue),
        cr_id: this.renderValue.viewInfo.payload.common_part.cr_id,
        issued: this.renderValue.viewInfo.payload.common_part.iat,
        status: this.renderValue.viewInfo.payload.common_part.consent_status,
        service: this.renderValue.viewInfo.payload.common_part.subject_name,
        source: this.renderValue.viewInfo.payload.common_part.source_name,
        userId: this.renderValue.viewInfo.payload.common_part.surrogate_id,
        purposeCat: this.renderValue.viewInfo.payload.role_specific_part.usage_rules.purposeCategory,
        processingCat: this.renderValue.viewInfo.payload.role_specific_part.usage_rules.processingCategories,
        concepts: this.getConcepts(this.renderValue.viewInfo.payload.common_part.rs_description.resource_set.datasets[this.renderValue.viewInfo.payload.common_part.rs_description.resource_set.datasets.length-1].dataMappings),
        consentSignedToken: this.renderValue.viewInfo.signature,
        notarizationStatus: "n/a",
        notarizationLink: "n/a",
        policy: this.renderValue.viewInfo.payload.role_specific_part.usage_rules.policy.policyRef,
        shareWith:this.renderValue.viewInfo.payload.role_specific_part.usage_rules.shareWith,
        storage: this.renderValue.viewInfo.payload.role_specific_part.usage_rules.storage,
        collectionMethod: this.renderValue.viewInfo.payload.role_specific_part.usage_rules.collectionMethod,
        termination: this.renderValue.viewInfo.payload.role_specific_part.usage_rules.termination,
        history: this.renderValue.viewInfo.consentStatusList
       
      }, hasScroll: true
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
