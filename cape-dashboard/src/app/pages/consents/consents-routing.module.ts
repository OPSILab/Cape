import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ConsentsComponent } from './consents.component';
import { ControlFlowComponent } from './control-flow/control-flow.component';
const routes: Routes = [
  {
    path: '',
    component: ConsentsComponent,

  },
  //{
  //  path: ':serviceId/:serviceName',
  //  component: ConsentsComponent
  //},
 
  //{
  //  path: ':consentId/:eventName',
  //  component: ConsentsComponent
  //},
  {
    path: 'controlflow',
    component: ControlFlowComponent,
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [
    RouterModule,
  ],
})

export class ConsentsRoutingModule { }
