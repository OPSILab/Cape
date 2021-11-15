import { NgModule } from '@angular/core';
import { NbMenuModule, NbThemeModule, NbLayoutModule, NbSidebarModule, NbButtonModule, NbSelectModule } from '@nebular/theme';
import { PagesComponent } from './pages.component';
import { PagesRoutingModule } from './pages-routing.module';

import { LayoutModule } from '../layout/layout.module';
import { ErrorDialogModule } from './error-dialog/error-dialog.module';

@NgModule({
  imports: [PagesRoutingModule, NbLayoutModule, NbMenuModule, NbSidebarModule, NbButtonModule, NbSelectModule, NbThemeModule, LayoutModule, ErrorDialogModule],
  declarations: [PagesComponent],
})
export class PagesModule {}
