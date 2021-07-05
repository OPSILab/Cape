import { Injectable, ChangeDetectorRef } from '@angular/core';

import { HttpClient, HttpParams } from '@angular/common/http';
import { Subject, BehaviorSubject } from 'rxjs';
import { NbToastrService, NbGlobalLogicalPosition } from '@nebular/theme';
import { LinkingResponseData } from './model/service-link/linkingResponseData';
import { SurrogateIdResponse } from './model/service-link/surrogateIdResponse';
import { DataOperatorDescription } from './model/service-link/dataOperatorDescription';
import { ServiceLinkRecordDoubleSigned } from './model/service-link/serviceLinkRecordDoubleSigned';
import { SlStatusEnum } from './model/service-link/serviceLinkStatusRecordPayload';
import { ServiceLinkStatusRecordSigned } from './model/service-link/serviceLinkStatusRecordSigned';
import { TranslateService } from '@ngx-translate/core';
import { ConsentForm } from './model/consent/consentForm';
import { ConsentRecordSigned } from './model/consent/consentRecordSigned';
import { ConsentStatusEnum, ConsentStatusRecordPayload } from './model/consent/consentStatusRecordPayload';
import { ConsentStatusRecordSigned } from './model/consent/consentStatusRecordSigned';
import { ConsentRecordSinkRoleSpecificPart } from './model/consent/consentRecordSinkRoleSpecificPart';
import { ChangeConsentStatusRequestFrom } from './model/consent/changeSlrStatusRequestFrom';
import { ChangeConsentStatusRequest } from './model/consent/changeConsentStatusRequest';
import { RoleEnum, ServiceEntry } from './model/service-link/serviceEntry';
import { Account } from './model/account/account.model';
import { ProcessingBasisProcessingCategories, ProcessingBasisPurposeCategory } from './model/processingBasis';
import { QuerySortEnum } from './model/querySortEnum';
import { ErrorDialogService } from './error-dialog/error-dialog.service';
import { UserSurrogateIdLink } from './model/service-link/userSurrogateIdLink';
import { StartLinkingRequest } from './model/service-link/startLinkingRequest';
import { ConsentFormRequest } from './model/consent/consentFormRequest';
import { UsageRules } from './model/consent/UsageRules';

export enum LinkingFromEnum {
  Service = 'Service',
  Operator = 'Operator',
}

export interface ServiceLinkEvent {
  serviceId: string;
  slrId?: string;
  surrogateId?: string;
  status: SlStatusEnum;
}

export interface ConsentRecordEvent {
  serviceId: string;
  crId: string;
  status: ConsentStatusRecordPayload;
  consentRecord?: ConsentRecordSigned;
}

@Injectable({ providedIn: 'root' })
export class CapeSdkAngularService {
  private registeredSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public isRegisteredService$ = this.registeredSubject.asObservable();
  private linkSubject: Subject<ServiceLinkEvent> = new Subject<ServiceLinkEvent>();
  public serviceLinkStatus$ = this.linkSubject.asObservable();
  private consentSubject: Subject<ConsentRecordEvent> = new Subject<ConsentRecordEvent>();
  public consentRecordStatus$ = this.consentSubject.asObservable();

  constructor(
    private http: HttpClient,
    private errorDialogService: ErrorDialogService,
    private toastrService: NbToastrService,
    private translateService: TranslateService
  ) {}

  async linkWithOperator(
    sdkUrl: string,
    locale: string,
    operatorId: string,
    serviceId: string,
    serviceName: string,
    serviceUserId: string,
    returnUrl: string,
    cdr: ChangeDetectorRef
  ): Promise<void> {
    const surrogateIdResponse = await this.generateSurrogateId(sdkUrl, operatorId, serviceUserId);
    const surrogateId = surrogateIdResponse.surrogate_id;

    const dataOperatorDescription = await this.getDataOperatorDescription(sdkUrl, operatorId);

    const dataOperatorLinkingUrl = dataOperatorDescription.operatorUrls.linkingUri;

    if (dataOperatorLinkingUrl === undefined || dataOperatorLinkingUrl === '') throw new Error('The Operator Linking Url is not valid or empty');

    /*
     * Operator returned Message
     * */
    window.onmessage = async (event: MessageEvent<Record<string, string>>) => {
      const result: string = event.data.result;
      const message: string = event.data.message;
      const resSurrogateId: string = event.data.surrogateId;
      const resServiceId: string = event.data.serviceId;
      const resReturnUrl: string = event.data.returnUrl;

      if (resSurrogateId !== surrogateId || resServiceId !== serviceId || resReturnUrl !== returnUrl)
        this.errorDialogService.openErrorDialog(new Error(this.translateService.instant('general.services.linkingParamMismatch')));
      else if (result === 'SUCCESS') {
        this.toastrService.primary('', message, { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 3000 });

        // link userId - surrogateId
        await this.linkSurrogateId(sdkUrl, serviceUserId, surrogateId, serviceId, operatorId);

        // Trigger components subscribed to the Linking Completed event
        this.linkSubject.next({ serviceId: resServiceId, status: SlStatusEnum.Active, surrogateId: resSurrogateId } as ServiceLinkEvent);
        cdr.detectChanges();
      } else if (result === 'CANCELLED') this.toastrService.primary('', message, { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 3000 });
    };

    window.open(
      `${dataOperatorLinkingUrl}?surrogateId=${surrogateId}&serviceId=${serviceId}&serviceName=${serviceName}&returnUrl=${returnUrl}&locale=${locale}&linkingFrom=Service`,
      '_blank'
    );
  }

  async linkFromOperator(
    sdkUrl: string,
    sessionCode: string,
    operatorId: string,
    serviceId: string,
    serviceName: string,
    serviceUserId: string
  ): Promise<void> {
    const surrogateIdResponse = await this.generateSurrogateId(sdkUrl, operatorId, serviceUserId);
    const surrogateId = surrogateIdResponse.surrogate_id;

    await this.startServiceLinking(sdkUrl, sessionCode, surrogateId, operatorId, serviceId);
    await this.linkSurrogateId(sdkUrl, serviceUserId, surrogateId, serviceId, operatorId);
  }

  async automaticLinkFromService(
    sdkUrl: string,
    operatorId: string,
    serviceId: string,
    serviceUserId: string,
    serviceUserEmail: string,
    locale: string,
    returnUrl: string
  ): Promise<UserSurrogateIdLink> {
    const surrogateIdResponse = await this.generateSurrogateId(sdkUrl, operatorId, serviceUserId);
    const surrogateId = surrogateIdResponse.surrogate_id;

    // Get Linking Session Code for automatic Linking
    const sessionCode = await this.getServiceLinkingSessionCode(sdkUrl, serviceUserId, surrogateId, serviceId, returnUrl);
    console.log(sessionCode);

    try {
      // Start Service Linking with retrieved Linking Session Code
      await this.startServiceLinking(sdkUrl, sessionCode, surrogateId, operatorId, serviceId);
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error?.error.innerError.innerError.error == 'it.eng.opsi.cape.exception.AccountNotFoundException') {
        // Call SDK API to Create Account using as accountId the accountId of the Service
        await this.createCapeAccount(sdkUrl, serviceUserId, serviceUserEmail, locale);
        // Once the Cape Account has been created, retry automaticLinking
        return await this.automaticLinkFromService(sdkUrl, operatorId, serviceId, serviceUserId, serviceUserEmail, locale, returnUrl);
      } else this.errorDialogService.openErrorDialog(error);
    }
    // Once the Service Link is done, save the userId - surrogateId association
    const userSurrogateLink: UserSurrogateIdLink = await this.linkSurrogateId(sdkUrl, serviceUserId, surrogateId, serviceId, operatorId);

    return userSurrogateLink;
  }

  /*
   *
   * Get Session Code for background linking from service and transparent to User ( automatic acceptance of service linking)
   *
   * */

  async getServiceLinkingSessionCode(
    sdkUrl: string,
    serviceUserId: string,
    surrogateId: string,
    serviceId: string,
    returnUrl: string
  ): Promise<string> {
    return this.http
      .get(
        `${sdkUrl}/api/v2/slr/linking/sessionCode?serviceId=${serviceId}&userId=${serviceUserId}&surrogateId=${surrogateId}&returnUrl=${returnUrl}&forceLinking=true`,
        {
          responseType: 'text',
        }
      )
      .toPromise();
  }

  /*
   * Used when linking was started at Operator and after Service Login
   * or background linking from service and transparent to User ( automatic acceptance of service linking)
   *
   * */
  public startServiceLinking(
    sdkUrl: string,
    sessionCode: string,
    surrogateId: string,
    operatorId: string,
    serviceId: string
  ): Promise<LinkingResponseData> {
    return this.http
      .post<LinkingResponseData>(`${sdkUrl}/api/v2/slr/linking`, {
        session_code: sessionCode,
        surrogate_id: surrogateId,
        service_id: serviceId,
        operator_id: operatorId,
      } as StartLinkingRequest)
      .toPromise();
  }

  public generateSurrogateId(sdkUrl: string, operatorId: string, serviceUserId: string): Promise<SurrogateIdResponse> {
    return this.http
      .get<SurrogateIdResponse>(`${sdkUrl}/api/v2/slr/linking/surrogateId?operatorId=${operatorId}&userId=${serviceUserId}`)
      .toPromise();
  }

  public linkSurrogateId(
    sdkUrl: string,
    serviceUserId: string,
    surrogateId: string,
    serviceId: string,
    operatorId: string
  ): Promise<UserSurrogateIdLink> {
    return this.http
      .post<UserSurrogateIdLink>(`${sdkUrl}/api/v2/userSurrogateIdLink`, {
        userId: serviceUserId,
        surrogateId: surrogateId,
        serviceId: serviceId,
        operatorId: operatorId,
      })
      .toPromise();
  }

  public async enableServiceLink(
    sdkUrl: string,
    slrId: string,
    surrogateId: string,
    serviceId: string,
    serviceName: string
  ): Promise<ServiceLinkStatusRecordSigned> {
    const newServiceStatusRecord = await this.http
      .put<ServiceLinkStatusRecordSigned>(`${sdkUrl}/api/v2/slr/${slrId}/surrogate/${surrogateId}/services/${serviceId}`, {})
      .toPromise();

    this.toastrService.primary('', this.translateService.instant('general.services.enableSuccessfulMessage', { serviceName: serviceName }), {
      position: NbGlobalLogicalPosition.BOTTOM_END,
      duration: 3000,
    });
    this.emitServiceLinkEvent({ serviceId: serviceId, status: SlStatusEnum.Active, surrogateId: surrogateId, slrId: slrId } as ServiceLinkEvent);

    return newServiceStatusRecord;
  }

  public async disableServiceLink(
    sdkUrl: string,
    slrId: string,
    surrogateId: string,
    serviceId: string,
    serviceName: string
  ): Promise<ServiceLinkStatusRecordSigned> {
    const newServiceStatusRecord = await this.http
      .delete<ServiceLinkStatusRecordSigned>(`${sdkUrl}/api/v2/slr/${slrId}/surrogate/${surrogateId}/services/${serviceId}`, {})
      .toPromise();

    this.toastrService.primary('', this.translateService.instant('general.services.disableSuccessfulMessage', { serviceName: serviceName }), {
      position: NbGlobalLogicalPosition.BOTTOM_END,
      duration: 3000,
    });

    this.emitServiceLinkEvent({ serviceId: serviceId, status: SlStatusEnum.Removed, surrogateId: surrogateId, slrId: slrId } as ServiceLinkEvent);

    return newServiceStatusRecord;
  }

  public getLinkSurrogateIdByUserIdAndServiceIdAndOperatorId(
    sdkUrl: string,
    serviceUserId: string,
    serviceId: string,
    operatorId: string
  ): Promise<UserSurrogateIdLink> {
    return this.http
      .get<UserSurrogateIdLink>(`${sdkUrl}/api/v2/userSurrogateIdLink?userId=${serviceUserId}&serviceId=${serviceId}&operatorId=${operatorId}`)
      .toPromise();
  }

  public getDataOperatorDescription(sdkUrl: string, operatorId: string): Promise<DataOperatorDescription> {
    return this.http.get<DataOperatorDescription>(`${sdkUrl}/api/v2/dataOperatorDescriptions/${operatorId}`).toPromise();
  }

  public getServiceLinkRecordBySurrogateIdAndServiceId(
    sdkUrl: string,
    surrogateId: string,
    serviceId: string
  ): Promise<ServiceLinkRecordDoubleSigned> {
    return this.http.get<ServiceLinkRecordDoubleSigned>(`${sdkUrl}/api/v2/slr/surrogate/${surrogateId}/services/${serviceId}`).toPromise();
  }

  public async getServiceLinkRecordByUserIdAndServiceId(
    sdkUrl: string,
    serviceUserId: string,
    serviceId: string,
    operatorId: string
  ): Promise<ServiceLinkRecordDoubleSigned> {
    const userSurrogateLink: UserSurrogateIdLink = await this.getLinkSurrogateIdByUserIdAndServiceIdAndOperatorId(
      sdkUrl,
      serviceUserId,
      serviceId,
      operatorId
    );
    return this.getServiceLinkRecordBySurrogateIdAndServiceId(sdkUrl, userSurrogateLink.surrogateId, serviceId);
  }

  public async getServiceLinkStatus(sdkUrl: string, serviceUserId: string, serviceId: string, operatorId: string): Promise<SlStatusEnum> {
    try {
      const serviceLink: ServiceLinkRecordDoubleSigned = await this.getServiceLinkRecordByUserIdAndServiceId(
        sdkUrl,
        serviceUserId,
        serviceId,
        operatorId
      );

      return serviceLink?.serviceLinkStatuses?.pop().payload.sl_status;
    } catch (error) {
      return null;
    }
  }

  public async fetchConsentForm(
    sdkUrl: string,
    serviceUserId: string,
    serviceId: string,
    operatorId: string,
    purposeId: string,
    requesterRole: RoleEnum,
    sourceServiceId?: string,
    sourceDatasetId?: string
  ): Promise<ConsentForm> {
    const userSurrogateLink: UserSurrogateIdLink = await this.getLinkSurrogateIdByUserIdAndServiceIdAndOperatorId(
      sdkUrl,
      serviceUserId,
      serviceId,
      operatorId
    );

    return this.http
      .post<ConsentForm>(`${sdkUrl}/api/v2/consents/consentForm`, {
        requester_surrogate_id: userSurrogateLink.surrogateId,
        requester_role: requesterRole,
        purpose_id: purposeId,
        sink_service_id: serviceId,
        source_service_id: sourceServiceId,
        source_service_dataset_id: sourceDatasetId,
      } as ConsentFormRequest)
      .toPromise();
  }

  public async giveConsent(sdkUrl: string, consentForm: ConsentForm): Promise<ConsentRecordSigned> {
    return this.http.post<ConsentRecordSigned>(`${sdkUrl}/api/v2/consents`, consentForm).toPromise();
  }

  public async getConsentsBySurrogateIdAndQuery(
    sdkUrl: string,
    checkConsentAtOperator: boolean,
    surrogateId: string,
    serviceId?: string,
    sourceServiceId?: string,
    datasetId?: string,
    status?: ConsentStatusEnum,
    purposeId?: string,
    purposeName?: string,
    purposeCategory?: ProcessingBasisPurposeCategory,
    processingCategory?: ProcessingBasisProcessingCategories,
    iatSort?: QuerySortEnum
  ): Promise<ConsentRecordSigned[]> {
    let params = new HttpParams();
    params = params.set('checkConsentAtOperator', String(checkConsentAtOperator));
    params = iatSort ? params.set('iatSort', iatSort) : params;
    params = serviceId ? params.set('serviceId', serviceId) : params;
    params = sourceServiceId ? params.set('sourceServiceId', sourceServiceId) : params;
    params = datasetId ? params.set('datasetId', datasetId) : params;
    params = status ? params.set('status', status) : params;
    params = purposeId ? params.set('purposeId', purposeId) : params;
    params = purposeName ? params.set('purposeName', purposeName) : params;
    params = purposeCategory ? params.set('purposeCategory', purposeCategory) : params;
    params = processingCategory ? params.set('processingCategory', processingCategory) : params;
    return this.http
      .get<ConsentRecordSigned[]>(`${sdkUrl}/api/v2/users/surrogates/${surrogateId}/consents`, {
        params: params,
      })
      .toPromise();
  }

  public async getConsentsByUserIdAndQuery(
    sdkUrl: string,
    checkConsentAtOperator: boolean,
    serviceUserId: string,
    serviceId?: string,
    sourceServiceId?: string,
    datasetId?: string,
    status?: ConsentStatusEnum,
    purposeId?: string,
    purposeName?: string,
    purposeCategory?: ProcessingBasisPurposeCategory,
    processingCategory?: ProcessingBasisProcessingCategories,
    iatSort?: QuerySortEnum
  ): Promise<ConsentRecordSigned[]> {
    let params = new HttpParams();
    params = params.set('checkConsentAtOperator', String(checkConsentAtOperator));
    params = iatSort ? params.set('iatSort', iatSort) : params;
    params = serviceId ? params.set('serviceId', serviceId) : params;
    params = sourceServiceId ? params.set('sourceServiceId', sourceServiceId) : params;
    params = datasetId ? params.set('datasetId', datasetId) : params;
    params = status ? params.set('status', status) : params;
    params = purposeId ? params.set('purposeId', purposeId) : params;
    params = purposeName ? params.set('purposeName', purposeName) : params;
    params = purposeCategory ? params.set('purposeCategory', purposeCategory) : params;
    params = processingCategory ? params.set('processingCategory', processingCategory) : params;

    return this.http
      .get<ConsentRecordSigned[]>(`${sdkUrl}/api/v2/users/${serviceUserId}/consents`, {
        params: params,
      })
      .toPromise();
  }

  private async changeConsentStatus(sdkUrl: string, consent: ConsentRecordSigned, newStatus: ConsentStatusEnum): Promise<ConsentRecordSigned> {
    const url = `${sdkUrl}/api/v2/users/${consent.payload.common_part.surrogate_id}/servicelinks/${consent.payload.common_part.slr_id}/consents/${consent.payload.common_part.cr_id}/statuses`;

    return this.http
      .post<ConsentRecordSigned>(url, {
        resource_set:
          consent.consentStatusList.length > 1
            ? consent.consentStatusList[consent.consentStatusList.length - 1].payload.consent_resource_set
            : consent.payload.common_part.rs_description.resource_set,

        status: newStatus,

        usage_rules:
          consent.consentStatusList.length > 1
            ? consent.consentStatusList[consent.consentStatusList.length - 1].payload.consent_usage_rules
            : consent.payload.common_part.usage_rules,
        request_from: ChangeConsentStatusRequestFrom.Service,
      } as ChangeConsentStatusRequest)
      .toPromise();
  }

  public async disableConsent(sdkUrl: string, consent: ConsentRecordSigned): Promise<ConsentStatusRecordSigned> {
    const newConsentRecord = await this.changeConsentStatus(sdkUrl, consent, ConsentStatusEnum.Disabled);
    const newConsentStatusRecord = newConsentRecord.consentStatusList.pop();

    this.toastrService.primary('', this.translateService.instant('general.consent.disableSuccessfulMessage'), {
      position: NbGlobalLogicalPosition.BOTTOM_END,
      duration: 3000,
    });

    this.emitConsentRecordEvent({
      crId: consent.payload.common_part.cr_id,
      serviceId: consent.payload.common_part.subject_id,
      status: newConsentStatusRecord.payload,
    } as ConsentRecordEvent);

    return newConsentStatusRecord;
  }

  public async enableConsent(sdkUrl: string, consent: ConsentRecordSigned): Promise<ConsentStatusRecordSigned> {
    const newConsentRecord = await this.changeConsentStatus(sdkUrl, consent, ConsentStatusEnum.Active);
    const newConsentStatusRecord = newConsentRecord.consentStatusList.pop();

    this.toastrService.primary('', this.translateService.instant('general.consent.enableSuccessfulMessage'), {
      position: NbGlobalLogicalPosition.BOTTOM_END,
      duration: 3000,
    });

    this.emitConsentRecordEvent({
      crId: consent.payload.common_part.cr_id,
      serviceId: consent.payload.common_part.subject_id,
      status: newConsentStatusRecord.payload,
    } as ConsentRecordEvent);

    return newConsentStatusRecord;
  }

  public async withdrawConsent(sdkUrl: string, consent: ConsentRecordSigned): Promise<ConsentStatusRecordSigned> {
    const newConsentRecord = await this.changeConsentStatus(sdkUrl, consent, ConsentStatusEnum.Withdrawn);
    const newConsentStatusRecord = newConsentRecord.consentStatusList.pop();

    this.toastrService.primary('', this.translateService.instant('general.consent.withdrawSuccessfulMessage'), {
      position: NbGlobalLogicalPosition.BOTTOM_END,
      duration: 3000,
    });

    this.emitConsentRecordEvent({
      crId: consent.payload.common_part.cr_id,
      serviceId: consent.payload.common_part.subject_id,
      status: newConsentStatusRecord.payload,
    } as ConsentRecordEvent);

    return newConsentStatusRecord;
  }

  public emitIsRegisteredValue(isRegistered: boolean): boolean {
    this.registeredSubject.next(isRegistered);
    return this.registeredSubject.getValue();
  }

  public emitConsentRecordEvent(event: ConsentRecordEvent): void {
    this.consentSubject.next(event);
  }

  public emitServiceLinkEvent(event: ServiceLinkEvent): SlStatusEnum {
    this.linkSubject.next(event);
    return event.status;
  }

  public getRegisteredService(sdkUrl: string, serviceId: string): Promise<ServiceEntry> {
    return this.http.get<ServiceEntry>(`${sdkUrl}/api/v2/services/${serviceId}?onlyRegistered=true`).toPromise();
  }

  public createCapeAccount(sdkUrl: string, accountId: string, accountEmail: string, locale: string): Promise<Account> {
    return this.http
      .post<Account>(`${sdkUrl}/api/v2/accounts`, {
        username: localStorage.serviceAccountId as string,
        account_info: {
          email: localStorage.serviceAccountEmail as string,
        },
        language: locale,
      } as Account)
      .toPromise();
  }
}
