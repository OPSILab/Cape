import { NgModule } from '@angular/core';
import {
  NbAccordionModule,
  NbButtonModule,
  NbCardModule,
  NbSelectModule,
  NbIconModule,
  NbTabsetModule,
  NbToggleModule,
  NbInputModule,
  NbRadioModule,
  NbWindowModule,
  NbCheckboxModule,
  NbUserModule,
} from '@nebular/theme';
import { CommonModule } from '@angular/common';
import { AccountRoutingModule } from './account-routing.module';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { AccountComponent } from './account.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbDropdownModule, NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from './account.service';
import { HttpClient } from '@angular/common/http';


@NgModule({
  imports: [
    CommonModule,
    AccountRoutingModule,
    NbAccordionModule,
    NbButtonModule,
    NbCardModule,
    NbToggleModule,
    NbIconModule,
    NbTabsetModule,
    NbToggleModule,
    FormsModule,
    NgbDropdownModule,
    NgbCollapseModule,
    NbSelectModule,
    NbInputModule,
    NbRadioModule,
    ReactiveFormsModule,
    NbWindowModule.forChild(),
    NbCheckboxModule,
    NbUserModule,
    TranslateModule.forChild({}),
  ],
  declarations: [AccountComponent],
  providers: [AccountService],
})
export class AccountModule {}
