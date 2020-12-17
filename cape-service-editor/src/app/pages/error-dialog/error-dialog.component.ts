import { Component, OnInit } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';

@Component({
  selector: 'error-dialog',
  template: `
    <nb-card accent="danger" style=" max-width: 95vw; max-height: 95vh;">
      <nb-card-header class="d-flex justify-content-between">
        <h5 class="h5">Error</h5>
        <button nbButton appearance="outline" shape="rectangle" size="tiny" status="info" class="close" (click)="ref.close()">
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

  constructor(public ref: NbDialogRef<unknown>) { }

  ngOnInit() {
  }

  closeModal() {
    this.ref.close();
  }



}
