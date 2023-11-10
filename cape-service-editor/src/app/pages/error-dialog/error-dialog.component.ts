import { Component } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { Location } from '@angular/common';
import { LoginService } from '../../auth/login/login.service';
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
        <ng-container *ngIf="error.error && error.error !== 'EDITOR_VALIDATION_ERROR'; then genericError; else validationError"> </ng-container>

        <ng-template #genericError>
          <div class="row p-1">{{ error.message }}</div>
          <div class="row justify-content-center p-1">{{ error.error.message }}</div>
          <div class="row p-1 mt-1 justify-content-center">
            <strong>Status: {{ error.status }}</strong>
          </div>
        </ng-template>

        <ng-template #validationError>
          <div class="row p-1 justify-content-center">{{ 'general.editor.validationErrors' | translate }}</div>
          <div class="row p-3">
            <table class="table table-striped">
              <thead class="thead-light text-center">
                <tr>
                  <th scope="col">{{ 'general.error' | translate }}</th>
                  <th scope="col">{{ 'general.editor.path' | translate }}</th>
                </tr>
              </thead>
              <tbody class="text-center">
                <tr *ngFor="let valError of error.validationErrors" class="">
                  <td>{{ valError.message }}</td>
                  <td>
                    {{ valError.path }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </ng-template>
      </nb-card-body>
    </nb-card>
  `,
})
export class ErrorDialogComponent {
  public error;

  constructor(public ref: NbDialogRef<unknown>, private _location: Location, private loginService: LoginService) {}

  closeModal(error: { [key: string]: { cause?: string } }): void {
    if (error.error?.cause === 'it.eng.opsi.cape.exception.AuditLogNotFoundException' || error.status === 401)
      void this.loginService.logout().catch((error) => console.log(error));
    // else
    //   this.backClicked();
    this.ref.close();
  }

  backClicked(): void {
    this._location.back();
  }
}
