/**
 * @license
 * Copyright Akveo. All Rights Reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { ThemeModule } from './@theme/theme.module';
import { NbEvaIconsModule } from '@nebular/eva-icons';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { NgxConfigureModule, NgxConfigureOptions } from 'ngx-configure';
import { AppOptions } from './app.options';
import { AuthGuard } from './_guards/auth.guard';
import { LoginPopupModule } from './loginPopup/loginPopup.module';
import { LoginModule } from './login/login.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

import {
  NbDatepickerModule,
  NbDialogModule,
  NbMenuModule,
  NbSidebarModule,
  NbToastrModule,
  NbWindowModule,
  NbCardModule,
  NbBadgeModule,
} from '@nebular/theme';
import { RememberMeResolve } from './loginPopup/loginPopup.resolve';
import { ErrorDialogModule } from './pages/error-dialog/error-dialog.module';
import { HttpConfigInterceptor } from './http.interceptor';
import { ReactiveFormsModule } from '@angular/forms';

export function createTranslateLoader(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,
    TranslateModule.forRoot({
      defaultLanguage: 'en',
      loader: {
        provide: TranslateLoader,
        useFactory: createTranslateLoader,
        deps: [HttpClient],
      },
    }),
    ThemeModule.forRoot(),
    NbEvaIconsModule,
    NbSidebarModule.forRoot(),
    NbMenuModule.forRoot(),
    NbCardModule,
    NbDatepickerModule.forRoot(),
    NbDialogModule.forRoot(),
    NbWindowModule.forRoot(),
    NbToastrModule.forRoot(),
    NgxConfigureModule.forRoot(),
    LoginPopupModule,
    LoginModule,
    NgbModule,
    ErrorDialogModule,
    NbBadgeModule,
    ReactiveFormsModule,
  ],
  providers: [
    { provide: NgxConfigureOptions, useClass: AppOptions },
    AuthGuard,
    RememberMeResolve,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpConfigInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
