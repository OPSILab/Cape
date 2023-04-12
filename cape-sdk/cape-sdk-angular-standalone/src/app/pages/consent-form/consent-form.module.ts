import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConsentFormComponent2 } from './consent-form.component';
import { NbCardModule, NbRadioModule, NbButtonModule, NbInputModule, NbCheckboxModule, NbAccordionModule, NbIconModule } from '@nebular/theme';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { CapeSdkAngularModule } from 'cape-sdk-angular';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ErrorDialogModule } from '../error-dialog/error-dialog.module';
import { HttpLoaderFactory } from 'src/app/app.module';
import { HttpClient } from '@angular/common/http';

@NgModule({
  imports: [
    CommonModule,
    NbCardModule,
    NbRadioModule,
    NbButtonModule,
    NbInputModule,
    TranslateModule.forChild({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },
    }),
    NbCheckboxModule,
    NbAccordionModule,
    NbIconModule,
    FormsModule,
    ReactiveFormsModule,
    CapeSdkAngularModule,
    ErrorDialogModule,
  ],
  declarations: [ConsentFormComponent2],
  providers: [],
  entryComponents: [ConsentFormComponent2],
})
export class ConsentForm2Module {}
