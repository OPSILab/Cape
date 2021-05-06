import { NgModule } from '@angular/core';
import { ErrorDialogComponent } from './error-dialog.component';
import { NbCardModule } from '@nebular/theme';
import { ErrorDialogService } from './error-dialog.service';
import { CommonModule, Location } from '@angular/common';

@NgModule({
  imports: [CommonModule, NbCardModule],
  declarations: [ErrorDialogComponent],
  exports: [ErrorDialogComponent],
  entryComponents: [ErrorDialogComponent],
  providers: [ErrorDialogService, Location],
})
export class ErrorDialogModule {}
