import { Component, OnInit } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { LoginService } from '../../login/login.service';
import { Location } from '@angular/common';
@Component({
  selector: 'error-dialog',
  template: `
    <nb-card accent="danger" style=" max-width: 95vw; max-height: 95vh;">
      <nb-card-header class="d-flex justify-content-between">
        <h5 class="h5">Error</h5>
        <button nbButton appearance="outline" shape="rectangle" size="tiny" status="info" class="close" (click)="closeModal(error);">
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
export class ErrorDialogComponent implements OnInit {

  public error;

  constructor(public ref: NbDialogRef<unknown>,private _location: Location,private loginService: LoginService) { }

  ngOnInit() {
  }

  closeModal(error) {

    if (error.error?.cause === 'it.eng.opsi.cape.exception.AuditLogNotFoundException' || error.status === 0 || error.status === 401)
      this.loginService.logout();
    else
      this.backClicked();
    this.ref.close();

  }

  backClicked() {
    this._location.back();
  }

}
