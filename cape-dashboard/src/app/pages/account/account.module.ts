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
} from '@nebular/theme';
import { CommonModule } from '@angular/common';
import { AccountRoutingModule } from './account-routing.module';
import { TranslateModule } from '@ngx-translate/core';
import { AccountComponent } from './account.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbDropdownModule, NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from './account.service';

@NgModule({
  imports: [
    CommonModule,
    AccountRoutingModule,
    TranslateModule.forChild({}),
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
    NbCheckboxModule
  ],
  declarations: [
    AccountComponent,
  ],
  providers: [
    AccountService
  ]
})

export class AccountModule {

}
