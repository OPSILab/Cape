import { Injectable } from '@angular/core';
import { ErrorDialogComponent } from './error-dialog.component';
import { NbDialogService } from '@nebular/theme';
import { Callback, CapeSdkDialogComponent, dialogType } from './cape-sdk-dialog.component';

@Injectable()
export class CapeSdkDialogService {
  constructor(private modalService: NbDialogService) {}

  openErrorDialog(error: unknown, dashboardUrl?: string): void {
    this.modalService.open(ErrorDialogComponent, {
      context: {
        error: error,
        dashboardUrl: dashboardUrl,
      },
      hasScroll: true,
    });
  }

  openCapeDialog(type: dialogType, callback?: Callback, callbackParams?: Array<unknown>, title?: string, message?: string): void {
    this.modalService.open(CapeSdkDialogComponent, {
      context: {
        dialogType: type,
        title: title,
        message: message,
        actionCallback: callback,
        actionCallbackParams: callbackParams,
      },
      hasScroll: true,
    });
  }
}
