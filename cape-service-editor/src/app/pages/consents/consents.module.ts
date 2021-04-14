import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { ConsentsComponent } from './consents.component';
import { ConsentsService } from './consents.service';
import { ConsentInfoLinkRenderComponent } from './consentinfo-link-render.component';
import { ConsentsRoutingModule } from './consents-routing.module';
import { ConsentInfoModalComponent } from './consentInfo-modal/consentInfo-modal.component';

import { NbAccordionModule, NbButtonModule, NbCardModule, NbSelectModule, NbSpinnerModule, NbIconModule, NbListModule } from '@nebular/theme';
import { TranslateModule } from '@ngx-translate/core';

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
  ],
  declarations: [ConsentsComponent, ConsentInfoLinkRenderComponent, ConsentInfoModalComponent],
  providers: [ConsentsService],
  entryComponents: [ConsentInfoLinkRenderComponent, ConsentInfoModalComponent],
})
export class ConsentsModule {}
