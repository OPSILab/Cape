import { Injectable } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

@Injectable()
export class DashboardService {

  private config: any;
  private auditLogUrl: string;
  private servicesUrl: string;

  private accountId: string = localStorage.getItem('accountId');

  constructor(configService: NgxConfigureService, private http: HttpClient) {

    this.config = configService.config;
  }

}
