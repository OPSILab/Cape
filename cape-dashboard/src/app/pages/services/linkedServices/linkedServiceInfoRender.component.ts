
import { Component, Input, OnInit } from '@angular/core';
import { NbDialogService } from '@nebular/theme';

import { LinkedServiceInfoModalComponent } from './linkedServiceInfo-modal/linkedServiceInfo-modal.component';
import { AvailableServicesService } from '../availableServices/availableServices.service';
import { ServiceEntry } from '../../../model/service-linking/serviceEntry';
import { IsDescribedAt } from '../../../model/service-linking/isDescribedAt';
import { Row } from './linkedServices.component';
import { TranslateService } from '@ngx-translate/core';



@Component({
  template: `
    <button nbButton ghost shape="round" size="small" status="primary"
      (click)="showServiceInfoModal()"><i class="material-icons">info</i></button>
  `
})
export class LinkedServiceInfoRenderComponent implements OnInit {

  public serviceInfo;

  @Input() value: Row;


  constructor(private modalService: NbDialogService, private availableService: AvailableServicesService,
    private translateService: TranslateService) {
  }

  ngOnInit() {
  }

  async showServiceInfoModal() {

    const serviceDescription: ServiceEntry = await this.availableService.getService(this.value.serviceId);

    /* Get Localized Human readable description of the Service, default en */
    let localizedHumanReadableDescription = serviceDescription.humanReadableDescription.filter(d => d.locale === this.value.locale)[0];
    if (!localizedHumanReadableDescription)
      localizedHumanReadableDescription = serviceDescription.humanReadableDescription.filter(d => d.locale === 'en')[0];

    /* Get Localized Purposes descriptions, default en */
    serviceDescription.processingBases = serviceDescription.processingBases.map(b => {
      const firstMatch = b.description.filter(d =>
        d.locale === this.value.locale);

      b.description = firstMatch.length > 0 ? firstMatch : b.description.filter(d => d.locale === 'en');
      return b;
    });

    this.modalService.open(LinkedServiceInfoModalComponent, {
      context: {
        modalHeader: this.value.serviceName,
        created: this.value.created,
        serviceUri: this.value.serviceUri,
        slrId: this.value.slrId,
        serviceId: serviceDescription.serviceId,
        description: localizedHumanReadableDescription.description,
        iconUrl: (serviceDescription.serviceIconUrl !== '') ?
          serviceDescription.serviceIconUrl : 'assets/images/app/no_image.png',
        keywords: localizedHumanReadableDescription.keywords,
        provider: serviceDescription.serviceInstance.serviceProvider.name,
        processings: serviceDescription.processingBases,
        data: this.mapDatasetsConcept(serviceDescription.isDescribedAt)
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
