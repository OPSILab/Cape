import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { Ge1Component } from './ge1.component';
import { AuthGuard } from '../../../_guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: Ge1Component,
    canActivate: [AuthGuard]
  }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class Ge1RoutingModule {
}
