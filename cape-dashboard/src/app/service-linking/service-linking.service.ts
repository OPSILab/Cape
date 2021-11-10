import { Injectable, TemplateRef } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { NgxConfigureService } from 'ngx-configure';
import { LinkingFromEnum } from '../model/service-linking/LinkingFromEnum';
import { AppConfig } from '../model/appConfig';
import { SurrogateIdResponse } from '../model/service-linking/surrogateIdResponse';
import { UserSurrogateIdLink } from '../model/service-linking/userSurrogateIdLink';
import { ErrorDialogService } from '../error-dialog/error-dialog.service';
import { LinkingResponseData } from '../model/service-linking/linkingResponseData';
import { StartLinkingRequest } from '../model/service-linking/startLinkingRequest';
import { ErrorResponse } from '../model/errorResponse';
import { NbDialogRef, NbDialogService } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class ServiceLinkingService {
  private config: AppConfig;
  private serviceManagerApiPath: string;

  constructor(
    configService: NgxConfigureService,
    private http: HttpClient,
    private errorDialogService: ErrorDialogService,
    private dialogService: NbDialogService,
    private translateService: TranslateService
  ) {
    this.config = configService.config as AppConfig;
    this.serviceManagerApiPath = this.config.system.serviceManagerUrl;
  }

  public startLinkingFromOperatorRedirectToServiceLogin(accountId: string, serviceId: string, forceLinking = false): Promise<HttpResponse<unknown>> {
    return this.http
      .get(`${this.serviceManagerApiPath}/api/v2/slr/account/${accountId}/service/${serviceId}?forceLinking=${forceLinking.toString()}`, {
        observe: 'response',
      })
      .toPromise();
  }

  public startLinkingFromServiceAfterOperatorLogin(
    accountId: string,
    surrogateId: string,
    serviceId: string,
    returnUrl: string,
    linkingFrom: LinkingFromEnum,
    forceLinking = false
  ): Promise<HttpResponse<unknown>> {
    return this.http
      .get(
        `${
          this.serviceManagerApiPath
        }/api/v2/slr/service/${serviceId}/account/${accountId}?surrogateId=${surrogateId}&returnUrl=${returnUrl}&linkingFrom=${linkingFrom}&forceLinking=${forceLinking.toString()}`,
        { observe: 'response' }
      )
      .toPromise();
  }

  async automaticLinkFromOperator(
    sdkUrl: string,
    operatorId: string,
    serviceId: string,
    serviceName: string,
    serviceUserId: string,
    // serviceUserEmail: string,
    // locale: string,
    returnUrl: string,
    linkedDialog: TemplateRef<unknown>,
    errorWithOptionDialog: TemplateRef<unknown>
  ): Promise<UserSurrogateIdLink> {
    try {
      const surrogateIdResponse = await this.generateSurrogateId(sdkUrl, operatorId, serviceUserId);
      const surrogateId = surrogateIdResponse.surrogate_id;

      // Get Linking Session Code for automatic Linking
      const sessionCode = await this.getServiceLinkingSessionCode(sdkUrl, serviceUserId, surrogateId, serviceId, returnUrl);
      console.log(sessionCode);

      // Start Service Linking with retrieved Linking Session Code
      await this.startServiceLinking(sdkUrl, sessionCode, surrogateId, operatorId, serviceId);

      // Once the Service Link is done, save the userId - surrogateId association
      const userSurrogateLink: UserSurrogateIdLink = await this.linkSurrogateId(sdkUrl, serviceUserId, surrogateId, serviceId, operatorId);

      const successMessage: string = await this.getLocalizedLabel(
        'general.services.linkingSuccessfulMessage',
        localStorage.getItem('currentLocale'),
        serviceId,
        serviceName
      );

      this.dialogService.open(linkedDialog, {
        context: {
          message: successMessage,
          returnUrl: returnUrl,
        },
        hasScroll: false,
      });

      return userSurrogateLink;
      // } catch (error) {
      //   this.errorDialogService.openErrorDialog(error as ErrorResponse);
      // }
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
      if (error.status === 409 && !error.error.message?.includes('Current State: COMPLETED')) {
        this.dialogService.open(errorWithOptionDialog, {
          context: {
            // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
            error: error,
          },
          hasScroll: false,
        });
      } else this.errorDialogService.openErrorDialog(error);
    }
  }

  private async getLocalizedLabel(field: string, locale: string, serviceId: string, serviceName: string): Promise<string> {
    // Set temporarily the language coming from Service locale, in order to get the localized Success message
    const currentLang = this.translateService.currentLang;
    this.translateService.use(locale);
    const translatedLabel: string = (await this.translateService
      .get(field, { serviceId: serviceId, serviceName: serviceName })
      .toPromise()) as string;
    this.translateService.use(currentLang);
    return translatedLabel;
  }

  public generateSurrogateId(sdkUrl: string, operatorId: string, serviceUserId: string): Promise<SurrogateIdResponse> {
    return this.http
      .get<SurrogateIdResponse>(`${sdkUrl}/api/v2/slr/linking/surrogateId?operatorId=${operatorId}&userId=${serviceUserId}`)
      .toPromise();
  }

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
   *
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
}
