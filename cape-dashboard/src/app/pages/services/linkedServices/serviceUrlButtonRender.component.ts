import { Component, OnInit, Input } from '@angular/core';
import { LinkedServiceRow } from './linkedServices.component';

@Component({
  template: `
    <a href="{{ serviceUri.startsWith('http://') ? serviceUri : 'http://' + serviceUri }}" target="_blank"
      ><nb-icon class="p-0" icon="external-link"></nb-icon
    ></a>
  `,
})
export class ServiceUrlButtonRenderComponent implements OnInit {
  @Input() value: LinkedServiceRow;

  serviceUri: string;

  ngOnInit(): void {
    this.serviceUri = this.value.service_uri;
  }
}
