import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import 'rxjs/add/operator/toPromise';
import { NgxConfigureService } from 'ngx-configure';
import { AuditLog, EventLog, DateRange } from '../../model/auditlogs/auditlogs.model';
import { ProcessingBasisProcessingCategoriesEnum, ProcessingBasisLegalBasisEnum } from '../../model/processingBasis';
import { AppConfig } from '../../model/appConfig';

@Injectable()
export class AuditLogsService {
  auditLogUrl: string;
  accountUrl: string;
  loading: boolean;

  private config: AppConfig;
  private accountId: string = localStorage.getItem('accountId');

  constructor(configService: NgxConfigureService, private http: HttpClient) {
    this.loading = false;
    this.config = configService.config as AppConfig;
    this.auditLogUrl = this.config.system.auditLogUrl;
    this.accountUrl = this.config.system.accountUrl;
  }

  getAuditLog(): Promise<AuditLog> {
    return this.http.get<AuditLog>(`${this.auditLogUrl}/api/v2/auditLogs/accounts/${this.accountId}`).toPromise();
  }

  getEventLogs(): Promise<EventLog[]> {
    return this.http.get<EventLog[]>(`${this.auditLogUrl}/api/v2/eventLogs/accounts/${this.accountId}`).toPromise();
  }

  getFilteredEventLogs(
    legalBasisFilter: ProcessingBasisLegalBasisEnum[],
    dateRangeFilter: DateRange,
    processingFilter: ProcessingBasisProcessingCategoriesEnum[]
  ): Promise<EventLog[]> {
    let queryParams = new HttpParams();
    if (legalBasisFilter) for (const legalBasis of legalBasisFilter) queryParams = queryParams.append('legalBasis', legalBasis);

    if (dateRangeFilter) {
      queryParams = dateRangeFilter.start ? queryParams.append('startDate', dateRangeFilter.start.toISOString()) : queryParams;
      queryParams = dateRangeFilter.end ? queryParams.append('endDate', dateRangeFilter.end.toISOString()) : queryParams;
    }

    if (processingFilter) for (const processing of processingFilter) queryParams = queryParams.append('processingCategories', processing);

    const httpOptions = {
      params: queryParams,
    };

    return this.http.get<EventLog[]>(`${this.auditLogUrl}/api/v2/eventLogs/accounts/${this.accountId}`, httpOptions).toPromise();
  }
}
