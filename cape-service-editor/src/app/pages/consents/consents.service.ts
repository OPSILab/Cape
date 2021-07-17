import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import 'rxjs/add/operator/toPromise';
import { NgxConfigureService } from 'ngx-configure';
import { ConsentStatusEnum } from '../../model/consents/consentStatusRecordPayload';
import { ProcessingBasisProcessingCategoriesEnum, ProcessingBasisPurposeCategoryEnum } from '../../model/processingBasis';
import { QuerySortEnum } from '../../model/querySortEnum';
import { ConsentRecordSigned } from '../../model/consents/consentRecordSigned';
import { AppConfig } from '../../model/appConfig';

@Injectable({ providedIn: 'root' })
export class ConsentsService {
  sdkUrl: string;
  checkConsentAtOperator: boolean;

  public config: AppConfig;

  constructor(configService: NgxConfigureService, private http: HttpClient) {
    this.config = configService.config as AppConfig;
    this.sdkUrl = this.config.system.sdkUrl;
    this.checkConsentAtOperator = this.config.system.checkConsentAtOperator;
  }

  getConsents(
    iatSort?: QuerySortEnum,
    surrogateId?: string,
    serviceId?: string,
    sourceServiceId?: string,
    datasetId?: string,
    status?: ConsentStatusEnum,
    purposeId?: string,
    purposeName?: string,
    purposeCategory?: ProcessingBasisPurposeCategoryEnum,
    processingCategories?: ProcessingBasisProcessingCategoriesEnum[]
  ): Promise<ConsentRecordSigned[]> {
    let params = new HttpParams();
    params = params.set('checkConsentAtOperator', String(this.checkConsentAtOperator));
    params = iatSort ? params.set('iatSort', iatSort) : params;
    params = surrogateId ? params.set('surrogateId', surrogateId) : params;
    params = serviceId ? params.set('serviceId', serviceId) : params;
    params = sourceServiceId ? params.set('sourceServiceId', sourceServiceId) : params;
    params = datasetId ? params.set('datasetId', datasetId) : params;
    params = status ? params.set('status', status) : params;
    params = purposeId ? params.set('purposeId', purposeId) : params;
    params = purposeName ? params.set('purposeName', purposeName) : params;
    params = purposeCategory ? params.set('purposeCategory', purposeCategory) : params;

    if (processingCategories)
      processingCategories.forEach((cat) => {
        params = params.append('processingCategories', cat);
      });

    return this.http
      .get<ConsentRecordSigned[]>(this.sdkUrl + '/api/v2/consents', {
        params: params,
      })
      .toPromise();
  }

  public async getConsentsBySurrogateIdAndQuery(
    surrogateId: string,
    iatSort?: QuerySortEnum,
    serviceId?: string,
    sourceServiceId?: string,
    datasetId?: string,
    status?: ConsentStatusEnum,
    purposeId?: string,
    purposeName?: string,
    purposeCategory?: ProcessingBasisPurposeCategoryEnum,
    processingCategories?: ProcessingBasisProcessingCategoriesEnum[]
  ): Promise<ConsentRecordSigned[]> {
    let params = new HttpParams();
    params = params.set('checkConsentAtOperator', String(this.checkConsentAtOperator));
    params = iatSort ? params.set('iatSort', iatSort) : params;
    params = serviceId ? params.set('serviceId', serviceId) : params;
    params = sourceServiceId ? params.set('sourceServiceId', sourceServiceId) : params;
    params = datasetId ? params.set('datasetId', datasetId) : params;
    params = status ? params.set('status', status) : params;
    params = purposeId ? params.set('purposeId', purposeId) : params;
    params = purposeName ? params.set('purposeName', purposeName) : params;
    params = purposeCategory ? params.set('purposeCategory', purposeCategory) : params;
    if (processingCategories)
      processingCategories.forEach((cat) => {
        params = params.append('processingCategories', cat);
      });

    return this.http
      .get<ConsentRecordSigned[]>(`${this.sdkUrl}/api/v2/users/surrogates/${surrogateId}/consents`, {
        params: params,
      })
      .toPromise();
  }

  public async getConsentsByUserIdAndQuery(
    serviceUserId: string,
    iatSort?: QuerySortEnum,
    serviceId?: string,
    sourceServiceId?: string,
    datasetId?: string,
    status?: ConsentStatusEnum,
    purposeId?: string,
    purposeName?: string,
    purposeCategory?: ProcessingBasisPurposeCategoryEnum,
    processingCategories?: ProcessingBasisProcessingCategoriesEnum[]
  ): Promise<ConsentRecordSigned[]> {
    let params = new HttpParams();
    params = params.set('checkConsentAtOperator', String(this.checkConsentAtOperator));
    params = iatSort ? params.set('iatSort', iatSort) : params;
    params = serviceId ? params.set('serviceId', serviceId) : params;
    params = sourceServiceId ? params.set('sourceServiceId', sourceServiceId) : params;
    params = datasetId ? params.set('datasetId', datasetId) : params;
    params = status ? params.set('status', status) : params;
    params = purposeId ? params.set('purposeId', purposeId) : params;
    params = purposeName ? params.set('purposeName', purposeName) : params;
    params = purposeCategory ? params.set('purposeCategory', purposeCategory) : params;
    if (processingCategories)
      processingCategories.forEach((cat) => {
        params = params.append('processingCategories', cat);
      });

    return this.http
      .get<ConsentRecordSigned[]>(`${this.sdkUrl}/api/v2/users/${serviceUserId}/consents`, {
        params: params,
      })
      .toPromise();
  }
}
