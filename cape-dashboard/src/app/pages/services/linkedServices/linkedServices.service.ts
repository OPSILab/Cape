import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NgxConfigureService } from 'ngx-configure';
import { ServiceLinkRecordDoubleSigned } from '../../../model/service-linking/serviceLinkRecordDoubleSigned';
import { ServiceLinkStatusRecordSigned } from '../../../model/service-linking/serviceLinkStatusRecordSigned';
import { ChangeSlrStatusRequestFrom } from '../../../model/service-linking/changeSlrStatusRequestFrom';
import { AppConfig } from '../../../model/appConfig';

@Injectable()
export class LinkedServicesService {
  private accountUrl: string;
  private serviceManagerUrl: string;
  public config: AppConfig;

  private accountId: string = localStorage.getItem('accountId');

  constructor(configService: NgxConfigureService, private http: HttpClient) {
    this.config = configService.config as AppConfig;
    this.accountUrl = this.config.system.accountUrl;
    this.serviceManagerUrl = this.config.system.serviceManagerUrl;
  }

  getServiceLinks(): Promise<ServiceLinkRecordDoubleSigned[]> {
    return this.http.get<ServiceLinkRecordDoubleSigned[]>(`${this.accountUrl}/api/v2/accounts/${this.accountId}/servicelinks`).toPromise();
  }

  getServiceLinkByServiceId(serviceId: string): Promise<ServiceLinkRecordDoubleSigned> {
    return this.http
      .get<ServiceLinkRecordDoubleSigned>(`${this.accountUrl}/api/v2/accounts/${this.accountId}/services/${serviceId}/servicelink`)
      .toPromise();
  }

  disableServiceLink(serviceId: string, slrId: string): Promise<ServiceLinkStatusRecordSigned> {
    return this.http
      .delete<ServiceLinkStatusRecordSigned>(
        `${this.serviceManagerUrl}/api/v2/slr/account/${this.accountId}/services/${serviceId}/slr/${slrId}?requestFrom=${ChangeSlrStatusRequestFrom.Operator}`,
        {}
      )
      .toPromise();
  }

  enableServiceLink(serviceId: string, slrId: string): Promise<ServiceLinkStatusRecordSigned> {
    return this.http
      .put<ServiceLinkStatusRecordSigned>(
        `${this.serviceManagerUrl}/api/v2/slr/account/${this.accountId}/services/${serviceId}/slr/${slrId}?requestFrom=${ChangeSlrStatusRequestFrom.Operator}`,
        {}
      )
      .toPromise();
  }
}
