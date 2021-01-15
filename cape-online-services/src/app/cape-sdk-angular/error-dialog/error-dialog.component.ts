import { Component, OnInit } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';


@Component({
  selector: 'error-dialog',
  template: `
    <nb-card accent="danger" style=" max-width: 95vw; max-height: 95vh;">
      <nb-card-header class="d-flex justify-content-between">
        <h5>{{ 'general.error' | translate}}</h5>
        <button nbButton appearance="outline" shape="rectangle" size="tiny" status="info" class="close" (click)="ref.close()">
          <i class="material-icons">close</i>
        </button>
      </nb-card-header>
      <nb-card-body class="m-3">
        <div class="row">{{error.error? error.error.message : error.message}}</div>
        <div class="row mt-1 justify-content-center"><strong>Status: {{error.status}}</strong></div>
      </nb-card-body>
      <nb-card-footer>
        <div class="row justify-content-center">
          <button nbButton ghost shape="rectangle" size="medium" status="primary" (click)="ref.close()"> <i class="material-icons">close</i></button>
        </div>
      </nb-card-footer>
    </nb-card>
  `
})
export class ErrorDialogComponent {


  error;
  service;

  constructor(public ref: NbDialogRef<unknown>) { }


  closeModal() {
    this.ref.close();
  }



}
