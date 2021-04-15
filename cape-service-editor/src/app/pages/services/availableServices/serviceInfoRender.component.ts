import { Component, Input, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { NbDialogService } from '@nebular/theme';
import { IsDescribedAt } from '../../../model/service-linking/isDescribedAt';
import { TranslateService } from '@ngx-translate/core';
import { ServiceEntry } from '../../../model/service-linking/serviceEntry';
import { HumanReadableDescription } from '../../../model/humanReadableDescription';
import { AvailableServiceRow } from './availableServices.component';

interface Row {
  serviceId: string;
  serviceName: string;
  humanReadableDescription: HumanReadableDescription;
  serviceDescription: ServiceEntry;
  locale: string;
}

@Component({
  templateUrl: `./serviceInfoRender.component.html`,
})
export class ServiceInfoRenderComponent implements OnInit {
  @Input() value: AvailableServiceRow;

  @ViewChild('availableServiceInfoModal', { static: true }) serviceInfoModalRef: TemplateRef<unknown>;

  constructor(private modalService: NbDialogService, private translateService: TranslateService) {}

  ngOnInit() {}

  showServiceInfoModal() {
    this.modalService.open(this.serviceInfoModalRef, {
      context: {
        modalHeader: this.value.name,
        description: this.value.humanReadableDescription[0]?.description,
        keywords: this.value.humanReadableDescription[0]?.keywords,
        serviceId: this.value.serviceId,
        serviceUri: this.value.identifier,
        iconUrl: this.value.serviceIconUrl !== '' ? this.value.serviceIconUrl : 'favicon.png',
        provider: this.value.serviceInstance.serviceProvider.name,
        processings: this.value.processingBases,
        datasetsMap: this.mapDatasetsConcept(this.value.isDescribedAt),
        locale: this.value.locale,
        dataController: this.value.serviceInstance.dataController,
      },
      hasScroll: true,
    });
  }

  mapDatasetsConcept(datasets: Array<IsDescribedAt>) {
    return datasets.reduce(
      (map, dataset) =>
        map.set(
          dataset.datasetId,
          dataset.dataMapping.map(
            (concept) => concept.name + (concept.required ? '' : ` (${this.translateService.instant('general.services.data_concept_optional')})`)
          )
        ),
      new Map<string, string[]>()
    );
  }
}
