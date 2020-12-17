import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { ServicesComponent } from './services.component';
import { AuthGuard } from '../../_guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: ServicesComponent
  },
  {
    path: 'my-wellness',
    loadChildren: () => import('./mywellness/mywellness.module')
      .then(m => m.MyWellnessModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'my-measurements',
    loadChildren: () => import('./mymeasurements/mymeasurements.module')
      .then(m => m.MyMeasurementsModule),
    canActivate: [AuthGuard]
  }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ServicesRoutingModule {
}
