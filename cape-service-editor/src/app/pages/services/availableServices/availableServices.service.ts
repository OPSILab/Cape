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
  private sdkUrl: string;

  constructor(private configService: NgxConfigureService, private http: HttpClient) {
    this.config = this.configService.config as AppConfig;
    this.serviceRegistryUrl = this.config.serviceRegistry.url;
    this.sdkUrl = this.config.system.sdkUrl;
  }

  getServices(): Promise<ServiceEntry[]> {
    return this.http.get<ServiceEntry[]>(`${this.serviceRegistryUrl}/api/v2/services?withCertificate=true`).toPromise();
  }

  getServicesCount(): Promise<number> {
    return this.http.get<number>(`${this.serviceRegistryUrl}/api/v2/services/count`).toPromise();
  }

  getService(serviceId: string): Promise<ServiceEntry> {
    return this.http.get<ServiceEntry>(`${this.serviceRegistryUrl}/api/v2/services/${serviceId}`).toPromise();
  }

  saveService(service: ServiceEntry): Promise<ServiceEntry> {
    return this.http.post<ServiceEntry>(`${this.serviceRegistryUrl}/api/v2/services`, service).toPromise();
  }

  registerService(serviceId: string): Promise<ServiceEntry> {
    return this.http
      .post<ServiceEntry>(`${this.sdkUrl}/api/v2/services/${serviceId}`, '', { headers: { 'Content-Type': 'application/json' } })
      .toPromise();
  }

  deregisterService(serviceId: string): Promise<ServiceEntry> {
    return this.http.delete(`${this.sdkUrl}/api/v2/services/${serviceId}`).toPromise();
  }

  updateService(service: Object, serviceId: string): Promise<ServiceEntry> {
    return this.http.put(`${this.serviceRegistryUrl}/api/v2/services/${serviceId}`, service).toPromise();
  }

  deleteService(serviceId: string): Promise<ServiceEntry> {
    return this.http.delete(`${this.serviceRegistryUrl}/api/v2/services/${serviceId}`).toPromise();
  }
}
