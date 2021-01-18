import { NgModule } from '@angular/core';
import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';
import { NbIconModule, NbActionsModule, NbUserModule, NbContextMenuModule, NbButtonModule } from '@nebular/theme';
import { CommonModule } from '@angular/common';
import { CapeSdkAngularModule } from '../cape-sdk-angular/cape-sdk-angular.module';
import { TranslateModule } from '@ngx-translate/core';
import { RouterModule } from '@angular/router';


@NgModule({
  imports: [CommonModule, RouterModule, NbIconModule, NbActionsModule, NbUserModule, NbUserModule,
    NbContextMenuModule, CapeSdkAngularModule, NbButtonModule, TranslateModule.forChild({})],
  declarations: [HeaderComponent, FooterComponent],
  exports: [HeaderComponent, FooterComponent]


})
export class LayoutModule { }
