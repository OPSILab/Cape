import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from 'rxjs';
import 'rxjs/add/operator/toPromise';
import { NgxConfigureService } from 'ngx-configure';


@Injectable({ providedIn: "root" })
export class ConsentsService {

  loading: boolean;
  service: any;
  sdkUrl: string;

  public config: any;

  token: string = `Bearer ${localStorage.getItem('tokenData')}`;
  accountId: string = localStorage.getItem('accountData');
  userId: string;

  constructor(configService: NgxConfigureService, private http: HttpClient) {
    
    this.config = configService.config;
    this.sdkUrl = this.config.system.sdkUrl;
    this.loading = false;

    if ((this.userId = localStorage.getItem('userData')))
      JSON.parse(this.userId).userId;
  }


  getTestConsents(): Promise<any> {
    
    var apiConsents = `${this.config.system.serviceEditorUrl}${this.config.system.assetsDataDir}/consents.json`;
    return this.http
      .get(apiConsents).toPromise();
  }


  getConsents(): Promise<any> {

    return this.http
      .get(this.sdkUrl + "/services/consents"
      ).toPromise();
  }


}
