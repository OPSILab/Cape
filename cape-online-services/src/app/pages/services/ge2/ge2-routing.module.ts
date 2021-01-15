import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { Ge2Component } from './ge2.component';
import { AuthGuard } from '../../../_guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: Ge2Component
  }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class Ge2RoutingModule {
}
