import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AvailableServicesComponent } from './availableServices/availableServices.component';
import { LinkedServicesComponent } from './linkedServices/linkedServices.component';

const routes: Routes = [
  {
    path: 'availableServices',
    component: AvailableServicesComponent,
  },
  {
    path: 'linkedServices',
    component: LinkedServicesComponent,
  },
  {
    path: 'linkedServices/:serviceId/:serviceName',
    component: LinkedServicesComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ServicesRoutingModule {}
