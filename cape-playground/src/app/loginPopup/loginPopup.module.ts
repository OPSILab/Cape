import { NgModule }      from '@angular/core';
import { CommonModule }  from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NbLayoutModule, NbSidebarModule, NbCardModule, NbCheckboxModule, NbButtonModule } from '@nebular/theme';

import { LoginPopupComponent } from './loginPopup.component';
import { LoginPopupRoutingModule } from './loginPopup-routing.module';
import { TranslateModule } from '@ngx-translate/core';


@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    LoginPopupRoutingModule,
    NbCardModule,
    NbLayoutModule,
    NbSidebarModule,
    NbCheckboxModule,
    NbButtonModule,
    TranslateModule.forChild({})
  ],
  declarations: [
    LoginPopupComponent,
  ]
})
export class LoginPopupModule {}
