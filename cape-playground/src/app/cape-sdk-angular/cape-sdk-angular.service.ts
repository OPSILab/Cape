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


export interface UserSurrogateIdLink {
  userId: string;
  surrogateId: string;
  serviceId: string;
  operatorId: string;
}

export enum LinkingFromEnum {
  Service = 'Service',
  Operator = 'Operator'
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
  private linkSubject: BehaviorSubject<ServiceLinkEvent> = new BehaviorSubject<ServiceLinkEvent>(undefined);
  public serviceLinkStatus$ = this.linkSubject.asObservable();
  private consentSubject: BehaviorSubject<ConsentRecordEvent> = new BehaviorSubject<ConsentRecordEvent>(undefined);
  public consentRecordStatus$ = this.consentSubject.asObservable();


  constructor(private http: HttpClient, private errorDialogService: ErrorDialogService,
    private toastrService: NbToastrService, private translateService: TranslateService) { }


  async linkWithOperator(sdkUrl: string, locale: string, operatorId: string, serviceId: string, serviceName: string, userId: string, returnUrl: string, cdr: ChangeDetectorRef) {

    const surrogateIdResponse = await this.generateSurrogateId(sdkUrl, operatorId, userId);
    const surrogateId = surrogateIdResponse.surrogate_id;

    const operatorDescription = await this.getOperatorDescription(sdkUrl, operatorId);

    const operatorLinkingUrl = operatorDescription.operatorUrls.linkingUri;

    if (operatorLinkingUrl === undefined || operatorLinkingUrl === '')
      throw new Error("The Operator Linking Url is not valid or empty");

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
        const userSurrogateLink: UserSurrogateIdLink = await this.linkSurrogateId(sdkUrl, userId, surrogateId, serviceId, operatorId);

        // Trigger components subscribed to the Linking Completed event
        this.linkSubject.next({ serviceId: resServiceId, status: SlStatusEnum.Active, surrogateId: resSurrogateId } as ServiceLinkEvent);
        cdr.detectChanges();

      } else if (result === 'CANCELLED')
        this.toastrService.primary('', message, { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 5000 });
    };

    window.open(`${operatorLinkingUrl}?surrogateId=${surrogateId}&serviceId=${serviceId}&serviceName=${serviceName}&returnUrl=${returnUrl}&locale=${locale}&linkingFrom=Service`, "_blank");
  }


  async linkFromOperator(sdkUrl: string, code: string, operatorId: string, serviceId: string, serviceName: string, userId: string, returnUrl: string) {

    const surrogateIdResponse = await this.generateSurrogateId(sdkUrl, operatorId, userId);
    const surrogateId = surrogateIdResponse.surrogate_id;

    const linkingResponse: LinkingResponseData = await this.startServiceLinking(sdkUrl, code, surrogateId, operatorId, serviceId, returnUrl);

    const userSurrogateLink: UserSurrogateIdLink = await this.linkSurrogateId(sdkUrl, userId, surrogateId, serviceId, operatorId);
  }


  /*
   * Used when linking was started at Operator and after Service Login
   *
   * */
  public startServiceLinking(sdkUrl: string, code: string, surrogateId: string, operatorId: string, serviceId: string, returnUrl: string) {

    const startLinkingBody: StartLinkingRequest = {
      code: code,
      surrogate_id: surrogateId,
      service_id: serviceId,
      operator_id: operatorId,
      return_url: returnUrl
    };

    return this.http.post<LinkingResponseData>(`${sdkUrl}/slr/linking`, startLinkingBody).toPromise();

  }


  public generateSurrogateId(sdkUrl: string, operatorId: string, userId: string): Promise<SurrogateIdResponse> {

    return this.http
      .get<SurrogateIdResponse>(`${sdkUrl}/slr/surrogate_id?operatorId=${operatorId}&userId=${userId}`).toPromise();
  }


  public linkSurrogateId(sdkUrl: string, userId: string, surrogateId: string, serviceId: string, operatorId: string): Promise<UserSurrogateIdLink> {

    return this.http.post<UserSurrogateIdLink>(`${sdkUrl}/userSurrogateIdLink`, { userId: userId, surrogateId: surrogateId, serviceId: serviceId, operatorId: operatorId }).toPromise();

  }


  public async enableServiceLink(sdkUrl: string, slrId: string, surrogateId: string, serviceId: string, serviceName: string): Promise<ServiceLinkStatusRecordSigned> {

    const newServiceStatusRecord = await this.http
      .put<ServiceLinkStatusRecordSigned>(
        `${sdkUrl}/slr/${slrId}/surrogate/${surrogateId}/services/${serviceId}`, {})
      .toPromise();

    this.toastrService.primary('', this.translateService.instant('general.services.enableSuccessfulMessage', { serviceName: serviceName }),
      { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 4500 });
    this.emitServiceLinkEvent({ serviceId: serviceId, status: SlStatusEnum.Active, surrogateId: surrogateId, slrId: slrId } as ServiceLinkEvent);

    return newServiceStatusRecord;
  }


  public async disableServiceLink(sdkUrl: string, slrId: string, surrogateId: string, serviceId: string, serviceName: string): Promise<ServiceLinkStatusRecordSigned> {

    const newServiceStatusRecord = await this.http
      .delete<ServiceLinkStatusRecordSigned>(
        `${sdkUrl}/slr/${slrId}/surrogate/${surrogateId}/services/${serviceId}`, {})
      .toPromise();

    this.toastrService.primary('', this.translateService.instant('general.services.disableSuccessfulMessage', { serviceName: serviceName }),
      { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 4500 });

    this.emitServiceLinkEvent({ serviceId: serviceId, status: SlStatusEnum.Removed, surrogateId: surrogateId, slrId: slrId } as ServiceLinkEvent);

    return newServiceStatusRecord;
  }


  public getLinkSurrogateIdByUserIdAndServiceIdAndOperatorId(sdkUrl: string, userId, serviceId: string, operatorId: string): Promise<UserSurrogateIdLink> {

    return this.http.get<UserSurrogateIdLink>(`${sdkUrl}/userSurrogateIdLink?userId=${userId}&serviceId=${serviceId}&operatorId=${operatorId}`).toPromise();
  }


  public getOperatorDescription(sdkUrl: string, operatorId: string): Promise<OperatorDescription> {

    return this.http
      .get<OperatorDescription>(`${sdkUrl}/operatorDescriptions/${operatorId}`).toPromise();
  }


  public getServiceLinkRecordBySurrogateIdAndServiceId(sdkUrl: string, surrogateId: string, serviceId: string): Promise<ServiceLinkRecordDoubleSigned> {

    return this.http.get<ServiceLinkRecordDoubleSigned>(`${sdkUrl}/slr/surrogate/${surrogateId}/services/${serviceId}`).toPromise();
  }


  public async getServiceLinkRecordByUserIdAndServiceId(sdkUrl: string, userId: string, serviceId: string, operatorId: string): Promise<ServiceLinkRecordDoubleSigned> {

    const userSurrogateLink: UserSurrogateIdLink = await this.getLinkSurrogateIdByUserIdAndServiceIdAndOperatorId(sdkUrl, userId, serviceId, operatorId);
    return this.getServiceLinkRecordBySurrogateIdAndServiceId(sdkUrl, userSurrogateLink.surrogateId, serviceId);
  }


  public async getServiceLinkStatus(sdkUrl: string, userId: string, serviceId: string, operatorId: string): Promise<SlStatusEnum> {
    try {
      const serviceLink: ServiceLinkRecordDoubleSigned = await this.getServiceLinkRecordByUserIdAndServiceId(sdkUrl, userId, serviceId, operatorId);

      return serviceLink?.serviceLinkStatuses?.pop().payload.sl_status;

    } catch (error) {
      return null;
    }
  }


  public async fetchConsentForm(sdkUrl: string, userId: string, serviceId: string, operatorId: string, purposeId: string, sourceServiceId?: string, sourceDatasetId?: string): Promise<ConsentForm> {

    const userSurrogateLink: UserSurrogateIdLink = await this.getLinkSurrogateIdByUserIdAndServiceIdAndOperatorId(sdkUrl, userId, serviceId, operatorId);

    return this.http.get<ConsentForm>(`${sdkUrl}/users/${userSurrogateLink.surrogateId}/service/${serviceId}/purpose/${purposeId}/consentForm?sourceServiceId=${sourceServiceId}&sourceDatasetId=${sourceDatasetId}`).toPromise();
  }


  public async giveConsent(sdkUrl: string, consentForm: ConsentForm): Promise<ConsentRecordSigned> {

    return this.http.post<ConsentRecordSigned>(`${sdkUrl}/users/${consentForm.surrogate_id}/consents`, consentForm).toPromise();
  }

  public async getConsentsBySurrogateId(sdkUrl: string, surrogateId: string): Promise<ConsentRecordSigned> {

    return this.http.get<ConsentRecordSigned>(`${sdkUrl}/users/${surrogateId}/consent`).toPromise();
  }


  public async getConsentsBySurrogateIdAndPurposeId(sdkUrl: string, surrogateId: string, purposeId: string, checkConsentAtOperator: boolean): Promise<ConsentRecordSigned[]> {

    return this.http.get<ConsentRecordSigned[]>(`${sdkUrl}/users/${surrogateId}/purpose/${purposeId}/consent?checkConsentAtOperator=${checkConsentAtOperator}`).toPromise();
  }


  public async getConsentsByUserIdAndServiceIdAndPurposeId(sdkUrl: string, userId: string, serviceId: string, purposeId: string, operatorId: string, checkConsentAtOperator: boolean): Promise<ConsentRecordSigned[]> {

    const userSurrogateLink: UserSurrogateIdLink = await this.getLinkSurrogateIdByUserIdAndServiceIdAndOperatorId(sdkUrl, userId, serviceId, operatorId);
    return this.getConsentsBySurrogateIdAndPurposeId(sdkUrl, userSurrogateLink.surrogateId, purposeId, checkConsentAtOperator);
  }


  public async getConsentStatus(sdkUrl: string, userId: string, serviceId: string, purposeId: string, operatorId: string, checkConsentAtOperator: boolean): Promise<ConsentStatusEnum> {

    const consentRecords = await this.getConsentsByUserIdAndServiceIdAndPurposeId(sdkUrl, userId, serviceId, purposeId, operatorId, checkConsentAtOperator);
    return consentRecords[0].consentStatusList.pop().payload.consent_status;
  }


  private async changeConsentStatus(sdkUrl: string, consent: ConsentRecordSigned, newStatus: ConsentStatusEnum): Promise<ConsentStatusRecordSigned> {

    const url = `${sdkUrl}/users/${consent.payload.common_part.surrogate_id}/servicelinks/${consent.payload.common_part.slr_id}/consents/${consent.payload.common_part.cr_id}/statuses`;

    return this.http
      .post<ConsentStatusRecordSigned>(url,
        {
          resource_set: consent.consentStatusList.length > 1 ?
            consent.consentStatusList[consent.consentStatusList.length - 1].payload.consent_resource_set
            : consent.payload.common_part.rs_description.resource_set,

          status: newStatus,

          usage_rules: consent.consentStatusList.length > 1 ?
            consent.consentStatusList[consent.consentStatusList.length - 1].payload.consent_usage_rules
            : (consent.payload.role_specific_part as ConsentRecordSinkRoleSpecificPart).usage_rules,
          request_from: ChangeConsentStatusRequestFrom.Service
        } as ChangeConsentStatusRequest).toPromise();
  }


  public async disableConsent(sdkUrl: string, consent: ConsentRecordSigned): Promise<ConsentStatusRecordSigned> {

    const newConsentStatusRecord = await this.changeConsentStatus(sdkUrl, consent, ConsentStatusEnum.Disabled);

    this.toastrService.primary('', this.translateService.instant('general.consent.disableSuccessfulMessage'),
      { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 4500 });

    this.emitConsentRecordEvent({
      crId: consent.payload.common_part.cr_id,
      serviceId: consent.payload.common_part.subject_id,
      status: newConsentStatusRecord.payload
    } as ConsentRecordEvent);

    return newConsentStatusRecord;
  }


  public async enableConsent(sdkUrl: string, consent: ConsentRecordSigned): Promise<ConsentStatusRecordSigned> {

    const newConsentStatusRecord = await this.changeConsentStatus(sdkUrl, consent, ConsentStatusEnum.Active);

    this.toastrService.primary('', this.translateService.instant('general.consent.enableSuccessfulMessage'),
      { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 4500 });

    this.emitConsentRecordEvent({
      crId: consent.payload.common_part.cr_id,
      serviceId: consent.payload.common_part.subject_id,
      status: newConsentStatusRecord.payload
    } as ConsentRecordEvent);

    return newConsentStatusRecord;
  }


  public async withdrawConsent(sdkUrl: string, consent: ConsentRecordSigned): Promise<ConsentStatusRecordSigned> {

    const newConsentStatusRecord = await this.changeConsentStatus(sdkUrl, consent, ConsentStatusEnum.Withdrawn);

    this.toastrService.primary('', this.translateService.instant('general.consent.withdrawSuccessfulMessage'),
      { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 4500 });

    this.emitConsentRecordEvent({
      crId: consent.payload.common_part.cr_id,
      serviceId: consent.payload.common_part.subject_id,
      status: newConsentStatusRecord.payload
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
    return this.linkSubject.getValue().status;
  }


  public getRegisteredService(sdkUrl: string, serviceId: string): Promise<ServiceEntry> {

    return this.http.get<ServiceEntry>(`${sdkUrl}/services/${serviceId}?onlyRegistered=true`).toPromise();
  }

}
