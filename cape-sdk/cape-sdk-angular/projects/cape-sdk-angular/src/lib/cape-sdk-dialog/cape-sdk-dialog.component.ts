import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NbDialogRef } from '@nebular/theme';
import { CapeSdkAngularService } from '../cape-sdk-angular.service';

export enum dialogType {
  generic = 'generic',
  enableServiceLink = 'enableServiceLink',
}
export type Callback = (p1?: unknown, p2?: unknown, p3?: unknown, p4?: unknown, p5?: unknown) => unknown;

@Component({
  selector: 'cape-dialog',
  template: `
    <div [ngSwitch]="dialogType">
      <ng-container *ngSwitchCase="'enableServiceLink'">
        <nb-card>
          <nb-card-header class="d-flex justify-content-between">
            <h5 class="h5 mr-3">{{ 'general.services.enableServiceLinkLabel' | translate }}</h5>
            <button nbButton ghost shape="rectangle" size="small" (click)="ref.close()">
              <i class="material-icons">close</i>
            </button>
          </nb-card-header>
          <nb-card-body class="m-3">
            {{ 'general.services.askEnableServiceLink' | translate }}
          </nb-card-body>
          <nb-card-footer class="d-flex justify-content-end">
            <button nbButton ghost shape="rectangle" size="medium" (click)="applyCallback(); ref.close()">
              {{ 'general.services.enableServiceLinkButton' | translate }}
            </button>
            <button nbButton ghost shape="rectangle" size="medium" (click)="ref.close()">
              {{ 'general.services.closeButton' | translate }}
            </button>
          </nb-card-footer>
        </nb-card>
      </ng-container>

      <ng-container *ngSwitchDefault>
        <nb-card>
          <nb-card-header class="d-flex justify-content-between">
            <h5 class="h5 mr-3">{{ title | translate }}</h5>
            <button nbButton ghost shape="rectangle" size="small" (click)="ref.close()">
              <i class="material-icons">close</i>
            </button>
          </nb-card-header>
          <nb-card-body class="m-3">
            {{ message | translate }}
          </nb-card-body>
          <nb-card-footer class="d-flex justify-content-end">
            <button nbButton ghost shape="rectangle" size="medium" (click)="applyCallback(); ref.close()">
              {{ actionButtonLabel | translate }}
            </button>
            <button nbButton ghost shape="rectangle" size="medium" (click)="ref.close()">
              {{ 'general.services.closeButton' | translate }}
            </button>
          </nb-card-footer>
        </nb-card>
      </ng-container>
    </div>
  `,
})
export class CapeSdkDialogComponent {
  dialogType: dialogType;
  title: string;
  message: string;
  actionButtonLabel: string;
  actionCallback: Callback;
  actionCallbackParams: Array<unknown>;
  dashboardUrl: string;

  constructor(public ref: NbDialogRef<unknown>, private router: Router) {}

  openCapeDashboard(): void {
    window.open(`${this.dashboardUrl}/pages/services/availableServices`, '_blank');
  }

  applyCallback(): unknown {
    if (this.actionCallback) return this.actionCallback(...this.actionCallbackParams);
  }
}
