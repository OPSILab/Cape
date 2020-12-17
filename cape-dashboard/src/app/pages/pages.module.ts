import { NgModule } from '@angular/core';
import { NbMenuModule } from '@nebular/theme';

import { ThemeModule } from '../@theme/theme.module';
import { PagesComponent } from './pages.component';
import { PagesMenuTranslator } from './pages-menu-translator';
import { PagesRoutingModule } from './pages-routing.module';
import { DashboardModule } from './dashboard/dashboard.module';

@NgModule({
  imports: [
    PagesRoutingModule,
    ThemeModule,
    NbMenuModule,
    DashboardModule
  ],
  declarations: [
    PagesComponent,
  ],
  providers: [
    PagesMenuTranslator,
  ]
})
export class PagesModule {
}
