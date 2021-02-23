import { ExtraOptions, RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { RememberMeResolve } from './loginPopup/loginPopup.resolve';

const routes: Routes = [
  {
    path: 'login',
    loadChildren: () => import('./login/login.module').then((m) => m.LoginModule),
  },
  {
    path: 'loginPopup',
    loadChildren: () => import('./loginPopup/loginPopup.module').then((m) => m.LoginPopupModule),
    resolve: {
      rememberMe: RememberMeResolve,
    },
  },
  {
    path: 'pages',
    loadChildren: () => import('./pages/pages.module').then((m) => m.PagesModule),
  },
  {
    path: 'serviceLinking',
    loadChildren: () => import('./service-linking/service-linking.module').then((m) => m.ServiceLinkingModule),
  },
  { path: '', redirectTo: 'pages', pathMatch: 'full' },
  { path: '**', redirectTo: 'pages' },
];

const config: ExtraOptions = {
  useHash: false,
  enableTracing: false,
};

@NgModule({
  imports: [RouterModule.forRoot(routes, config)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
