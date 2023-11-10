import { Component } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { Location } from '@angular/common';
import { LoginService } from '../../auth/login/login.service';
import { NgxConfigureService } from 'ngx-configure';
import { AppConfig } from '../../model/appConfig';
@Component({
  selector: 'error-dialog',
  template: `
<nb-reveal-card *ngIf="appConfig.system.detailedErrors">
  <nb-card-front>
    <nb-card>
      <nb-card-header>
        {{ 'general.error' | translate }}
        <button nbButton outline status="info" class="close" (click)="closeModal(error)">{{ 'general.close' |
          translate
          }} </button>
      </nb-card-header>
      <nb-card-body>
        <ng-container
          *ngIf="!error.error || error.error !== 'EDITOR_VALIDATION_ERROR'; then genericError; else validationError">
        </ng-container>
        <ng-template #genericError>
          {{ 'general.errors' | translate }}
          <!--
          {{error.statusText}}
          {{error.header}}
          {{error.message}}-->


          <!--{{error.error.message}}-->
        </ng-template>
        <ng-template #validationError>
          <div class="row p-1 justify-content-center">{{ 'general.editor.validationErrors' | translate }}</div>
        </ng-template>
      </nb-card-body>
      <nb-card-footer *ngIf="appConfig.system.detailedErrors">
        {{ 'general.editor.show_error_details' | translate }}
      </nb-card-footer>
    </nb-card>
  </nb-card-front>
  <nb-card-back *ngIf="appConfig.system.detailedErrors">
    <nb-card>
      <nb-card-header>
        {{ 'general.error' | translate }} :
        {{error.statusText}}
        {{error.header}}
        <!--{{error.error.message}}-->
        <button nbButton outline status="info" class="close" (click)="closeModal(error)">{{ 'general.close' |
          translate
          }} </button>
      </nb-card-header>
      <nb-card-body>
        <ng-container
          *ngIf="!error.error || error.error !== 'EDITOR_VALIDATION_ERROR'; then genericErrorDetails; else validationErrorDetails">
        </ng-container>
        <ng-template #genericErrorDetails>
          {{error?.status ? "Status : " + error?.status + "\n\n" : null }}
          {{error?.statusText ? "Status text : " + error?.statusText + "\n\n" : null }}
          {{error.header ? "Header : " + error.header + "\n" : null }}
          {{error?.error?.message ? "Message : " + error?.error?.message + "\n\n" : error?.message ? "Message : " +
          error?.message + "\n\n" : null }}
        </ng-template>
        <ng-template #validationErrorDetails>
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
      <nb-card-footer>
        {{ 'general.editor.hide_error_details' | translate }}
      </nb-card-footer>
    </nb-card>
  </nb-card-back>
</nb-reveal-card>

<nb-card *ngIf="!appConfig.system.detailedErrors" style="width:300px;height:300px">
  <nb-card-header>
    {{ 'general.error' | translate }}
    <button nbButton outline status="info" class="close" (click)="closeModal(error)">{{ 'general.close' |
      translate
      }} </button>
  </nb-card-header>
  <nb-card-body>
    <ng-container
      *ngIf="error.error && error.error !== 'EDITOR_VALIDATION_ERROR'; then genericError; else validationError">
    </ng-container>
    <ng-template #genericError>
      {{ 'general.errors' | translate }}
      <!--
        {{error.statusText}}
        {{error.header}}
        {{error.message}}-->


      <!--{{error.error.message}}-->
    </ng-template>
    <ng-template #validationError>
      <div class="row p-1 justify-content-center">{{ 'general.editor.validationErrors' | translate }}</div>
    </ng-template>
  </nb-card-body>
  <nb-card-footer *ngIf="appConfig.system.detailedErrors">
    {{ 'general.editor.show_error_details' | translate }}
  </nb-card-footer>
</nb-card>

  `,
  styleUrls: ['./error-dialog.component.scss']

})
export class ErrorDialogComponent {
  public error;
  appConfig: AppConfig;

  constructor(private configService: NgxConfigureService, public ref: NbDialogRef<unknown>, private _location: Location, private loginService: LoginService) {
    this.appConfig = this.configService.config as AppConfig
  }

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
