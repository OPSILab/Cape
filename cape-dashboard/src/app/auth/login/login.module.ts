import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoginComponent } from './login.component';
import { LoginRoutingModule } from './login-routing.module';
import { NbLayoutModule, NbSidebarModule, NbCardModule, NbCheckboxModule, NbButtonModule } from '@nebular/theme';
import { TranslateModule } from '@ngx-translate/core';
import { LoginService } from './login.service';
import { LoginPopupComponent } from './loginPopup/loginPopup.component';
import { AccountService } from '../../pages/account/account.service';

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
    TranslateModule.forChild({}),
  ],
  declarations: [LoginComponent, LoginPopupComponent],
  providers: [LoginService, AccountService],
})
export class LoginModule {}
