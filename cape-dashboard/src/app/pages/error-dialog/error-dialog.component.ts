import { Component, OnInit } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { NgxConfigureService } from 'ngx-configure';
import { LoginService } from '../../login/login.service';

@Component({
  selector: 'error-dialog',
  template: `
    <nb-card accent="danger" style=" max-width: 95vw; max-height: 95vh;">
      <nb-card-header class="d-flex justify-content-between">
        <h5>{{'general.error' | translate}}</h5>
        <button nbButton appearance="outline" shape="rectangle" size="tiny" status="info" class="close" (click)="closeModal(error); ">
          <i class="material-icons">close</i>
        </button>
      </nb-card-header>
      <nb-card-body class="m-3">
         <div class="row p-1">{{error.message}}</div>
        <div *ngIf="error.error" class="row justify-content-center p-1">{{error.error.message}}</div>
        <div class="row p-1 mt-1 justify-content-center"><strong>Status: {{error.status}}</strong></div>
      </nb-card-body>
    </nb-card>
  `
})
export class ErrorDialogComponent {


  error;
  service;
  private dashHost: string;

  constructor(public ref: NbDialogRef<unknown>, private _location: Location,
    private router: Router, private configService: NgxConfigureService, private loginService: LoginService) {

    this.dashHost = this.configService.config.system.dashHost;

  }


  closeModal(error) {

    if (error.error?.cause === 'it.eng.opsi.cape.exception.AuditLogNotFoundException' || error.status === 0)
      this.loginService.logout();
    else
      this.backClicked();
    this.ref.close();

  }

  backClicked() {
    this._location.back();
  }

}
