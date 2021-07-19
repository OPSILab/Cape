import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ge1Component } from './ge1.component';
import { Ge1RoutingModule } from './ge1-routing.module';
import { TranslateModule } from '@ngx-translate/core';
import { NbCardModule, NbRadioModule, NbButtonModule } from '@nebular/theme';
import { CapeSdkAngularModule } from 'cape-sdk-angular';
import { NgxChartsModule } from '@swimlane/ngx-charts';

@NgModule({
  declarations: [Ge1Component],
  imports: [CommonModule, NbCardModule, NbRadioModule, NbButtonModule, Ge1RoutingModule, TranslateModule.forChild({}), CapeSdkAngularModule, NgxChartsModule],
})
export class Ge1Module {}
