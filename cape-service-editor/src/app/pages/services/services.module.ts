import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { AvailableServicesComponent } from './availableServices/availableServices.component';
import { AvailableServicesService } from './availableServices/availableServices.service';
import { ServicesRoutingModule } from './services-routing.module';
import { ServiceInfoRenderComponent } from './availableServices/serviceInfoRender.component';
import { EditorComponent } from './service-editor/editor.component';
import { DialogNamePromptComponent } from './service-editor/dialog-name-prompt/dialog-name-prompt.component';
import { DialogImportPromptComponent } from './service-editor/dialog-import-prompt/dialog-import-prompt.component';
import { TranslateModule } from '@ngx-translate/core';
import {
  NbAccordionModule,
  NbButtonModule,
  NbCardModule,
  NbSelectModule,
  NbSpinnerModule,
  NbIconModule,
  NbToastrModule,
  NbContextMenuModule,
  NbInputModule,
  NbTabsetModule,
} from '@nebular/theme';
import { ActionsServiceMenuRenderComponent } from './availableServices/actionsServiceMenuRender.component';
import { ServiceStatusComponent } from './availableServices/service-status/service-status.component';

@NgModule({
  imports: [
    CommonModule,
    Ng2SmartTableModule,
    NbCardModule,
    NbSelectModule,
    NbAccordionModule,
    NbButtonModule,
    ServicesRoutingModule,
    NbSpinnerModule,
    NbIconModule,
    NbToastrModule,
    NbContextMenuModule,
    NbInputModule,
    NbTabsetModule,
    TranslateModule.forChild({}),
  ],
  declarations: [
    AvailableServicesComponent,
    ServiceInfoRenderComponent,
    EditorComponent,
    DialogNamePromptComponent,
    DialogImportPromptComponent,

    ActionsServiceMenuRenderComponent,
      ServiceStatusComponent,
  ],
  providers: [AvailableServicesService],
  entryComponents: [ActionsServiceMenuRenderComponent, ServiceInfoRenderComponent, DialogNamePromptComponent, DialogImportPromptComponent],
})
export class ServicesModule {}
