import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import 'rxjs/add/operator/toPromise';
import { NgxConfigureService } from 'ngx-configure';
import { ServiceEntry } from '../../../model/service-linking/serviceEntry';


@Injectable({ providedIn: 'root' })
export class AvailableServicesService {

  private serviceRegistryUrl: string;
  private serviceManagerUrl: string;
  private accountUrl: string;
  private loading: boolean;
  private config;
  private sdkUrl: string;


  constructor(private configService: NgxConfigureService, private http: HttpClient) {

    this.loading = false;
    this.config = this.configService.config;
    this.serviceRegistryUrl = this.config.serviceRegistry.url;
    this.accountUrl = this.config.system.accountUrl;
    this.sdkUrl = this.config.system.sdkUrl;

  }


  getServices(): Promise<ServiceEntry[]> {

    return this.http
      .get<ServiceEntry[]>(`${this.serviceRegistryUrl}/services`).toPromise();
  }


  getServicesCount(): Promise<number> {

    return this.http
      .get<number>(`${this.serviceRegistryUrl}/services/count`).toPromise();
  }


  getService(serviceId: string) {
    
    return this.http
      .get(`${this.serviceRegistryUrl}/services/${serviceId}`);
  }


  saveService(service: Object): Promise<Object> {

    return this.http
      .post(`${this.serviceRegistryUrl}/services`, service).toPromise();
  }


  registerService(serviceId: string): Promise<Object> {

    return this.http
    .post(`${this.sdkUrl}/services/${serviceId}`, "", { headers: { "Content-Type": "application/json" } }).toPromise();
  }


  deregisterService(serviceId: string): Promise<Object> {

    return this.http
      .delete(`${this.sdkUrl}/services/${serviceId}`).toPromise();
  }


  updateService(service: Object, serviceId: string): Promise<Object> {

    return this.http
      .put(`${this.serviceRegistryUrl}/services/${serviceId}`, service
      ).toPromise();
  }

}
