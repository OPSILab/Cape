import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { ServiceLinkingComponent } from './service-linking.component';
import { AuthGuard } from '../_guards/auth.guard';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: ServiceLinkingComponent,
    canActivate: [AuthGuard],
    children: [],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ServiceLinkingRoutingModule {}
