import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RememberMeResolve } from './loginPopup/loginPopup.resolve';

const routes: Routes = [
  {
    path: 'login',
    loadChildren: () => import('./login/login.module')
      .then(m => m.LoginModule)
  },
  {
    path: 'loginPopup',
    loadChildren: () => import('./loginPopup/loginPopup.module')
      .then(m => m.LoginPopupModule),
    resolve: {
      rememberMe: RememberMeResolve
    }
  },
  {
    path: 'pages',
    loadChildren: () => import('./pages/pages.module')
      .then(m => m.PagesModule)
  },
  { path: '', redirectTo: 'pages', pathMatch: 'full' },
  { path: '**', redirectTo: 'pages' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { enableTracing: true } )],
  exports: [RouterModule]
})
export class AppRoutingModule { }
