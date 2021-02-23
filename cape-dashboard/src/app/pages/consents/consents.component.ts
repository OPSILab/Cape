import { Component, Input, OnInit, OnDestroy, ViewChild, TemplateRef, ChangeDetectorRef } from '@angular/core';
import { ConsentsService } from './consents.service';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { NgxConfigureService } from 'ngx-configure';
import { LoginService } from '../../login/login.service';
import { TranslateService } from '@ngx-translate/core';
import { trigger, state, style, animate, transition } from '@angular/animations';
import { NbDialogService, NbToastrService, NbGlobalLogicalPosition } from '@nebular/theme';
import { ErrorDialogService } from '../error-dialog/error-dialog.service';
import { ConsentRecordSignedPair } from '../../model/consents/consentRecordSignedPair';
import { Dataset } from '../../model/consents/dataset';
import { DataMapping } from '../../model/dataMapping';
import { ShareWith } from '../../model/shareWith';
import { ConsentRecordSinkRoleSpecificPart } from '../../model/consents/consentRecordSinkRoleSpecificPart';
import { HumanReadableDescription } from '../../model/humanReadableDescription';
import { ConsentRecordSigned } from '../../model/consents/consentRecordSigned';
import { ResourceSet } from '../../model/consents/resourceSet';
import { SinkUsageRules } from '../../model/consents/sinkUsageRules';
import { ServiceEntry } from '../../model/service-linking/serviceEntry';
import { FormGroup, FormControl } from '@angular/forms';
import { ProcessingBasisProcessingCategories } from '../../model/processingBasis';
import { LinkedServicesService } from '../services/linkedServices/linkedServices.service';
import { ConsentStatusEnum } from '../../model/consents/consentStatusRecordPayload';

@Component({
  selector: 'consent',
  styleUrls: ['./consents.component.scss'],
  templateUrl: './consents.component.html',
  animations: [
    trigger('expandCollapse', [
      state('open', style({ height: '100%', opacity: 1, display: 'block' })),
      state('closed', style({ height: 0, opacity: 0, display: 'none' })),
      transition('open => closed', [animate('0.1s')]),
      transition('closed => open', [animate('0.1s')]),
    ]),
  ],
})
export class ConsentsComponent implements OnInit, OnDestroy {
  public isCollapsed: boolean[];
  public changedDataMapping: boolean[];
  public changedShareWith: boolean[];
  private lastClickedIndex: number;
  private lastClickedDataMapping: Map<number, DataMapping[]> = new Map<number, DataMapping[]>();
  private lastClickedShareWith: Map<number, ShareWith[]> = new Map<number, ShareWith[]>();
  public consents: ConsentRecordSignedPair[];
  public services: ServiceEntry[];

  @ViewChild('consentUpdateConflict')
  private consentUpdateConflict: TemplateRef<unknown>;

  @Input() loading = true;
  private processingCategoryEnum = ProcessingBasisProcessingCategories;
  public processingCategoryOptions;

  private changeStatusButtons;
  public filtersForm = new FormGroup({
    service: new FormControl(),
    status: new FormControl(),
    purposeCategory: new FormControl(),
    processingCategory: new FormControl(),
  });

  currentLocale: string;
  public message = '';

  constructor(
    private route: ActivatedRoute,
    private service: ConsentsService,
    private linkedServicesService: LinkedServicesService,
    private router: Router,
    private translate: TranslateService,
    private configService: NgxConfigureService,
    private loginService: LoginService,
    private modalService: NbDialogService,
    private toastrService: NbToastrService,
    private errorDialogService: ErrorDialogService,
    private changeDetector: ChangeDetectorRef
  ) {
    this.currentLocale = this.translate.currentLang;
    this.processingCategoryOptions = Object.keys(this.processingCategoryEnum);

    this.changeStatusButtons = [
      {
        id: 1,
        name: this.translate.instant('general.consents.activate') as string,
        color: 'success',
      },
      {
        id: 2,
        name: this.translate.instant('general.consents.disable') as string,
        color: 'warning',
      },
      {
        id: 3,
        name: this.translate.instant('general.consents.withdraw') as string,
        color: 'danger',
      },
    ];
  }

  async ngOnInit(): Promise<void> {
    const queryParams = this.route.snapshot.queryParams;

    await this.getConsents(queryParams.consentId, queryParams.serviceId);
    await this.getServices();
  }

  ngOnDestroy(): void {
    console.log('ngOnDestroy');
  }

  async getConsents(
    consentId?: string,
    serviceId?: string,
    status?: ConsentStatusEnum,
    purposeCategory?: string,
    processingCategory?: ProcessingBasisProcessingCategories
  ): Promise<void> {
    this.isCollapsed = [];
    this.changedDataMapping = [];
    this.changedShareWith = [];
    this.lastClickedIndex = -1;

    try {
      this.loading = true;
      this.message = '';

      this.consents = await this.service.getConsentPairs(consentId, serviceId, status, purposeCategory, processingCategory);

      if (this.consents.length === 0) this.message = this.translate.instant('general.consents.no_consent_message') as string;
      else
        this.consents.forEach(() => {
          this.isCollapsed.push(true);
          this.changedDataMapping.push(false);
          this.changedShareWith.push(false);
        });

      this.loading = false;
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error?.error?.statusCode === '401') {
        this.loginService.logout().catch((error) => this.errorDialogService.openErrorDialog(error));
      } else this.errorDialogService.openErrorDialog(error);
    }
  }

  async getServices(): Promise<void> {
    this.services = await this.service.getServices();
  }

  getNowDateTime(): string {
    const dateTime = new Date();
    return `${dateTime.getFullYear()}-${
      dateTime.getMonth() + 1
    }-${dateTime.getDate()} ${dateTime.getHours()}:${dateTime.getMinutes()}:${dateTime.getSeconds()}`;
  }

  getLocalizedHumanReadableDescription(descriptions: HumanReadableDescription[], locale?: string): string {
    return descriptions.find((d) => d.locale === (locale ? locale : this.currentLocale))?.description;
  }

  /* ****************************
   *  Consent Status and Update
   *
   * ****************************/

  isActiveConsent(consentIndex: number): boolean {
    return (
      this.consents[consentIndex].sink.consentStatusList[this.consents[consentIndex].sink.consentStatusList.length - 1].payload.consent_status ===
      ConsentStatusEnum.Active
    );
  }

  onChangeStatusClick(consentIndex: number, buttonId: number): void {
    let newStatus: ConsentStatusEnum;
    switch (true) {
      case buttonId === 1: {
        newStatus = ConsentStatusEnum.Active;
        break;
      }
      case buttonId === 2: {
        newStatus = ConsentStatusEnum.Disabled;
        break;
      }
      case buttonId === 3: {
        newStatus = ConsentStatusEnum.Withdrawn;
        break;
      }
      // The newStatus is the old one
      default: {
        newStatus = this.consents[consentIndex].sink.consentStatusList[this.consents[consentIndex].sink.consentStatusList.length - 1].payload
          .consent_status;
        break;
      }
    }
    void this.updateConsent(consentIndex, newStatus);
  }

  isActiveButton(consentIndex: number, buttonId: number): boolean {
    const consentPair: ConsentRecordSignedPair = this.consents[consentIndex];
    const status: ConsentStatusEnum = consentPair.sink.consentStatusList[consentPair.sink.consentStatusList.length - 1].payload.consent_status;

    return (
      this.changedDataMapping[consentIndex] === false &&
      this.changedShareWith[consentIndex] === false &&
      ((buttonId === 1 && status === ConsentStatusEnum.Disabled) ||
        (buttonId === 2 && status === ConsentStatusEnum.Active) ||
        (buttonId === 3 && (status === ConsentStatusEnum.Active || status === ConsentStatusEnum.Disabled)))
    );
  }

  async updateConsent(consentIndex: number, newStatus?: ConsentStatusEnum): Promise<void> {
    const consent: ConsentRecordSigned = this.consents[consentIndex].sink;

    // Put as new status the one in input, if any, otherwise undefined, indicating that we are not updating the ConsentStatus
    const status: ConsentStatusEnum = newStatus;

    // TODO Change if we will use more datasets per resource set
    const lastDataset: Dataset = consent.payload.common_part.rs_description.resource_set.datasets[0];

    const lastUsageRules: SinkUsageRules = (consent.payload.role_specific_part as ConsentRecordSinkRoleSpecificPart).usage_rules;

    if (this.changedDataMapping[consentIndex])
      lastDataset.dataMappings = this.lastClickedDataMapping.get(consentIndex).reduce((result: DataMapping[], concept: DataMapping) => {
        if (concept.current || concept.required) {
          const conceptAux = { ...concept };
          delete conceptAux.current;
          delete conceptAux.initial;
          result.push(conceptAux);
        }
        return result;
      }, []);

    if (this.changedShareWith[consentIndex])
      lastUsageRules.shareWith = this.lastClickedShareWith.get(consentIndex).reduce((result: ShareWith[], shareWith: ShareWith) => {
        if (shareWith.current || shareWith.required) {
          const shareWithAux = { ...shareWith };
          delete shareWithAux.current;
          delete shareWithAux.initial;
          result.push(shareWithAux);
        }
        return result;
      }, []);

    const newResourceSet: ResourceSet = {
      rs_id: consent.payload.common_part.rs_description.resource_set.rs_id,
      datasets: [lastDataset],
    } as ResourceSet;

    try {
      const updatedStatus = await this.service.updateConsentStatus(
        consent.payload.common_part.slr_id,
        consent.payload.common_part.cr_id,
        newResourceSet,
        status,
        lastUsageRules
      );
      this.toastrService.primary('', this.translate.instant('general.consents.consent_updated_message'), {
        position: NbGlobalLogicalPosition.BOTTOM_END,
        duration: 4500,
      });

      consent.consentStatusList.push(updatedStatus);
      this.changedDataMapping[consentIndex] = false;
      this.changedShareWith[consentIndex] = false;
      void this.openDetails(consentIndex, false);

      this.changeDetector.detectChanges();
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error.error?.statusCode === '401') {
        this.loginService.logout().catch((error) => this.errorDialogService.openErrorDialog(error));
        // this.router.navigate(['/login']);
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      } else if (error.status === 409) {
        this.openConsentUpdateConflictDialog(
          error,
          this.consentUpdateConflict,
          consentIndex,
          consent.payload.common_part.subject_id,
          consent.payload.common_part.slr_id
        );
      } else this.errorDialogService.openErrorDialog(error);
    }
  }

  openConsentUpdateConflictDialog(
    error: unknown,
    consentUpdateConflict: TemplateRef<unknown>,
    consentIndex: number,
    serviceId: string,
    slrId: string
  ): void {
    this.modalService.open(consentUpdateConflict, {
      context: {
        error: error,
        consentIndex: consentIndex,
        serviceId: serviceId,
        slrId: slrId,
      },
      hasScroll: false,
      closeOnBackdropClick: false,
      closeOnEsc: false,
    });
  }

  async forceServiceLinkAndConsentActivation(consentIndex: number, serviceId: string, slrId: string): Promise<void> {
    try {
      // Activate the Service Link relative to the Consent (this will reactivate also the related Consent)
      await this.linkedServicesService.enableServiceLink(serviceId, slrId);

      this.toastrService.primary('', this.translate.instant('general.consents.consent_updated_message'), {
        position: NbGlobalLogicalPosition.BOTTOM_END,
        duration: 4500,
      });

      await this.getConsents();
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error.error?.statusCode === '401') {
        this.loginService.logout().catch((error) => this.errorDialogService.openErrorDialog(error));
        // this.router.navigate(['/login']);
      } else this.errorDialogService.openErrorDialog(error);
    }
  }

  /*********************************************
   * Datasets and Share With changes management
   *********************************************/

  async getConsentDataMapping(consentIndex: number): Promise<DataMapping[]> {
    const consent: ConsentRecordSignedPair = this.consents[consentIndex];
    const consentStatusLength = consent.sink.consentStatusList.length;

    // TODO Change if we will use more datasets per resource set
    // Get the current Dataset either from common part or from consents status list if we have statuses > 1
    const consentDataset: Dataset =
      consentStatusLength > 1
        ? consent.sink.consentStatusList[consentStatusLength - 1].payload.consent_resource_set.datasets[0]
        : consent.sink.payload.common_part.rs_description.resource_set.datasets[0];

    const serviceId: string = consent.sink.payload.common_part.subject_id;
    const purposeId: string = consentDataset.purposeId;
    const sourceServiceId = consent.source?.payload.common_part.subject_id;
    const sourceDatasetId = consentDataset.dataset_id;

    const matchingDataMappings: DataMapping[] = await this.service.getMatchingDatasets(serviceId, purposeId, sourceDatasetId, sourceServiceId);

    const matchingDataMappingMap: Map<string, DataMapping> = new Map(matchingDataMappings.map((dataMapping) => [dataMapping.conceptId, dataMapping]));
    const consentDataMappingMap: Map<string, DataMapping> = new Map(
      consentDataset.dataMappings.map((dataMapping) => [dataMapping.conceptId, dataMapping])
    );

    for (const [conceptId, dataMapping] of matchingDataMappingMap) {
      dataMapping.initial = consentDataMappingMap.has(conceptId);
      dataMapping.current = dataMapping.initial;
    }

    return Promise.resolve(Array.from(matchingDataMappingMap.values()));
  }

  changeDataMapping(newValue: boolean, consentIndex: number, conceptIndex: number): void {
    if (consentIndex === this.lastClickedIndex) {
      const clickedConcept = this.lastClickedDataMapping.get(consentIndex)[conceptIndex];
      clickedConcept.current = newValue;

      this.changedDataMapping[consentIndex] =
        this.lastClickedDataMapping.get(consentIndex).reduce((count, concept) => {
          if (concept.initial !== concept.current) count++;
          return count;
        }, 0) > 0;
    }
  }

  async getShareWithByConsentIndex(consentIndex: number): Promise<ShareWith[]> {
    const consent: ConsentRecordSignedPair = this.consents[consentIndex];
    const consentStatusLength = consent.sink.consentStatusList.length;

    // TODO Change if we will use more datasets per resource set
    // Get the current Dataset either from common part or from consents status list if we have statuses > 1
    const consentDataset: Dataset =
      consentStatusLength > 1
        ? consent.sink.consentStatusList[consentStatusLength - 1].payload.consent_resource_set.datasets[0]
        : consent.sink.payload.common_part.rs_description.resource_set.datasets[0];
    const consentShareWith: ShareWith[] =
      consentStatusLength > 1
        ? consent.sink.consentStatusList[consentStatusLength - 1].payload.consent_usage_rules.shareWith
        : (consent.sink.payload.role_specific_part as ConsentRecordSinkRoleSpecificPart).usage_rules.shareWith;

    const purposeId: string = consentDataset.purposeId;
    const sinkShareWith: ShareWith[] = (await this.service.getServiceProcessingBasis(consent.sink.payload.common_part.subject_id, purposeId))
      .shareWith;

    const consentShareWithMap: Map<string, ShareWith> = new Map(consentShareWith.map((consentShare) => [consentShare.orgName, consentShare]));
    const sinkShareWithMap: Map<string, ShareWith> = new Map(sinkShareWith.map((sinkShare) => [sinkShare.orgName, sinkShare]));

    for (const [orgName, share] of sinkShareWithMap) {
      share.initial = consentShareWithMap.has(orgName);
      share.current = share.initial;
    }

    return Promise.resolve(Array.from(sinkShareWithMap.values()));
  }

  changeShareWith(newValue: boolean, consentIndex: number, shareWithIndex: number): void {
    if (consentIndex === this.lastClickedIndex) {
      const clickedShareWith = this.lastClickedShareWith.get(consentIndex)[shareWithIndex];
      clickedShareWith.current = newValue;

      this.changedShareWith[consentIndex] =
        this.lastClickedShareWith.get(consentIndex).reduce((count, shareWith) => {
          if (shareWith.initial !== shareWith.current) count++;
          return count;
        }, 0) > 0;
    }
  }

  /**************************
   * HISTORY AND DETAILS
   ***************************/

  async openDetails(consentIndex: number, changeCollapse = true): Promise<void> {
    this.lastClickedIndex = consentIndex;
    this.lastClickedDataMapping.set(consentIndex, await this.getConsentDataMapping(consentIndex));
    this.lastClickedShareWith.set(consentIndex, await this.getShareWithByConsentIndex(consentIndex));

    // If the currently clicked Consent is not the last clicked one
    if (consentIndex !== this.lastClickedIndex) {
      // Collapse the last opened Consent details
      this.isCollapsed[this.lastClickedIndex] = true;

      // Set the currently clicked Consent as the last one and load its DataMapping and ShareWith
      this.lastClickedIndex = consentIndex;
      this.lastClickedDataMapping.set(consentIndex, await this.getConsentDataMapping(consentIndex));
      this.lastClickedShareWith.set(consentIndex, await this.getShareWithByConsentIndex(consentIndex));
    }

    if (changeCollapse) this.isCollapsed[consentIndex] = !this.isCollapsed[consentIndex];
  }

  openConsentHistory(consentHistory: TemplateRef<unknown>, consentIndex: number): void {
    this.modalService.open(consentHistory, {
      context: {
        consent: this.consents[consentIndex],
      },
      hasScroll: false,
    });
  }

  openDatasetHistory(datasetHistory: TemplateRef<unknown>, consentIndex: number): void {
    this.modalService.open(datasetHistory, {
      context: {
        consent: this.consents[consentIndex],
      },
      hasScroll: false,
    });
  }

  /**************************
   * FILTERING
   ***************************/

  async onFilterSubmit(): Promise<void> {
    await this.getConsents(
      undefined,
      this.filtersForm.get('service').value,
      this.filtersForm.get('status').value,
      this.filtersForm.get('purposeCategory').value,
      this.filtersForm.get('processingCategory').value
    );
  }

  resetFilters(): void {
    this.filtersForm.reset({
      service: '',
      status: '',
      purposeCategory: '',
      processingCategory: '',
    });

    void this.getConsents();
  }

  resetFilter(controlName: string): void {
    this.filtersForm.get(controlName)?.setValue('');
  }
}
