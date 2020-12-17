import { NgModule } from '@angular/core';
import { NbCardModule, NbIconModule } from '@nebular/theme';

import { NgxChartsModule } from '@swimlane/ngx-charts';
import { ThemeModule } from '../../@theme/theme.module';
import { DashboardComponent } from './dashboard.component';
import { personalDataProcessingBar } from './charts/personalDataProcessingBar.component';
import { personalDataStorageBar } from './charts/personalDataStorageBar.component';
import { processedDataPie } from './charts/processedDataPie.component';
import { legalBasisPie } from './charts/legalBasisPie.component';
import { purposeCategoryPie } from './charts/purposeCategoryPie.component';
import { StatusCardComponent } from './status-card/status-card.component';
import { DashboardService } from './dashboard.service';



const components = [
  personalDataProcessingBar,
  personalDataStorageBar,
  DashboardComponent,
  processedDataPie,
  legalBasisPie,
  purposeCategoryPie,
  StatusCardComponent
];

@NgModule({
  imports: [
    NbCardModule,
    ThemeModule,
    NgxChartsModule,
    NbIconModule
  ],
  declarations: [
    ...components
  ],
  providers: [
    DashboardService
  ]
})
export class DashboardModule { }
