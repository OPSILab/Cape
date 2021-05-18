import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { ConsentsComponent } from './consents.component';
import { ConsentsService } from './consents.service';
import { ConsentInfoLinkRenderComponent } from './consentInfoRender.component';
import { ConsentsRoutingModule } from './consents-routing.module';

import {
  NbAccordionModule,
  NbButtonModule,
  NbCardModule,
  NbSelectModule,
  NbSpinnerModule,
  NbIconModule,
  NbListModule,
  NbInputModule,
} from '@nebular/theme';
import { TranslateModule } from '@ngx-translate/core';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [
    CommonModule,
    Ng2SmartTableModule,
    NbCardModule,
    NbSelectModule,
    NbAccordionModule,
    NbButtonModule,
    NbSpinnerModule,
    ConsentsRoutingModule,
    NbIconModule,
    TranslateModule.forChild({}),
    NbListModule,
    ReactiveFormsModule,
    NbInputModule,
  ],
  declarations: [ConsentsComponent, ConsentInfoLinkRenderComponent],
  providers: [ConsentsService],
  entryComponents: [ConsentInfoLinkRenderComponent],
})
export class ConsentsModule {}
