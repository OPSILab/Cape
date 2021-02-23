import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuditRoutingModule } from './audit-routing.module';
import { AuditLogsService } from './auditlogs.service';
import { NbDateFnsDateModule } from '@nebular/date-fns';
import { AuditLogsComponent } from './auditlogs.component';
import { NbAccordionModule, NbButtonModule, NbCardModule, NbSelectModule, NbDatepickerModule, NbBadgeModule } from '@nebular/theme';
import { TranslateModule } from '@ngx-translate/core';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [
    CommonModule,
    NbDatepickerModule,
    NbDateFnsDateModule.forChild({
      parseOptions: { awareOfUnicodeTokens: true },
      formatOptions: { awareOfUnicodeTokens: true },
      format: 'dd.MM.yyyy',
    }),
    AuditRoutingModule,
    NbCardModule,
    NbSelectModule,
    NbAccordionModule,
    NbButtonModule,
    NbBadgeModule,
    TranslateModule.forChild({}),
    ReactiveFormsModule,
  ],
  declarations: [AuditLogsComponent],
  providers: [AuditLogsService],
})
export class AuditLogsModule {}
