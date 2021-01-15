import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { WeightComponent } from './weight/weight.component';
import { CholesterolComponent } from './cholesterol/cholesterol.component';
import { MyMeasurementsComponent } from './mymeasurements.component';
import { AuthGuard } from '../../../_guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: MyMeasurementsComponent
  },
  {
    path: 'weight',
    component: WeightComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'cholesterol',
    component: CholesterolComponent,
    canActivate: [AuthGuard]
  }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MyMeasurementsRoutingModule {
}
