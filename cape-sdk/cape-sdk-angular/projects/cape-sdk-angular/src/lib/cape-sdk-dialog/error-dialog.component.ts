import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NbDialogRef } from '@nebular/theme';
import { ErrorResponse } from '../model/errorResponse';

@Component({
  selector: 'error-dialog',
  template: `
    <nb-card accent="danger" style=" max-width: 95vw; max-height: 95vh;">
      <nb-card-header class="d-flex justify-content-between">
        <h5>{{ 'general.error' | translate }}</h5>
        <button nbButton appearance="outline" shape="rectangle" size="tiny" status="info" class="close" (click)="closeModal(error)">
          <i class="material-icons">close</i>
        </button>
      </nb-card-header>
      <nb-card-body class="m-3">
        <div class="row p-1">{{ error.message }}</div>
        <div *ngIf="error.error" class="row justify-content-center p-1">{{ error.error.message }}</div>
        <div class="row p-1 mt-1 justify-content-center">
          <strong>Status: {{ error.status }}</strong>
        </div>
      </nb-card-body>
      <nb-card-footer class="d-flex justify-content-end">
        <button nbButton ghost shape="rectangle" size="medium" (click)="closeModal(error)">
          {{ 'general.services.closeButton' | translate }}
        </button>
        <button
          *ngIf="error.error?.innerError?.error == 'it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException'"
          nbButton
          ghost
          shape="rectangle"
          size="medium"
          (click)="openCapeDashboard()"
        >
          {{ 'general.services.openCapeDashboardToLinkService' | translate }}
        </button>
      </nb-card-footer>
    </nb-card>
  `,
})
export class ErrorDialogComponent {
  error;
  dashboardUrl: string;

  constructor(public ref: NbDialogRef<unknown>, private router: Router) {}

  openCapeDashboard(): void {
    window.open(`${this.dashboardUrl}/pages/services/availableServices`, '_blank');
  }

  closeModal(error: ErrorResponse): void {
    if (error.status === 0 || error.status === 401) {
      localStorage.removeItem('accountId');
      localStorage.removeItem('accountEmail');
      localStorage.removeItem('auth_app_token');
      void this.router.navigate(['/']);
    }

    this.ref.close();
  }
}
