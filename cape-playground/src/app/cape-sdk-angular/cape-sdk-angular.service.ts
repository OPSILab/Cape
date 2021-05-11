import { Injectable, ChangeDetectorRef } from '@angular/core';

import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, Subject, BehaviorSubject } from 'rxjs';
import { ErrorDialogService } from '../pages/error-dialog/error-dialog.service';
import { NbToastrService, NbPosition, NbGlobalLogicalPosition } from '@nebular/theme';
import { LinkingResponseData } from './model/service-link/linkingResponseData';
import { StartLinkingRequest } from './model/service-link/startLinkingRequest';
import { SurrogateIdResponse } from './model/service-link/surrogateIdResponse';
import { OperatorDescription } from './model/service-link/operatorDescription';
import { ServiceLinkRecordDoubleSigned } from './model/service-link/serviceLinkRecordDoubleSigned';
import { SlStatusEnum } from './model/service-link/serviceLinkStatusRecordPayload';
import { ServiceLinkStatusRecordSigned } from './model/service-link/serviceLinkStatusRecordSigned';
import { TranslateService } from '@ngx-translate/core';
import { ConsentForm } from './model/consent/consentForm';
import { ConsentRecordSigned } from './model/consent/consentRecordSigned';
import { ConsentStatusEnum, ConsentStatusRecordPayload } from './model/consent/consentStatusRecordPayload';
import { ConsentStatusRecordSigned } from './model/consent/consentStatusRecordSigned';
import { ConsentRecordRoleSpecificPart } from './model/consent/consentRecordRoleSpecificPart';
import { ConsentRecordSinkRoleSpecificPart } from './model/consent/consentRecordSinkRoleSpecificPart';
import { ChangeConsentStatusRequestFrom } from './model/consent/changeSlrStatusRequestFrom';
import { ChangeConsentStatusRequest } from './model/consent/changeConsentStatusRequest';
import { ServiceEntry } from './model/service-link/serviceEntry';
import { Account } from './model/account/account.model';

export interface UserSurrogateIdLink {
  userId: string;
  surrogateId: string;
  serviceId: string;
  operatorId: string;
}

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

@Injectable()
export class CapeSdkAngularService {
  private registeredSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(undefined);
  public isRegisteredService$ = this.registeredSubject.asObservable();
  private linkSubject: Subject<ServiceLinkEvent> = new Subject<ServiceLinkEvent>();
  public serviceLinkStatus$ = this.linkSubject.asObservable();
  private consentSubject: BehaviorSubject<ConsentRecordEvent> = new BehaviorSubject<ConsentRecordEvent>(undefined);
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
  ) {
    const surrogateIdResponse = await this.generateSurrogateId(sdkUrl, operatorId, serviceUserId);
    const surrogateId = surrogateIdResponse.surrogate_id;

    const operatorDescription = await this.getOperatorDescription(sdkUrl, operatorId);

    const operatorLinkingUrl = operatorDescription.operatorUrls.linkingUri;

    if (operatorLinkingUrl === undefined || operatorLinkingUrl === '') throw new Error('The Operator Linking Url is not valid or empty');

    /*
     * Operator returned Message
     * */
    window.onmessage = async (event) => {
      const result: string = event.data.result;
      const message: string = event.data.message;
      const resSurrogateId: string = event.data.surrogateId;
      const resServiceId: string = event.data.serviceId;
      const resReturnUrl: string = event.data.returnUrl;

      if (resSurrogateId !== surrogateId || resServiceId !== serviceId || resReturnUrl !== returnUrl)
        this.errorDialogService.openErrorDialog(new Error(this.translateService.instant('general.services.linkingParamMismatch')));
      else if (result === 'SUCCESS') {
        this.toastrService.primary('', message, { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 5000 });

        // link userId - surrogateId
        const userSurrogateLink: UserSurrogateIdLink = await this.linkSurrogateId(sdkUrl, serviceUserId, surrogateId, serviceId, operatorId);

        // Trigger components subscribed to the Linking Completed event
        this.linkSubject.next({ serviceId: resServiceId, status: SlStatusEnum.Active, surrogateId: resSurrogateId } as ServiceLinkEvent);
        cdr.detectChanges();
      } else if (result === 'CANCELLED') this.toastrService.primary('', message, { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 5000 });
    };

    window.open(
      `${operatorLinkingUrl}?surrogateId=${surrogateId}&serviceId=${serviceId}&serviceName=${serviceName}&returnUrl=${returnUrl}&locale=${locale}&linkingFrom=Service`,
      '_blank'
    );
  }

  async linkFromOperator(
    sdkUrl: string,
    sessionCode: string,
    operatorId: string,
    serviceId: string,
    serviceName: string,
    serviceUserId: string,
    returnUrl: string
  ) {
    const surrogateIdResponse = await this.generateSurrogateId(sdkUrl, operatorId, serviceUserId);
    const surrogateId = surrogateIdResponse.surrogate_id;

    const linkingResponse: LinkingResponseData = await this.startServiceLinking(sdkUrl, sessionCode, surrogateId, operatorId, serviceId, returnUrl);

    const userSurrogateLink: UserSurrogateIdLink = await this.linkSurrogateId(sdkUrl, serviceUserId, surrogateId, serviceId, operatorId);
  }

  async automaticLinkFromService(sdkUrl: string, operatorId: string, serviceId: string, serviceUserId: string, returnUrl: string) {
    const surrogateIdResponse = await this.generateSurrogateId(sdkUrl, operatorId, serviceUserId);
    const surrogateId = surrogateIdResponse.surrogate_id;

    // Get Linking Session Code for automatic Linking
    const sessionCode = await this.getServiceLinkingSessionCode(sdkUrl, serviceUserId, surrogateId, serviceId, returnUrl);
    console.log(sessionCode);

    // Start Service Linking with retrieved Linking Session Code
    const linkingResponse: LinkingResponseData = await this.startServiceLinking(sdkUrl, sessionCode, surrogateId, operatorId, serviceId, returnUrl);

    // Once the Service Link is done, save the userId - surrogateId association
    const userSurrogateLink: UserSurrogateIdLink = await this.linkSurrogateId(sdkUrl, serviceUserId, surrogateId, serviceId, operatorId);

    return userSurrogateLink;
  }

  /*
   *
   * Get Session Code for background linking from service and transparent to User ( automatic acceptance of service linking)
   *
   * */

  async getServiceLinkingSessionCode(sdkUrl: string, serviceUserId: string, surrogateId: string, serviceId: string, returnUrl: string) {
    return this.http
      .get(
        `${sdkUrl}/slr/linking/sessionCode?serviceId=${serviceId}&userId=${serviceUserId}&surrogateId=${surrogateId}&returnUrl=${returnUrl}&forceLinking=true`,
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
  public startServiceLinking(sdkUrl: string, sessionCode: string, surrogateId: string, operatorId: string, serviceId: string, returnUrl: string) {
    const startLinkingBody: StartLinkingRequest = {
      session_code: sessionCode,
      surrogate_id: surrogateId,
      service_id: serviceId,
      operator_id: operatorId,
      return_url: returnUrl,
    };

    return this.http.post<LinkingResponseData>(`${sdkUrl}/slr/linking`, startLinkingBody).toPromise();
  }

  public generateSurrogateId(sdkUrl: string, operatorId: string, serviceUserId: string): Promise<SurrogateIdResponse> {
    return this.http.get<SurrogateIdResponse>(`${sdkUrl}/slr/surrogate_id?operatorId=${operatorId}&userId=${serviceUserId}`).toPromise();
  }

  public linkSurrogateId(sdkUrl: string, serviceUserId: string, surrogateId: string, serviceId: string, operatorId: string): Promise<UserSurrogateIdLink> {
    return this.http
      .post<UserSurrogateIdLink>(`${sdkUrl}/userSurrogateIdLink`, {
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
      .put<ServiceLinkStatusRecordSigned>(`${sdkUrl}/slr/${slrId}/surrogate/${surrogateId}/services/${serviceId}`, {})
      .toPromise();

    this.toastrService.primary('', this.translateService.instant('general.services.enableSuccessfulMessage', { serviceName: serviceName }), {
      position: NbGlobalLogicalPosition.BOTTOM_END,
      duration: 4500,
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
      .delete<ServiceLinkStatusRecordSigned>(`${sdkUrl}/slr/${slrId}/surrogate/${surrogateId}/services/${serviceId}`, {})
      .toPromise();

    this.toastrService.primary('', this.translateService.instant('general.services.disableSuccessfulMessage', { serviceName: serviceName }), {
      position: NbGlobalLogicalPosition.BOTTOM_END,
      duration: 4500,
    });

    this.emitServiceLinkEvent({ serviceId: serviceId, status: SlStatusEnum.Removed, surrogateId: surrogateId, slrId: slrId } as ServiceLinkEvent);

    return newServiceStatusRecord;
  }

  public getLinkSurrogateIdByUserIdAndServiceIdAndOperatorId(
    sdkUrl: string,
    serviceUserId,
    serviceId: string,
    operatorId: string
  ): Promise<UserSurrogateIdLink> {
    return this.http
      .get<UserSurrogateIdLink>(`${sdkUrl}/userSurrogateIdLink?userId=${serviceUserId}&serviceId=${serviceId}&operatorId=${operatorId}`)
      .toPromise();
  }

  public getOperatorDescription(sdkUrl: string, operatorId: string): Promise<OperatorDescription> {
    return this.http.get<OperatorDescription>(`${sdkUrl}/operatorDescriptions/${operatorId}`).toPromise();
  }

  public getServiceLinkRecordBySurrogateIdAndServiceId(sdkUrl: string, surrogateId: string, serviceId: string): Promise<ServiceLinkRecordDoubleSigned> {
    return this.http.get<ServiceLinkRecordDoubleSigned>(`${sdkUrl}/slr/surrogate/${surrogateId}/services/${serviceId}`).toPromise();
  }

  public async getServiceLinkRecordByUserIdAndServiceId(
    sdkUrl: string,
    serviceUserId: string,
    serviceId: string,
    operatorId: string
  ): Promise<ServiceLinkRecordDoubleSigned> {
    const userSurrogateLink: UserSurrogateIdLink = await this.getLinkSurrogateIdByUserIdAndServiceIdAndOperatorId(sdkUrl, serviceUserId, serviceId, operatorId);
    return this.getServiceLinkRecordBySurrogateIdAndServiceId(sdkUrl, userSurrogateLink.surrogateId, serviceId);
  }

  public async getServiceLinkStatus(sdkUrl: string, serviceUserId: string, serviceId: string, operatorId: string): Promise<SlStatusEnum> {
    try {
      const serviceLink: ServiceLinkRecordDoubleSigned = await this.getServiceLinkRecordByUserIdAndServiceId(sdkUrl, serviceUserId, serviceId, operatorId);

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
    sourceServiceId?: string,
    sourceDatasetId?: string
  ): Promise<ConsentForm> {
    const userSurrogateLink: UserSurrogateIdLink = await this.getLinkSurrogateIdByUserIdAndServiceIdAndOperatorId(sdkUrl, serviceUserId, serviceId, operatorId);

    let fetchConsentFormUrl = `${sdkUrl}/users/surrogates/${userSurrogateLink.surrogateId}/service/${serviceId}/purpose/${purposeId}/consentForm`;

    if (sourceServiceId && sourceDatasetId) fetchConsentFormUrl += `?sourceServiceId=${sourceServiceId}&sourceDatasetId=${sourceDatasetId}`;
    return this.http.get<ConsentForm>(fetchConsentFormUrl).toPromise();
  }

  public async giveConsent(sdkUrl: string, consentForm: ConsentForm): Promise<ConsentRecordSigned> {
    return this.http.post<ConsentRecordSigned>(`${sdkUrl}/users/surrogates/${consentForm.surrogate_id}/consents`, consentForm).toPromise();
  }

  public async getConsentsBySurrogateId(sdkUrl: string, surrogateId: string): Promise<ConsentRecordSigned> {
    return this.http.get<ConsentRecordSigned>(`${sdkUrl}/users/surrogates/${surrogateId}/consents`).toPromise();
  }

  public async getConsentsBySurrogateIdAndPurposeId(
    sdkUrl: string,
    surrogateId: string,
    purposeId: string,
    checkConsentAtOperator: boolean
  ): Promise<ConsentRecordSigned[]> {
    return this.http
      .get<ConsentRecordSigned[]>(`${sdkUrl}/users/surrogates/${surrogateId}/consents?purposeId=${purposeId}&checkConsentAtOperator=${checkConsentAtOperator}`)
      .toPromise();
  }

  public async getConsentsByUserIdAndServiceIdAndPurposeId(
    sdkUrl: string,
    serviceUserId: string,
    serviceId: string,
    purposeId: string,
    operatorId: string,
    checkConsentAtOperator: boolean
  ): Promise<ConsentRecordSigned[]> {
    return this.http
      .get<ConsentRecordSigned[]>(
        `${sdkUrl}/users/${serviceUserId}/consents?serviceId=${serviceId}&purposeId=${purposeId}&checkConsentAtOperator=${checkConsentAtOperator}`
      )
      .toPromise();
  }

  public async getConsentStatus(
    sdkUrl: string,
    serviceUserId: string,
    serviceId: string,
    purposeId: string,
    operatorId: string,
    checkConsentAtOperator: boolean
  ): Promise<ConsentStatusEnum> {
    const consentRecords = await this.getConsentsByUserIdAndServiceIdAndPurposeId(
      sdkUrl,
      serviceUserId,
      serviceId,
      purposeId,
      operatorId,
      checkConsentAtOperator
    );
    return consentRecords[0].consentStatusList.pop().payload.consent_status;
  }

  private async changeConsentStatus(sdkUrl: string, consent: ConsentRecordSigned, newStatus: ConsentStatusEnum): Promise<ConsentRecordSigned> {
    const url = `${sdkUrl}/users/${consent.payload.common_part.surrogate_id}/servicelinks/${consent.payload.common_part.slr_id}/consents/${consent.payload.common_part.cr_id}/statuses`;

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
            : (consent.payload.role_specific_part as ConsentRecordSinkRoleSpecificPart).usage_rules,
        request_from: ChangeConsentStatusRequestFrom.Service,
      } as ChangeConsentStatusRequest)
      .toPromise();
  }

  public async disableConsent(sdkUrl: string, consent: ConsentRecordSigned): Promise<ConsentStatusRecordSigned> {
    const newConsentRecord = await this.changeConsentStatus(sdkUrl, consent, ConsentStatusEnum.Disabled);
    const newConsentStatusRecord = newConsentRecord.consentStatusList.pop();

    this.toastrService.primary('', this.translateService.instant('general.consent.disableSuccessfulMessage'), {
      position: NbGlobalLogicalPosition.BOTTOM_END,
      duration: 4500,
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
      duration: 4500,
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
      duration: 4500,
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
    return this.http.get<ServiceEntry>(`${sdkUrl}/services/${serviceId}?onlyRegistered=true`).toPromise();
  }

  public createCapeAccount(sdkUrl: string, accountId: string, accountEmail: string, locale: string): Promise<Account> {
    return this.http
      .post<Account>(`${sdkUrl}/accounts`, {
        username: localStorage.serviceAccountId as string,
        account_info: {
          email: localStorage.serviceAccountEmail as string,
        },
        language: locale,
      } as Account)
      .toPromise();
  }
}
