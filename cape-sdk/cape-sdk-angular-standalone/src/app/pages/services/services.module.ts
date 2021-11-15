import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ServicesComponent } from './services.component';
import { ServicesRoutingModule } from './services-routing.module';
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
    ServicesRoutingModule,
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
  declarations: [ServicesComponent],
  providers: [],
  entryComponents: [ServicesComponent],
})
export class ServicesModule {}
