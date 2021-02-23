import { Injectable } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ConsentRecordSigned } from '../../model/consents/consentRecordSigned';
import { ConsentRecordSignedPair } from '../../model/consents/consentRecordSignedPair';
import { ServiceLinkRecordDoubleSigned } from '../../model/service-linking/serviceLinkRecordDoubleSigned';
import { ResourceSet } from '../../model/consents/resourceSet';
import { ConsentStatusEnum } from '../../model/consents/consentStatusRecordPayload';
import { SinkUsageRules } from '../../model/consents/sinkUsageRules';
import { ConsentStatusRecordSigned } from '../../model/consents/consentStatusRecordSigned';
import { ChangeConsentStatusRequest } from '../../model/consents/changeConsentStatusRequest';
import { DataMapping } from '../../model/dataMapping';
import { ProcessingBasis, ProcessingBasisProcessingCategories } from '../../model/processingBasis';
import { AvailableServicesService } from '../services/availableServices/availableServices.service';
import { ServiceEntry } from '../../model/service-linking/serviceEntry';
import { ChangeConsentStatusRequestFrom } from '../../model/consents/changeSlrStatusRequestFrom';

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

  getConsents(): Promise<ConsentRecordSigned[]> {
    return this.http.get<ConsentRecordSigned[]>(`${this.consentsApiPath}/accounts/${this.accountId}/consents`).toPromise();
  }

  getConsentPairs(
    consentId?: string,
    serviceId?: string,
    status?: ConsentStatusEnum,
    purposeCategory?: string,
    processingCategory?: ProcessingBasisProcessingCategories
  ): Promise<ConsentRecordSignedPair[]> {
    let queryParams = new HttpParams();

    if (consentId) queryParams = queryParams.append('consentId', consentId);

    if (serviceId) queryParams = queryParams.append('serviceId', serviceId);

    if (status) queryParams = queryParams.append('status', status);

    if (purposeCategory) queryParams = queryParams.append('purposeCategory', purposeCategory);

    if (processingCategory) queryParams = queryParams.append('processingCategory', processingCategory);

    const httpOptions = {
      params: queryParams,
    };

    return this.http.get<ConsentRecordSignedPair[]>(`${this.consentsApiPath}/accounts/${this.accountId}/consents/pair`, httpOptions).toPromise();
  }

  getServiceLinks(): Promise<ServiceLinkRecordDoubleSigned[]> {
    return this.http.get<ServiceLinkRecordDoubleSigned[]>(`${this.accountManagerUrl}/${this.accountId}/serviceLinks`).toPromise();
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
  ): Promise<ConsentStatusRecordSigned> {
    const url = `${this.consentsApiPath}/accounts/${this.accountId}/servicelinks/${slrId}/consents/${crId}/statuses`;

    return this.http
      .post<ConsentStatusRecordSigned>(url, {
        resource_set: resourceSet,
        status: status,
        usage_rules: usageRules,
        request_from: ChangeConsentStatusRequestFrom.Operator,
      } as ChangeConsentStatusRequest)
      .toPromise();
  }

  getMatchingDatasets(serviceId: string, purposeId: string, sourceDatasetId?: string, sourceServiceId?: string): Promise<DataMapping[]> {
    let url = `${this.consentsApiPath}/service/${serviceId}/purpose/${purposeId}/matchingDataset`;
    if (sourceDatasetId && sourceServiceId) url = url.concat(`?sourceServiceId=${sourceServiceId}&sourceDatasetId=${sourceDatasetId}`);

    return this.http.get<DataMapping[]>(url).toPromise();
  }

  getServiceProcessingBasis(serviceId: string, purposeId: string): Promise<ProcessingBasis> {
    return this.http.get<ProcessingBasis>(`${this.serviceRegistryUrl}/services/${serviceId}/purposes/${purposeId}`).toPromise();
  }
}
