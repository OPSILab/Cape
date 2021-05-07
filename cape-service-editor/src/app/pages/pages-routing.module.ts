import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { PagesComponent } from './pages.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthGuard } from '../auth/services/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: PagesComponent,
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent,
        canActivate: [AuthGuard],
      },

      {
        path: '',
        redirectTo: 'services/availableServices',
        pathMatch: 'full',
        canActivate: [AuthGuard],
      },
      {
        path: 'account',
        loadChildren: () => import('./account/account.module').then((m) => m.AccountModule),
        canActivate: [AuthGuard],
      },
      {
        path: 'consents',
        loadChildren: () => import('./consents/consents.module').then((m) => m.ConsentsModule),
        canActivate: [AuthGuard],
      },
      {
        path: 'services',
        loadChildren: () => import('./services/services.module').then((m) => m.ServicesModule),
        canActivate: [AuthGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PagesRoutingModule {}
