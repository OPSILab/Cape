import { Component, OnInit } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { Location } from '@angular/common';
import { NgxConfigureService } from 'ngx-configure';
import { LoginService } from '../auth/login/login.service';
import { ErrorResponse } from '../model/errorResponse';

@Component({
  selector: 'error-dialog',
  template: `<nb-card accent="danger" style="max-width: 95vw; max-height: 95vh">
    <nb-card-header class="d-flex justify-content-between">
      <h5>{{ 'general.error' | translate }}</h5>
      <button nbButton appearance="outline" shape="rectangle" size="tiny" status="info" class="close" (click)="closeModal(error)">
        <i class="material-icons">close</i>
      </button>
    </nb-card-header>
    <nb-card-body class="m-3">
      <div class="row justify-content-center p-1">
        <ng-container *ngIf="error.error !== undefined; else noNestedError"> {{ printErrorMessage(error.error) }} </ng-container>
      </div>
      <ng-template #noNestedError>{{ error.message }}</ng-template>
      <div class="row p-1 mt-1 justify-content-center">
        <strong>Status: {{ error.status }}</strong>
      </div>
    </nb-card-body>
  </nb-card> `,
})
export class ErrorDialogComponent {
  error: ErrorResponse;
  service;

  constructor(
    public ref: NbDialogRef<unknown>,
    private _location: Location,
    private configService: NgxConfigureService,
    private loginService: LoginService
  ) {}

  printErrorMessage(toParse: string | ErrorResponse): string {
    return typeof toParse === 'string' ? (JSON.parse(toParse) as ErrorResponse).message : toParse.message;
  }

  closeModal(error: ErrorResponse): void {
    if ((error.error as ErrorResponse).error === 'it.eng.opsi.cape.exception.AuditLogNotFoundException' || error.status === 0 || error.status === 401)
      void this.loginService.logout().catch((error) => console.log(error));
    // else
    //   this.backClicked();
    this.ref.close();
  }

  backClicked(): void {
    this._location.back();
  }
}
