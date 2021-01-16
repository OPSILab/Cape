import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../globals';
import { NgxConfigureService } from 'ngx-configure';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  apiRoot: string;

  public config: any;
  constructor(
    private http: HttpClient,
    private globals: Globals,
    configService: NgxConfigureService,
  ) { 

    this.config = configService.config;
    
  }

  getWidgets() {
    return this.http.get(this.globals.WIDGETS_URL);
  }

  getPersonalData() {
    return this.http.get(this.globals.PERSONAL_DATA_URL);
  }

  getPurpose() {
    return this.http.get(this.globals.PURPOSE_URL);
  }

  getContext() {
    return this.http.get(this.globals.CONTEXT_URL);
  }

  getDataStore() {
    return this.http.get(this.globals.DATA_STORE_URL);
  }

  getPersonalVsProcessed() {
    return this.http.get(this.globals.PERSONAL_PROCESSED_URL);
  }

  getGraphData() {
   // return this.http.get(this.globals.GRAPH_DATA_URL);
   this.apiRoot = `${this.config.system.dashUrl}${this.config.system.assetsDataDir}/cape_dataflow.json`;
    return this.http.get(this.apiRoot);
  
  
    }
  
  
}
