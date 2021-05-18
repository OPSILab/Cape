import { Injectable } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ConsentRecordSigned } from '../../model/consents/consentRecordSigned';
import { ConsentRecordSignedPair } from '../../model/consents/consentRecordSignedPair';
import { ServiceLinkRecordDoubleSigned } from '../../model/service-linking/serviceLinkRecordDoubleSigned';
import { ResourceSet } from '../../model/consents/resourceSet';
import { ConsentStatusEnum } from '../../model/consents/consentStatusRecordPayload';
import { SinkUsageRules } from '../../model/consents/sinkUsageRules';
import { ChangeConsentStatusRequest } from '../../model/consents/changeConsentStatusRequest';
import { DataMapping } from '../../model/dataMapping';
import { ProcessingBasis, ProcessingBasisProcessingCategories, ProcessingBasisPurposeCategory } from '../../model/processingBasis';
import { AvailableServicesService } from '../services/availableServices/availableServices.service';
import { ServiceEntry } from '../../model/service-linking/serviceEntry';
import { ChangeConsentStatusRequestFrom } from '../../model/consents/changeSlrStatusRequestFrom';
import { QuerySortEnum } from '../../model/querySortEnum';

@Injectable()
export class ConsentsService {
  private config;
  private consentsApiPath: string;
  private accountManagerUrl: string;
  private serviceRegistryUrl: string;
  private token = `Bearer ${localStorage.getItem('tokenData')}`;
  private accountId: string = localStorage.getItem('accountId');

  constructor(configService: NgxConfigureService, private http: HttpClient, private availableServices: AvailableServicesService) {
    this.config = configService.config as unknown;
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    this.consentsApiPath = this.config.system.consentManagerUrl as string;
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    this.accountManagerUrl = this.config.system.accountUrl as string;
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    this.serviceRegistryUrl = this.config.serviceRegistry.url as string;
  }

  getConsents(
    iatSort?: QuerySortEnum,
    consentId?: string,
    serviceId?: string,
    sourceServiceId?: string,
    datasetId?: string,
    status?: ConsentStatusEnum,
    purposeId?: string,
    purposeName?: string,
    purposeCategory?: ProcessingBasisPurposeCategory,
    processingCategories?: ProcessingBasisProcessingCategories[]
  ): Promise<ConsentRecordSigned[]> {
    let queryParams = new HttpParams();

    if (iatSort) queryParams = queryParams.set('iatSort', iatSort);

    if (consentId) queryParams = queryParams.set('consentId', consentId);

    if (serviceId) queryParams = queryParams.set('serviceId', serviceId);

    if (sourceServiceId) queryParams = queryParams.set('sourceServiceId', sourceServiceId);

    if (datasetId) queryParams = queryParams.set('datasetId', datasetId);

    if (status) queryParams = queryParams.set('status', status);

    if (purposeId) queryParams = queryParams.set('purposeId', purposeId);

    if (purposeName) queryParams = queryParams.set('purposeName', purposeName);

    if (purposeCategory) queryParams = queryParams.set('purposeCategory', purposeCategory);

    if (processingCategories)
      processingCategories.forEach((cat) => {
        queryParams = queryParams.append('processingCategories', cat);
      });

    const httpOptions = {
      params: queryParams,
    };
    return this.http.get<ConsentRecordSigned[]>(`${this.consentsApiPath}/api/v2/accounts/${this.accountId}/consents`, httpOptions).toPromise();
  }

  getConsentPairs(
    iatSort?: QuerySortEnum,
    consentId?: string,
    serviceId?: string,
    sourceServiceId?: string,
    datasetId?: string,
    status?: ConsentStatusEnum,
    purposeId?: string,
    purposeName?: string,
    purposeCategory?: ProcessingBasisPurposeCategory,
    processingCategories?: ProcessingBasisProcessingCategories[]
  ): Promise<ConsentRecordSignedPair[]> {
    let queryParams = new HttpParams();

    if (iatSort) queryParams = queryParams.set('iatSort', iatSort);

    if (consentId) queryParams = queryParams.set('consentId', consentId);

    if (serviceId) queryParams = queryParams.set('serviceId', serviceId);

    if (sourceServiceId) queryParams = queryParams.set('sourceServiceId', sourceServiceId);

    if (datasetId) queryParams = queryParams.set('datasetId', datasetId);

    if (status) queryParams = queryParams.set('status', status);

    if (purposeId) queryParams = queryParams.set('purposeId', purposeId);

    if (purposeName) queryParams = queryParams.set('purposeName', purposeName);

    if (purposeCategory) queryParams = queryParams.set('purposeCategory', purposeCategory);

    if (processingCategories)
      processingCategories.forEach((cat) => {
        queryParams = queryParams.append('processingCategories', cat);
      });

    const httpOptions = {
      params: queryParams,
    };
    return this.http
      .get<ConsentRecordSignedPair[]>(`${this.consentsApiPath}/api/v2/accounts/${this.accountId}/consents/pair`, httpOptions)
      .toPromise();
  }

  getServiceLinks(): Promise<ServiceLinkRecordDoubleSigned[]> {
    return this.http.get<ServiceLinkRecordDoubleSigned[]>(`${this.accountManagerUrl}/api/v2/accounts/${this.accountId}/serviceLinks`).toPromise();
  }

  getServices(): Promise<ServiceEntry[]> {
    return this.availableServices.getRegisteredServices();
  }

  updateConsentStatus(
    slrId: string,
    crId: string,
    resourceSet: ResourceSet,
    status: ConsentStatusEnum,
    usageRules: SinkUsageRules
  ): Promise<ConsentRecordSigned> {
    const url = `${this.consentsApiPath}/api/v2/accounts/${this.accountId}/servicelinks/${slrId}/consents/${crId}/statuses`;

    return this.http
      .post<ConsentRecordSigned>(url, {
        resource_set: resourceSet,
        status: status,
        usage_rules: usageRules,
        request_from: ChangeConsentStatusRequestFrom.Operator,
      } as ChangeConsentStatusRequest)
      .toPromise();
  }

  getMatchingDatasets(serviceId: string, purposeId: string, sourceDatasetId?: string, sourceServiceId?: string): Promise<DataMapping[]> {
    let url = `${this.consentsApiPath}/api/v2/services/${serviceId}/purposes/${purposeId}/matchingDatasets`;
    if (sourceDatasetId && sourceServiceId) url = url.concat(`?sourceServiceId=${sourceServiceId}&sourceDatasetId=${sourceDatasetId}`);

    return this.http.get<DataMapping[]>(url).toPromise();
  }

  getServiceProcessingBasis(serviceId: string, purposeId: string): Promise<ProcessingBasis> {
    return this.http.get<ProcessingBasis>(`${this.serviceRegistryUrl}/api/v2/services/${serviceId}/purposes/${purposeId}`).toPromise();
  }
}
