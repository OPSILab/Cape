import { NgModule } from '@angular/core';
import { NbMenuModule, NbCardModule, NbThemeModule, NbLayoutModule, NbActionsModule, NbSidebarModule, NbContextMenuModule, NbButtonModule, NbSelectModule, NbIconModule, NbUserModule } from '@nebular/theme';
import { PagesComponent } from './pages.component';
import { PagesRoutingModule } from './pages-routing.module';

import { TranslateModule } from "@ngx-translate/core";
import { LayoutModule } from '../layout/layout.module';
import { ErrorDialogModule } from './error-dialog/error-dialog.module';


@NgModule({
  imports: [
    PagesRoutingModule,
    NbLayoutModule,
    NbMenuModule,
    NbSidebarModule,
    NbButtonModule,
    NbSelectModule,
    NbThemeModule,
    LayoutModule,
    ErrorDialogModule
  ],
  declarations: [
    PagesComponent
  ]
})
export class PagesModule {
}
