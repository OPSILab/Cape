import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NbLayoutModule, NbButtonModule, NbContextMenuModule, NbMenuModule, NbToastrModule, NbDialogModule, NbCardModule, NbCheckboxModule, NbAccordionModule } from '@nebular/theme';
import { CapeSdkAngularComponent } from './cape-sdk-angular.component';
import { CapeSdkAngularService } from './cape-sdk-angular.service';
import { TranslateModule } from '@ngx-translate/core';
import { ErrorDialogModule } from './error-dialog/error-dialog.module';
import { ConsentFormComponent } from './consent-form/consent-form.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';


@NgModule({
  imports: [
    CommonModule,
    NbLayoutModule,
    NbButtonModule,
    NbCheckboxModule,
    NbMenuModule,
    NbAccordionModule,
    NbDialogModule.forChild(),
    NbCardModule,
    NbContextMenuModule,
    NbToastrModule,
    ErrorDialogModule,
    TranslateModule.forChild({
 
      
    }),
    ReactiveFormsModule 
  ],
  declarations: [CapeSdkAngularComponent, ConsentFormComponent],
  providers: [CapeSdkAngularService, FormBuilder],
  exports: [CapeSdkAngularComponent]
})
export class CapeSdkAngularModule {



}
