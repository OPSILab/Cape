import { NgModule } from '@angular/core';
import { NbMenuModule } from '@nebular/theme';

import { ThemeModule } from '../@theme/theme.module';
import { PagesComponent } from './pages.component';
import { PagesRoutingModule } from './pages-routing.module';
import { AccountComponent } from './account/account.component';

@NgModule({
  imports: [PagesRoutingModule, ThemeModule, NbMenuModule],
  declarations: [PagesComponent],// AccountComponent],
  providers: [],
})
export class PagesModule {}
