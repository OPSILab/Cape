import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ViewCell } from 'ng2-smart-table';
import { LinkedServicesService } from './linkedServices.service';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Component({
  template: `
    <a href="{{serviceUri.startsWith('http://') ? serviceUri : 'http://' + serviceUri }}" target="_blank"><nb-icon class="p-0" icon="external-link"></nb-icon></a>
  `
})
export class ServiceUrlButtonRenderComponent implements OnInit {


  @Input() value;

  serviceUri: string;

  constructor() {

  }

  ngOnInit(): void {
    this.serviceUri = this.value.serviceUri;
  }
}
