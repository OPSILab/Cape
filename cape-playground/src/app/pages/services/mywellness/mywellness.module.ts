import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MyWellnessComponent } from './mywellness.component';
import { MyWellnessRoutingModule } from './mywellness-routing.module';
import { CholesterolComponent } from './cholesterol/cholesterol.component';
import { WeightComponent } from './weight/weight.component';
import { TranslateModule } from '@ngx-translate/core';
import { NbCardModule, NbRadioModule, NbButtonModule } from '@nebular/theme';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { CapeSdkAngularModule } from 'cape-sdk-angular';

@NgModule({
  declarations: [MyWellnessComponent, CholesterolComponent, WeightComponent],
  imports: [
    CommonModule,
    NbCardModule,
    NbRadioModule,
    NbButtonModule,
    MyWellnessRoutingModule,
    TranslateModule.forChild({}),
    CapeSdkAngularModule,
    NgxChartsModule,
  ],
})
export class MyWellnessModule {}
