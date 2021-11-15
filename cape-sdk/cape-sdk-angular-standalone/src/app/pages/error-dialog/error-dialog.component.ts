import { Component, OnInit } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { Location } from '@angular/common';
import { ErrorResponse } from 'cape-sdk-angular';
// import { LoginService } from 'src/app/auth/login/login.service';

@Component({
  selector: 'error-dialog',
  template: `
    <nb-card accent="danger" style=" max-width: 95vw; max-height: 95vh;">
      <nb-card-header class="d-flex justify-content-between">
        <h5 class="h5">Error</h5>
        <button nbButton appearance="outline" shape="rectangle" size="tiny" status="info" class="close" (click)="closeModal(error)">
          <i class="material-icons">close</i>
        </button>
      </nb-card-header>
      <nb-card-body class="m-3">
        <div class="row justify-content-center p-1">
          <ng-container *ngIf="error.error; else noNestedError">{{ printErrorMessage(error.error) }} </ng-container>
        </div>
        <ng-template #noNestedError>{{ error.message }}</ng-template>
        <div *ngIf="error.status != undefined" class="row p-1 mt-1 justify-content-center">
          <strong>Status: {{ error.status }}</strong>
        </div>
      </nb-card-body>
    </nb-card>
  `,
})
export class ErrorDialogComponent implements OnInit {
  error: ErrorResponse;

  constructor(public ref: NbDialogRef<unknown>, private _location: Location) {}

  ngOnInit() {}

  closeModal(error: ErrorResponse) {
    // if (error.error?.cause === 'it.eng.opsi.cape.exception.AuditLogNotFoundException' || error.status === 0 || error.status === 401) this.loginService.logout();
    // else
    //   this.backClicked();
    this.ref.close();
  }

  printErrorMessage(toParse: string | ErrorResponse): string {
    return typeof toParse === 'string' ? (JSON.parse(toParse) as ErrorResponse).message : toParse.message;
  }

  backClicked() {
    this._location.back();
  }
}
