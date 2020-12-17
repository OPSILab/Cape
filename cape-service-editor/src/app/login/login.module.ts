import { NgModule }      from '@angular/core';
import { CommonModule }  from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { LoginComponent } from './login.component';

import { LoginRoutingModule } from './login-routing.module';
import { NbLayoutModule, NbSidebarModule, NbCardModule, NbCheckboxModule, NbButtonModule } from '@nebular/theme';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    LoginRoutingModule,
    NbCardModule,
    NbLayoutModule,
    NbSidebarModule,
    NbCheckboxModule,
    NbButtonModule,
    TranslateModule.forChild({})
  ],
  declarations: [
    LoginComponent
  ]
})
export class LoginModule {}
