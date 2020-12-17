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
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { CommonModule } from '@angular/common';
import { AccountRoutingModule } from './account-routing.module';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { AccountComponent } from './account.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbDropdownModule, NgbModalModule, NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from './account.service';
  
  export function createTranslateLoader(http: HttpClient) {
    return new TranslateHttpLoader(http, './assets/i18n/', '.json');
  }

@NgModule({
 imports: [
    CommonModule,
    AccountRoutingModule,
    TranslateModule.forChild({
      loader: {
        provide: TranslateLoader,
        useFactory: (createTranslateLoader),
        deps: [HttpClient]
      }
    }),
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
    TranslateModule.forChild({
      loader: {
        provide: TranslateLoader,
        useFactory: (createTranslateLoader),
        deps: [HttpClient]
      }
    }),
    NbCheckboxModule
  ],
  declarations: [
    AccountComponent,
  ],
  providers: [
    AccountService
  ]
})

export class AccountModule{

}