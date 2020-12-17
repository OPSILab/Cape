import { Component, Input, OnInit } from '@angular/core';
import { ServiceInfoModalComponent } from './serviceInfo-modal/serviceInfo-modal.component';
import { NbDialogService } from '@nebular/theme';
import { IsDescribedAt } from '../../../model/service-linking/isDescribedAt';
import { TranslateService } from '@ngx-translate/core';
import { ServiceEntry } from '../../../model/service-linking/serviceEntry';
import { HumanReadableDescription } from '../../../model/humanReadableDescription';

interface Row {
  serviceId: string;
  serviceName: string;
  humanReadableDescription: HumanReadableDescription;
  serviceDescription: ServiceEntry;
  locale: string;
}

@Component({
  template: `
    <button nbButton ghost shape="rectangle" size="small" status="primary"
      (click)="showServiceInfoModal()"><i class="material-icons">info</i></button>
  `,
})
export class ServiceInfoRenderComponent implements OnInit {


  @Input() value: Row;

  constructor(private modalService: NbDialogService, private translateService: TranslateService) { }

  ngOnInit() {
  }


  showServiceInfoModal() {


    this.modalService.open(ServiceInfoModalComponent, {
      context: {
        modalHeader: this.value.serviceDescription.name,
        description: this.value.humanReadableDescription.description,
        keywords: this.value.humanReadableDescription.keywords,
        serviceUri: this.value.serviceDescription.identifier,
        iconUrl: (this.value.serviceDescription.serviceIconUrl !== '') ?
          this.value.serviceDescription.serviceIconUrl : 'assets/images/app/no_image.png',
        provider: this.value.serviceDescription.serviceInstance.serviceProvider.name,
        processings: this.value.serviceDescription.processingBases,
        data: this.mapDatasetsConcept(this.value.serviceDescription.isDescribedAt),
        locale: this.value.locale
      }, hasScroll: true
    });


  }

  mapDatasetsConcept(datasets: Array<IsDescribedAt>) {

    return datasets.reduce(
      (map, dataset) => map.set(dataset.datasetId,
        dataset.dataMapping.map(concept =>
          concept.name + (concept.required ? '' : ` (${this.translateService.instant('general.services.data_concept_optional')})`))),
      new Map<string, string[]>());
  }

}
