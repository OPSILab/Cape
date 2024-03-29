import { Injectable } from '@angular/core';
import { ErrorDialogComponent } from './error-dialog.component';
import { NbDialogService } from '@nebular/theme';

@Injectable()
export class ErrorDialogService {
  constructor(private modalService: NbDialogService) {}

  openErrorDialog(error: any) {
    this.modalService.open(ErrorDialogComponent, {
      context: {
        error: error,
      },
      hasScroll: false,
    });
  }
}
