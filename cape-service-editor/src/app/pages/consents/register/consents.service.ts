import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from 'rxjs';
import 'rxjs/add/operator/toPromise';
import { NgxConfigureService } from 'ngx-configure';


@Injectable({ providedIn: "root" })
export class ConsentsService {

  apiPath: string;
  loading: boolean;
  service: any;
  sdkUrl:string;

  public config: any;

  token: string = `Bearer ${localStorage.getItem('tokenData')}`;
  accountId: string = localStorage.getItem('accountData');
  userId: string;

  constructor(configService: NgxConfigureService, private http: HttpClient) {
    this.loading = false;
    this.config = configService.config;
    this.apiPath = this.config.serviceRegistry.host + this.config.serviceRegistry.servicesApiPath;
 
    this.sdkUrl = this.config.system.sdkUrl;
    
    this.loading = false;
   
    //this.apiRoot=this.config.service_registry+"/service-registry/api/v1/services";
    


    if ((this.userId = localStorage.getItem('userData')))
      JSON.parse(this.userId).userId;
  }

  
  getTestConsents(): Promise<any> {
    var apiConsents= this.config.system.dashHost+this.config.system.dashPath+this.config.assets_data+"consents.json";
    return this.http
      .get(apiConsents).toPromise();
  }

  

  getConsents(): Promise<any> {

    return this.http
      .get(this.sdkUrl+"/services/consents"
      ).toPromise();
  }

  

 

}
