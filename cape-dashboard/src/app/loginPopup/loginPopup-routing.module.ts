import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';

import { LoginPopupComponent } from './loginPopup.component';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: LoginPopupComponent,
    children: [],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LoginPopupRoutingModule {}
