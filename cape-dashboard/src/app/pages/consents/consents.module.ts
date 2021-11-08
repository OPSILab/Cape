import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { NbDateFnsDateModule } from '@nebular/date-fns';
import { ConsentsRoutingModule } from './consents-routing.module';

import { NgbDropdownModule, NgbModalModule, NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';

import { ConsentsComponent } from './consents.component';
import { ConsentsService } from './consents.service';
import { ControlFlowComponent } from './control-flow/control-flow.component';
import { D3treeComponent } from './control-flow/d3-tree.component';

import {
  NbAccordionModule,
  NbButtonModule,
  NbCardModule,
  NbSelectModule,
  NbIconModule,
  NbTabsetModule,
  NbTooltipModule,
  NbToggleModule,
  NbDatepickerModule,
  NbCheckboxModule,
} from '@nebular/theme';
import { LinkedServicesService } from '../services/linkedServices/linkedServices.service';
import { MatExpansionModule } from '@angular/material/expansion';

@NgModule({
  imports: [
    CommonModule,
    ConsentsRoutingModule,
    NbDatepickerModule,
    NbDateFnsDateModule.forChild({
      parseOptions: { awareOfUnicodeTokens: true },
      formatOptions: { awareOfUnicodeTokens: true },
      format: 'dd.MM.yyyy',
    }),
    TranslateModule.forChild({}),
    NbAccordionModule,
    MatExpansionModule,
    NbButtonModule,
    NbCardModule,
    NbToggleModule,
    NbIconModule,
    NbTabsetModule,
    NbToggleModule,
    NbCheckboxModule,
    FormsModule,
    NgbDropdownModule,
    NgbModalModule,
    NgbCollapseModule,
    NbSelectModule,
    NbTooltipModule,
    ReactiveFormsModule,
  ],
  declarations: [ConsentsComponent, D3treeComponent, ControlFlowComponent],
  providers: [ConsentsService, LinkedServicesService],
})
export class ConsentsModule {}
