import { Component, Input, TemplateRef, ViewChild } from '@angular/core';
import { NbDialogRef, NbDialogService } from '@nebular/theme';

import { AvailableServicesService } from '../availableServices/availableServices.service';
import { ServiceEntry } from '../../../model/service-linking/serviceEntry';
import { Dataset } from '../../../model/service-linking/Dataset';
import { TranslateService } from '@ngx-translate/core';
import { LinkedServiceRow } from './linkedServices.component';
import { ProcessingBasis } from '../../../model/processingBasis';
import { Router } from '@angular/router';
import { TextualDescription2 } from '../../../model/service-linking/textualDescription2';
import { TextualDescription3 } from '../../../model/service-linking/textualDescription3';

@Component({
  templateUrl: './linkedServiceInfoRender.component.html',
})
export class LinkedServiceInfoRenderComponent {
  @Input() value: LinkedServiceRow;
  @ViewChild('linkedServiceInfoModal', { static: true }) linkedServiceInfoModalRef: TemplateRef<unknown>;

  private modalRef: NbDialogRef<unknown>;

  constructor(
    private modalService: NbDialogService,
    private availableService: AvailableServicesService,
    private translateService: TranslateService,
    private router: Router
  ) {}

  async showServiceInfoModal(): Promise<void> {
    const serviceDescription: ServiceEntry = await this.availableService.getService(this.value.service_id);

    /* Get Localized Human readable description of the Service, default en */
    const localizedHumanReadableDescription = this.getLocalizedDescription(serviceDescription.humanReadableDescription);
    /* Get Localized Purposes descriptions, default en */
    serviceDescription.processingBases = this.getLocalizedPurposesDescription(serviceDescription.processingBases);

    this.modalRef = this.modalService.open(this.linkedServiceInfoModalRef, {
      context: {
        modalHeader: this.value.service_name,
        created: this.value.created,
        serviceUri: this.value.service_uri,
        slrId: this.value.link_id,
        serviceId: serviceDescription.serviceId,
        description: localizedHumanReadableDescription[0]?.description,
        iconUrl: serviceDescription.serviceIconUrl !== '' ? serviceDescription.serviceIconUrl : 'assets/images/app/no_image.png',
        keywords: localizedHumanReadableDescription[0]?.keywords,
        provider: serviceDescription.serviceInstance.serviceProvider.name,
        processings: serviceDescription.processingBases,
        datasetsMap: this.mapDatasetsConcept(serviceDescription.isDescribedAt),
        dataController: serviceDescription.serviceInstance.dataController,
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

  openConsents(): void {
    void this.router.navigate(['/pages/consents', { serviceId: this.value.service_id, serviceName: this.value.service_name }]);
    this.modalRef.close();
  }

  private getLocalizedPurposesDescription(linkedServicePurpose: ProcessingBasis[]): ProcessingBasis[] {
    return linkedServicePurpose.map((processingBase) => {
      return {
        ...processingBase,
        description: processingBase.description.reduce((filtered: TextualDescription2[], description: TextualDescription2) => {
          if (this.value.locale !== 'en' && description.locale === this.value.locale) filtered = [description, ...filtered];
          else if (description.locale === 'en') filtered = [...filtered, description];
          return filtered;
        }, []),
      };
    });
  }

  private getLocalizedDescription(availableServiceDescr: TextualDescription3[]): TextualDescription3[] {
    return availableServiceDescr.reduce((filtered: TextualDescription3[], description: TextualDescription3) => {
      if (this.value.locale !== 'en' && description.locale === this.value.locale) filtered = [description, ...filtered];
      else if (description.locale === 'en') filtered = [...filtered, description];
      return filtered;
    }, []);
  }
}
