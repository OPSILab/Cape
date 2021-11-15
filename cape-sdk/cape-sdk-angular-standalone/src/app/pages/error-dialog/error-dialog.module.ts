import { NgModule } from '@angular/core';
import { ErrorDialogComponent } from './error-dialog.component';
import { NbCardModule } from '@nebular/theme';
import { ErrorDialogService } from './error-dialog.service';
import { CommonModule, Location } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  imports: [CommonModule, NbCardModule, TranslateModule.forChild()],
  declarations: [ErrorDialogComponent],
  exports: [ErrorDialogComponent],
  entryComponents: [ErrorDialogComponent],
  providers: [ErrorDialogService, Location],
})
export class ErrorDialogModule {}
