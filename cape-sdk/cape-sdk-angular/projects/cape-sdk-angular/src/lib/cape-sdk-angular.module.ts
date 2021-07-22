import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  NbLayoutModule,
  NbButtonModule,
  NbContextMenuModule,
  NbMenuModule,
  NbToastrModule,
  NbDialogModule,
  NbCardModule,
  NbCheckboxModule,
  NbAccordionModule,
  NbInputModule,
} from '@nebular/theme';
import { CapeSdkAngularComponent } from './cape-sdk-angular.component';
import { CapeSdkAngularService } from './cape-sdk-angular.service';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { CapeSdkDialogModule } from './cape-sdk-dialog/cape-sdk-dialog.module';
import { ConsentFormComponent } from './consent-form/consent-form.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http, '/assets/cape/i18n/', '.json');
}

// declare var System: System;
// interface System {
//   import(request: string): Promise<any>;
// }
// export class WebpackTranslateLoader implements TranslateLoader {
//   getTranslation(lang: string): Observable<any> {
//     return from(System.import(`../assets/i18n/${lang}.json`));
//   }
// }

@NgModule({
  imports: [
    CommonModule,
    NbLayoutModule,
    NbButtonModule,
    NbCheckboxModule,
    NbInputModule,
    NbMenuModule,
    NbAccordionModule,
    NbDialogModule.forChild(),
    NbCardModule,
    NbContextMenuModule,
    NbToastrModule,
    CapeSdkDialogModule,
    ReactiveFormsModule,
    TranslateModule.forChild({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },
      defaultLanguage: 'en',
      isolate: true,
    }),
  ],
  declarations: [CapeSdkAngularComponent, ConsentFormComponent],
  providers: [CapeSdkAngularService, FormBuilder],
  exports: [CapeSdkAngularComponent, TranslateModule],
})
export class CapeSdkAngularModule {}
