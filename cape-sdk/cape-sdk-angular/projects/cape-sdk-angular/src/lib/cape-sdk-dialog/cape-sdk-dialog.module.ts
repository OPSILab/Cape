import { NgModule } from '@angular/core';
import { ErrorDialogComponent } from './error-dialog.component';
import { NbCardModule, NbButtonModule } from '@nebular/theme';
import { CapeSdkDialogService } from './cape-sdk-dialog.service';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { CapeSdkDialogComponent } from './cape-sdk-dialog.component';

@NgModule({
  imports: [CommonModule, NbCardModule, NbButtonModule, TranslateModule.forChild()],
  declarations: [ErrorDialogComponent, CapeSdkDialogComponent],
  exports: [ErrorDialogComponent, CapeSdkDialogComponent],
  entryComponents: [ErrorDialogComponent, CapeSdkDialogComponent],
  providers: [CapeSdkDialogService],
})
export class CapeSdkDialogModule {}
