import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ge2Component } from './ge2.component';
import { Ge2RoutingModule } from './ge2-routing.module';
import { TranslateModule } from '@ngx-translate/core';
import { NbCardModule, NbRadioModule, NbButtonModule } from '@nebular/theme';
import { CapeSdkAngularModule } from '../../../cape-sdk-angular/cape-sdk-angular.module';
import { NgxChartsModule } from '@swimlane/ngx-charts';


@NgModule({
  declarations: [Ge2Component],
  imports: [
    CommonModule,
    NbCardModule,
    NbRadioModule,
    NbButtonModule,
    Ge2RoutingModule,
    TranslateModule.forChild({}),
    CapeSdkAngularModule,
    NgxChartsModule
  ]
})
export class Ge2Module { }
