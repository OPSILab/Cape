import { Component, Input, TemplateRef, ViewChild } from '@angular/core';
import { NbDialogService } from '@nebular/theme';
import { Dataset } from '../../../model/service-linking/dataset';
import { TranslateService } from '@ngx-translate/core';
import { AvailableServiceRow } from './availableServices.component';

@Component({
  templateUrl: `./serviceInfoRender.component.html`,
})
export class ServiceInfoRenderComponent {
  @Input() value: AvailableServiceRow;

  @ViewChild('availableServiceInfoModal', { static: true }) serviceInfoModalRef: TemplateRef<unknown>;

  constructor(private modalService: NbDialogService, private translateService: TranslateService) {}

  showServiceInfoModal(): void {
    this.modalService.open(this.serviceInfoModalRef, {
      context: {
        modalHeader: this.value.name,
        description: this.value.humanReadableDescription[0]?.description,
        keywords: this.value.humanReadableDescription[0]?.keywords,
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

  mapDatasetsConcept(datasets: Array<Dataset>): Map<string, string[]> {
    return datasets.reduce(
      (map, dataset) =>
        map.set(
          dataset.datasetId,
          dataset.dataMapping.map(
            (concept) =>
              concept.name + (concept.required ? '' : ` (${this.translateService.instant('general.services.data_concept_optional') as string})`)
          )
        ),
      new Map<string, string[]>()
    );
  }
}
