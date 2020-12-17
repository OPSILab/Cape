import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { AvailableServicesComponent } from './availableServices/availableServices.component';
import { AvailableServicesService } from './availableServices/availableServices.service';
import { ServiceInfoRenderComponent } from './availableServices/serviceInfoRender.component';
import { LinkButtonRenderComponent } from './availableServices/linkButtonRender.component';
import { ServicesRoutingModule } from './services-routing.module';
import { ServiceInfoModalComponent } from './availableServices/serviceInfo-modal/serviceInfo-modal.component';
import { LinkedServicesComponent } from './linkedServices/linkedServices.component';
import { LinkedServiceInfoModalComponent } from './linkedServices/linkedServiceInfo-modal/linkedServiceInfo-modal.component';
import { LinkedServiceInfoRenderComponent } from './linkedServices/linkedServiceInfoRender.component';
import { EnableServiceLinkButtonRenderComponent } from './linkedServices/enableServiceLinkButtonRender.component';
import { LinkedServicesService } from './linkedServices/linkedServices.service';
import { TranslateModule } from '@ngx-translate/core';

import {
  NbAccordionModule,
  NbButtonModule,
  NbCardModule,
  NbSelectModule,
  NbToggleModule,
  NbContextMenuModule,
  NbIconModule,

  NbToastrModule
} from '@nebular/theme';
import { ActionsServiceLinkMenuRenderComponent } from './linkedServices/actionsServiceLinkMenuRender.component';
import { ServiceUrlButtonRenderComponent } from './linkedServices/serviceUrlButtonRender.component';


@NgModule({
  imports: [
    CommonModule,
    Ng2SmartTableModule,
    NbCardModule,
    NbSelectModule,
    NbAccordionModule,
    NbButtonModule,
    NbToggleModule,
    NbToastrModule,
    NbIconModule,
    NbContextMenuModule,
    ServicesRoutingModule,
    TranslateModule.forChild({})
  ],
  declarations: [
    AvailableServicesComponent,
    LinkButtonRenderComponent,
    ServiceInfoRenderComponent,
    ServiceInfoModalComponent,
    LinkedServicesComponent,
    LinkedServiceInfoModalComponent,
    LinkedServiceInfoRenderComponent,
    EnableServiceLinkButtonRenderComponent,
    ActionsServiceLinkMenuRenderComponent,
    ServiceUrlButtonRenderComponent
  ],
  providers: [
    AvailableServicesService,
    LinkedServicesService
  ],
  entryComponents: [
    LinkButtonRenderComponent,
    ServiceInfoRenderComponent,
    ServiceInfoModalComponent,
    LinkedServicesComponent,
    LinkedServiceInfoModalComponent,
    LinkedServiceInfoRenderComponent,
    EnableServiceLinkButtonRenderComponent,
    ActionsServiceLinkMenuRenderComponent,
    ServiceUrlButtonRenderComponent
  ]
})
export class ServicesModule { }
