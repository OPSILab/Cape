import { NgModule } from '@angular/core';
import { ErrorDialogComponent } from './error-dialog.component';
import { NbCardModule, NbButtonModule } from '@nebular/theme';
import { ErrorDialogService } from './error-dialog.service';
import { CommonModule, Location } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';


@NgModule(
  {
    imports: [
      CommonModule,
      NbCardModule,
      NbButtonModule,
      TranslateModule
    ],
    declarations: [ErrorDialogComponent],
    exports: [ErrorDialogComponent],
    entryComponents: [
      ErrorDialogComponent
    ],
    providers: [ErrorDialogService, Location]
  }
) export class ErrorDialogModule { }
