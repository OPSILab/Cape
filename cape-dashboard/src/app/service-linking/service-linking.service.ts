import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { NgxConfigureService } from 'ngx-configure';
import { LinkingFromEnum } from '../model/service-linking/LinkingFromEnum';
import { AppConfig } from '../model/appConfig';

@Injectable()
export class ServiceLinkingService {
  private config: AppConfig;
  private serviceManagerApiPath: string;

  constructor(configService: NgxConfigureService, private http: HttpClient) {
    this.config = configService.config as AppConfig;
    this.serviceManagerApiPath = this.config.system.serviceManagerUrl;
  }

  public startLinkingFromOperatorRedirectToServiceLogin(
    accountId: string,
    serviceId: string,
    forceLinking: boolean = false
  ): Promise<HttpResponse<unknown>> {
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
    forceLinking: boolean = false
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
}
