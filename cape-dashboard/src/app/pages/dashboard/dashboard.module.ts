import { NgModule } from '@angular/core';
import { NbCardModule, NbIconModule } from '@nebular/theme';

import { NgxChartsModule } from '@swimlane/ngx-charts';
import { ThemeModule } from '../../@theme/theme.module';
import { DashboardComponent } from './dashboard.component';
import { PersonalDataProcessingBar } from './charts/personalDataProcessingBar.component';
import { PersonalDataStorageBar } from './charts/personalDataStorageBar.component';
import { ProcessedDataPie } from './charts/processedDataPie.component';
import { LegalBasisPie } from './charts/legalBasisPie.component';
import { PurposeCategoryPie } from './charts/purposeCategoryPie.component';
import { StatusCardComponent } from './status-card/status-card.component';
import { DashboardService } from './dashboard.service';
import { AuditLogsService } from '../auditlogs/auditlogs.service';
import { AvailableServicesService } from '../services/availableServices/availableServices.service';
import { TranslateModule } from '@ngx-translate/core';



const components = [
  PersonalDataProcessingBar,
  PersonalDataStorageBar,
  DashboardComponent,
  ProcessedDataPie,
  LegalBasisPie,
  PurposeCategoryPie,
  StatusCardComponent
];

@NgModule({
  imports: [
    NbCardModule,
    ThemeModule,
    NgxChartsModule,
    NbIconModule,
    TranslateModule
  ],
  declarations: [
    ...components
  ],
  providers: [
    DashboardService,
    AuditLogsService,
    AvailableServicesService
  ]
})
export class DashboardModule { }
