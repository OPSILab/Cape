import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { ServicesComponent } from './services.component';
// import { AuthGuard } from 'src/app/auth/services/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: ServicesComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ServicesRoutingModule {}
