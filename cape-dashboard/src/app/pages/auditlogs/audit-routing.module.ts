import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuditLogsComponent } from './auditlogs.component';

const routes: Routes = [
  {
    path: '',
    component: AuditLogsComponent,
  },
  {
    path: ':filters',
    component: AuditLogsComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AuditRoutingModule {}
