import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MyMeasurementsComponent } from './mymeasurements.component';
import { CholesterolComponent } from './cholesterol/cholesterol.component';
import { WeightComponent } from './weight/weight.component';
import { MyMeasurementsRoutingModule } from './mymeasurements-routing.module';
import { TranslateModule } from '@ngx-translate/core';
import { NbCardModule, NbRadioModule, NbButtonModule } from '@nebular/theme';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { CapeSdkAngularModule } from 'cape-sdk-angular';

@NgModule({
  declarations: [MyMeasurementsComponent, CholesterolComponent, WeightComponent],
  imports: [
    CommonModule,
    NbCardModule,
    NbRadioModule,
    NbButtonModule,
    MyMeasurementsRoutingModule,
    TranslateModule.forChild({}),
    CapeSdkAngularModule,
    NgxChartsModule,
  ],
})
export class MyMeasurementsModule {}
