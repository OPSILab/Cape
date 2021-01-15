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
    path: 'GE_1',
    loadChildren: () => import('./ge1/ge1.module')
      .then(m => m.Ge1Module),
    canActivate: [AuthGuard]
  },
  {
    path: 'GE_2',
    loadChildren: () => import('./ge2/ge2.module') 
      .then(m => m.Ge2Module),
    canActivate: [AuthGuard]
  }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ServicesRoutingModule {
}
