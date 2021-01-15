import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ServicesComponent } from './services.component';
import { ServicesRoutingModule } from './services-routing.module';
import { NbCardModule, NbRadioModule, NbButtonModule, NbInputModule, NbCheckboxModule, NbAccordionModule, NbIconModule } from '@nebular/theme';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { CapeSdkAngularModule } from '../../cape-sdk-angular/cape-sdk-angular.module';
import { DialogPersonalAttributesComponent } from './personalattributes-dialogue/dialog-personalattributes.component';
import { DialogPrivacyNoticeComponent } from './privacynotice/dialog-privacynotice.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';



@NgModule({
  imports: [
    CommonModule,
    NbCardModule,
    NbRadioModule,
    NbButtonModule,
    NbInputModule,
    ServicesRoutingModule,
    TranslateModule.forChild({}),
    CapeSdkAngularModule,
    NbCheckboxModule,
    NbAccordionModule,
    NbIconModule,
    FormsModule,
    ReactiveFormsModule
  ],
  declarations: [
    ServicesComponent,
    DialogPersonalAttributesComponent,
    DialogPrivacyNoticeComponent
  ],
  providers: [
  ],
  entryComponents: [
    ServicesComponent,
    DialogPersonalAttributesComponent
  ]
})
export class ServicesModule { }
