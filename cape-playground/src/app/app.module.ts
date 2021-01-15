import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NbThemeModule, NbLayoutModule, NbCardModule, NbSidebarModule, NbCheckboxModule, NbButtonModule, NbDialogModule, NbMenuModule, NbToastrModule, NbPosition, NbGlobalLogicalPosition } from '@nebular/theme';

import { NbEvaIconsModule } from '@nebular/eva-icons';

import { NgxConfigureModule, NgxConfigureOptions } from 'ngx-configure';
import { AuthGuard } from './_guards/auth.guard';
import { RememberMeResolve } from './loginPopup/loginPopup.resolve';
import { AppOptions } from './app.options';
import { HttpClientModule, HttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { MultiTranslateHttpLoader } from "ngx-translate-multi-http-loader";
import { HttpConfigInterceptor } from './http.interceptor';
import { ErrorDialogModule } from './pages/error-dialog/error-dialog.module';

//export function createTranslateLoader(http: HttpClient) {
//  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
//}

export function HttpLoaderFactory(http: HttpClient) {
  return new MultiTranslateHttpLoader(http, [
    { prefix: "./assets/i18n/", suffix: ".json" },
    { prefix: "./app/cape-sdk-angular/assets/i18n/", suffix: ".json" },
  ]);
}

@NgModule({
  declarations: [
    AppComponent
  ],
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
    NbToastrModule.forRoot({ position: NbGlobalLogicalPosition.BOTTOM_END })
  ],
  providers: [{ provide: NgxConfigureOptions, useClass: AppOptions },
    AuthGuard,
    RememberMeResolve,
  {
    provide: HTTP_INTERCEPTORS,
    useClass: HttpConfigInterceptor,
    multi: true
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
