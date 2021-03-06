import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import 'rxjs/add/operator/toPromise';
import { NgxConfigureService } from 'ngx-configure';
import { ServiceEntry } from '../../../model/service-linking/serviceEntry';
import { AppConfig } from '../../../model/appConfig';

@Injectable({ providedIn: 'root' })
export class AvailableServicesService {
  private serviceRegistryUrl: string;
  private serviceManagerUrl: string;
  private accountUrl: string;
  private loading: boolean;
  private config: AppConfig;

  private accountId: string = localStorage.getItem('accountId');

  constructor(private configService: NgxConfigureService, private http: HttpClient) {
    this.loading = false;
    this.config = this.configService.config as AppConfig;
    this.serviceRegistryUrl = this.config.serviceRegistry.url;
    this.accountUrl = this.config.system.accountUrl;
    this.serviceManagerUrl = this.config.system.serviceManagerUrl;
  }

  getRegisteredServices(): Promise<ServiceEntry[]> {
    return this.http.get<ServiceEntry[]>(`${this.serviceRegistryUrl}/services?onlyRegistered=true`).toPromise();
  }

  getServicesCount(onlyRegistered: boolean): Promise<number> {
    return this.http.get<number>(`${this.serviceRegistryUrl}/services/count?onlyRegistered=${onlyRegistered.toString()}`).toPromise();
  }

  getService(serviceId: string): Promise<ServiceEntry> {
    return this.http.get<ServiceEntry>(`${this.serviceRegistryUrl}/services/${serviceId}`).toPromise();
  }
}
