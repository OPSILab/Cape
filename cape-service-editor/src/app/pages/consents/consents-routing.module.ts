import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ConsentsComponent } from './consents.component';

const routes: Routes = [
  {
    path: 'register',
    component: ConsentsComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ConsentsRoutingModule {}
