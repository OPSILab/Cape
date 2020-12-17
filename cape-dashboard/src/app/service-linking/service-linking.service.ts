import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NgxConfigureService } from 'ngx-configure';
import { LinkingFromEnum } from '../model/service-linking/LinkingFromEnum';


@Injectable()
export class ServiceLinkingService {


  private config: any;
  private serviceManagerApiPath: string;

  constructor(configService: NgxConfigureService, private http: HttpClient) {

    this.config = configService.config;
    this.serviceManagerApiPath = this.config.system.serviceManagerUrl;
  }


  public startLinkingFromOperatorRedirectToServiceLogin(accountId: string, serviceId: string, forceLinking: boolean = false) {

    return this.http.get(`${this.serviceManagerApiPath}/slr/account/${accountId}/service/${serviceId}?forceLinking=${forceLinking}`,
      { observe: 'response' }).toPromise();
  }

  public startLinkingFromServiceAfterOperatorLogin(accountId: string, surrogateId: string, serviceId: string, returnUrl: string,
    linkingFrom: LinkingFromEnum, forceLinking: boolean = false) {

    return this.http.get(`${this.serviceManagerApiPath}/slr/service/${serviceId}/account/${accountId}?surrogateId=${surrogateId}&returnUrl=${returnUrl}&linkingFrom=${linkingFrom}&forceLinking=${forceLinking}`,
      { observe: 'response' }).toPromise();
  }


}
