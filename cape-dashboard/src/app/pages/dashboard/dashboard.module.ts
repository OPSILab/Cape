import { NgModule } from '@angular/core';
import { NbCardModule, NbIconModule } from '@nebular/theme';

import { NgxChartsModule } from '@swimlane/ngx-charts';
import { ThemeModule } from '../../@theme/theme.module';
import { DashboardComponent } from './dashboard.component';
import { PersonalDataProcessingBarComponent } from './charts/personalDataProcessingBar.component';
import { PersonalDataStorageBarComponent } from './charts/personalDataStorageBar.component';
import { ProcessedDataPieComponent } from './charts/processedDataPie.component';
import { LegalBasisPieComponent } from './charts/legalBasisPie.component';
import { PurposeCategoryPieComponent } from './charts/purposeCategoryPie.component';
import { StatusCardComponent } from './status-card/status-card.component';
import { AuditLogsService } from '../auditlogs/auditlogs.service';
import { AvailableServicesService } from '../services/availableServices/availableServices.service';
import { TranslateModule } from '@ngx-translate/core';

const components = [
  PersonalDataProcessingBarComponent,
  PersonalDataStorageBarComponent,
  DashboardComponent,
  ProcessedDataPieComponent,
  LegalBasisPieComponent,
  PurposeCategoryPieComponent,
  StatusCardComponent,
];

@NgModule({
  imports: [NbCardModule, ThemeModule, NgxChartsModule, NbIconModule, TranslateModule],
  declarations: [...components],
  providers: [AuditLogsService, AvailableServicesService],
})
export class DashboardModule {}
