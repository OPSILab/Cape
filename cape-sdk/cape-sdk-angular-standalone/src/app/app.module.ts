import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
  NbButtonModule,
  NbCardModule,
  NbCheckboxModule,
  NbDialogModule,
  NbGlobalLogicalPosition,
  NbLayoutModule,
  NbMenuModule,
  NbSidebarModule,
  NbThemeModule,
  NbToastrModule,
} from '@nebular/theme';

import { NbEvaIconsModule } from '@nebular/eva-icons';

import { NgxConfigureModule, NgxConfigureOptions } from 'ngx-configure';
import { AppOptions } from './app.options';
import { HttpClientModule, HttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { MultiTranslateHttpLoader } from 'ngx-translate-multi-http-loader';
import { HttpConfigInterceptor } from './http.interceptor';
import { ErrorDialogModule } from './pages/error-dialog/error-dialog.module';
import { LoginModule } from './auth/login/login.module';
import { NbRoleProvider } from '@nebular/security';
import { NbAuthModule, NbOAuth2AuthStrategy } from '@nebular/auth';
import { OidcJWTToken } from './auth/model/oidc';
import { OidcUserInformationService } from './auth/services/oidc-user-information.service';
import { AuthGuard } from './auth/services/auth.guard';

//export function createTranslateLoader(http: HttpClient) {
//  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
//}

export function HttpLoaderFactory(http: HttpClient) {
  return new MultiTranslateHttpLoader(http, [
    { prefix: './assets/i18n/', suffix: '.json' },
    { prefix: './assets/cape/i18n/', suffix: '.json' },
  ]);
}

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    NgxConfigureModule.forRoot(),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },
      defaultLanguage: 'en',
    }),
    NbThemeModule.forRoot({ name: 'cape' }),
    NbLayoutModule,
    NbMenuModule.forRoot(),
    NbSidebarModule.forRoot(),
    NbEvaIconsModule,
    NbDialogModule.forRoot(),
    ErrorDialogModule,
    NbCardModule,
    NbCheckboxModule,
    NbButtonModule,
    NbToastrModule.forRoot({ position: NbGlobalLogicalPosition.BOTTOM_END }),
    LoginModule,
    // NbSecurityModule.forRoot({
    //   accessControl: {
    //     ADMIN: {
    //       view: '*',
    //       parent: 'user',
    //     },
    //     USER: {
    //       view: ['dashboard'],
    //     },
    //   },
    // }),
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
