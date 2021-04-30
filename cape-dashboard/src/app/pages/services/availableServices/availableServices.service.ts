import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import 'rxjs/add/operator/toPromise';
import { NgxConfigureService } from 'ngx-configure';
import { ServiceEntry } from '../../../model/service-linking/serviceEntry';
import { AppConfig } from '../../../model/appConfig';

@Injectable({ providedIn: 'root' })
export class AvailableServicesService {
  private serviceRegistryUrl: string;
  private config: AppConfig;

  constructor(private configService: NgxConfigureService, private http: HttpClient) {
    this.config = this.configService.config as AppConfig;
    this.serviceRegistryUrl = this.config.serviceRegistry.url;
  }

  getRegisteredServices(): Promise<ServiceEntry[]> {
    return this.http.get<ServiceEntry[]>(`${this.serviceRegistryUrl}/api/v2/services?onlyRegistered=true`).toPromise();
  }

  getServicesCount(onlyRegistered: boolean): Promise<number> {
    return this.http.get<number>(`${this.serviceRegistryUrl}/api/v2/services/count?onlyRegistered=${onlyRegistered.toString()}`).toPromise();
  }

  getService(serviceId: string): Promise<ServiceEntry> {
    return this.http.get<ServiceEntry>(`${this.serviceRegistryUrl}/api/v2/services/${serviceId}`).toPromise();
  }
}
