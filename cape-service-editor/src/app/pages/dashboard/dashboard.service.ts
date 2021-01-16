import { Injectable } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';


export interface AuditLog {
  linkedServicesCount: number,
  givenConsentsCount: number,
  totalProcessedPersonalDataCount: number,
  processedPersonalDataCount: object,
  processingCategoryPersonalData: object,
  purposeCategoryCount: object,
  legalBasisCount: object,
  storageLocationPersonalData: object
}




@Injectable()
export class DashboardService {

  private config: any;
  private auditApiPath: string;
  private token: string = `Bearer ${localStorage.getItem('tokenData')}`;
  private accountId: string = localStorage.getItem('accountId');
  private serviceRegistryUrl: string;

  constructor(configService: NgxConfigureService, private http: HttpClient) {

    this.config = configService.config;
    this.auditApiPath = this.config.system.host + this.config.system.auditlog;
    this.serviceRegistryUrl = this.config.serviceRegistry.url;
  }


  getAuditLog(): Observable<AuditLog> {

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': this.token
      })
    };

    return this.http
      .get<AuditLog>(`${this.auditApiPath}/auditLogs/accounts/${this.accountId}`, httpOptions);
  }


  getServicesCount(): Observable<any> {

    return this.http
      .get(this.serviceRegistryUrl + '/services/count');
  }

}
