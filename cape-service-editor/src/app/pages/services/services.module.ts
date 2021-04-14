import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { AvailableServicesComponent } from './availableServices/availableServices.component';
import { AvailableServicesService } from './availableServices/availableServices.service';
import { ServicesRoutingModule } from './services-routing.module';
import { ServiceInfoRenderComponent } from './availableServices/serviceInfoRender.component';
import { RegisterButtonRenderComponent } from './availableServices/registerButtonRender.component';
import { EditorComponent } from './service-editor/editor.component';
import { DialogNamePromptComponent } from './service-editor/dialog-name-prompt/dialog-name-prompt.component';
import { DialogImportPromptComponent } from './service-editor/dialog-import-prompt/dialog-import-prompt.component';
import { EditButtonRenderComponent } from './availableServices/edit-button-render.component';
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
} from '@nebular/theme';
import { ActionsServiceMenuRenderComponent } from './availableServices/actionsServiceMenuRender.component';
import { ActivatedRouteSnapshot } from '@angular/router';

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
    TranslateModule.forChild({}),
  ],
  declarations: [
    AvailableServicesComponent,
    ServiceInfoRenderComponent,
    EditorComponent,
    DialogNamePromptComponent,
    DialogImportPromptComponent,
    EditButtonRenderComponent,
    RegisterButtonRenderComponent,
    ActionsServiceMenuRenderComponent,
  ],
  providers: [AvailableServicesService],
  entryComponents: [
    ActionsServiceMenuRenderComponent,
    RegisterButtonRenderComponent,
    ServiceInfoRenderComponent,
    DialogNamePromptComponent,
    DialogImportPromptComponent,
  ],
})
export class ServicesModule {}
