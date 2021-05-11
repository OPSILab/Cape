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
  NbIconModule,
} from '@nebular/theme';
import { ErrorDialogModule } from './pages/error-dialog/error-dialog.module';
import { HttpConfigInterceptor } from './http.interceptor';
import { ReactiveFormsModule } from '@angular/forms';
import { OidcUserInformationService } from './auth/services/oidc-user-information.service';
import { NbRoleProvider, NbSecurityModule } from '@nebular/security';

import { NbAuthModule, NbOAuth2AuthStrategy } from '@nebular/auth';
import { OidcJWTToken } from './auth/model/oidc';
import { AuthGuard } from './auth/services/auth.guard';
import { LoginModule } from './auth/login/login.module';

export function createTranslateLoader(http: HttpClient) {
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
    LoginModule,
    NgbModule,
    ErrorDialogModule,
    NbBadgeModule,
    ReactiveFormsModule,
    NbSecurityModule.forRoot({
      accessControl: {
        DATA_CONTROLLER: {
          view: '*',
        },
        DATA_SUBJECT: {
          view: ['dashboard'],
        },
        USER: {
          view: ['dashboard'],
        },
      },
    }),
    NbAuthModule.forRoot({
      strategies: [
        NbOAuth2AuthStrategy.setup({
          name: 'oidc',
          clientId: '',
          token: {
            class: OidcJWTToken,
          },
        }),
      ],
    }),
  ],
  providers: [
    { provide: NgxConfigureOptions, useClass: AppOptions },
    { provide: NbRoleProvider, useClass: OidcUserInformationService },
    AuthGuard,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpConfigInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
