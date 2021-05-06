import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { WeightComponent } from './weight/weight.component';
import { CholesterolComponent } from './cholesterol/cholesterol.component';
import { MyWellnessComponent } from './mywellness.component';
import { AuthGuard } from 'src/app/auth/services/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: MyWellnessComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'weight',
    component: WeightComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'cholesterol',
    component: CholesterolComponent,
    canActivate: [AuthGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MyWellnessRoutingModule {}
