import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ServicesComponent } from './services.component';
import { ServicesRoutingModule } from './services-routing.module';
import { NbCardModule, NbRadioModule, NbButtonModule } from '@nebular/theme';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';

@NgModule({
  imports: [CommonModule, NbCardModule, NbRadioModule, NbButtonModule, ServicesRoutingModule, TranslateModule.forChild({})],
  declarations: [ServicesComponent],
  providers: [],
  entryComponents: [ServicesComponent],
})
export class ServicesModule {}
