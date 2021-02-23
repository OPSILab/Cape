import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ServiceLinkingComponent } from './service-linking.component';
import { NbCardModule, NbButtonModule, NbLayoutModule, NbSidebarModule, NbToastrModule } from '@nebular/theme';
import { ServiceLinkingRoutingModule } from './service-linking-routing.module';
import { ServiceLinkingService } from './service-linking.service';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

export function createTranslateLoader(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [ServiceLinkingComponent],
  imports: [
    CommonModule,
    NbLayoutModule,
    NbSidebarModule,
    NbCardModule,
    NbButtonModule,
    NbToastrModule,
    ServiceLinkingRoutingModule,
    TranslateModule.forChild({
      loader: {
        provide: TranslateLoader,
        useFactory: createTranslateLoader,
        deps: [HttpClient],
      },
    }),
  ],
  providers: [ServiceLinkingService],
})
export class ServiceLinkingModule {}
