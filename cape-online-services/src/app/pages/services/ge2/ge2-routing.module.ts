import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { Ge2Component } from './ge2.component';
import { AuthGuard } from 'src/app/auth/services/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: Ge2Component,
    canActivate: [AuthGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class Ge2RoutingModule {}
