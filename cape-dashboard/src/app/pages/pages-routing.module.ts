import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { PagesComponent } from './pages.component';
import { AuthGuard } from '../_guards/auth.guard';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TranslateModule } from '@ngx-translate/core';

const routes: Routes = [
  {
    path: '',
    component: PagesComponent,
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent,
        canActivate: [AuthGuard]
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
        canActivate: [AuthGuard]
      },
      {
        path: 'auditlogs',
        loadChildren: () => import('./auditlogs/auditlogs.module')
          .then(m => m.AuditLogsModule),
        canActivate: [AuthGuard]
      },
      {
        path: 'services',
        loadChildren: () => import('./services/services.module')
          .then(m => m.ServicesModule),
        canActivate: [AuthGuard]
      },
      {
        path: 'consents',
        loadChildren: () => import('./consents/consents.module')
          .then(m => m.ConsentsModule),
        canActivate: [AuthGuard]
      },
      {
        path: 'account',
        loadChildren: () => import('./account/account.module')
          .then(m => m.AccountModule),
        canActivate: [AuthGuard]
      }
    ],
  }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PagesRoutingModule {
}
