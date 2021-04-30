/* eslint-disable @typescript-eslint/no-unsafe-assignment */
import { Component, OnInit, OnDestroy } from '@angular/core';
import { NbMenuService } from '@nebular/theme';
import { LoginService } from './auth/login/login.service';
import { NbIconLibraries } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AppConfig } from './model/appConfig';
import { ErrorDialogService } from './error-dialog/error-dialog.service';
import { NbAuthService, NbOAuth2AuthStrategy, NbOAuth2ClientAuthMethod, NbOAuth2GrantType, NbOAuth2ResponseType } from '@nebular/auth';
import { OidcJWTToken } from './auth/model/oidc';
import { v4 as uuidv4 } from 'uuid';
@Component({
  selector: 'ngx-app',
  template: '<router-outlet></router-outlet>',
})
export class AppComponent implements OnInit, OnDestroy {
  private unsubscribe: Subject<void> = new Subject();
  private appConfig: AppConfig;

  constructor(
    private iconLibraries: NbIconLibraries,
    private menuService: NbMenuService,
    private loginService: LoginService,
    private translateService: TranslateService,
    private configService: NgxConfigureService,
    private errorDialogService: ErrorDialogService,
    authService: NbAuthService, // force construction of the auth service
    oauthStrategy: NbOAuth2AuthStrategy
  ) {
    this.appConfig = this.configService.config as AppConfig;
    /******
     * Register additional Icon Packs
     */
    this.iconLibraries.registerFontPack('md-icon', {
      packClass: 'material-icons',
    });
    this.iconLibraries.registerFontPack('fa', {
      packClass: 'fa',
      iconClassPrefix: 'fa',
    });
    // this.iconLibraries.setDefaultPack('md-icon');

    /******
     * Set either currentLocale on Local storage or locale in App config as current Translate language
     */
    this.translateService.use(localStorage.getItem('currentLocale') || this.appConfig.i18n.locale);

    /******
     * Configure Oidc Auth Strategy
     */
    oauthStrategy.setOptions({
      name: 'oidc',
      clientId: this.appConfig.system.clientId,
      clientSecret: '',
      baseEndpoint: this.appConfig.system.idmHost,
      clientAuthMethod: NbOAuth2ClientAuthMethod.NONE,
      token: {
        endpoint: `/auth/realms/${this.appConfig.system.authRealm}/protocol/openid-connect/token`,
        redirectUri: `${this.appConfig.system.dashUrl}/login/loginPopup`,
        class: OidcJWTToken,
        key: 'access_token',
      },
      authorize: {
        endpoint: `/auth/realms/${this.appConfig.system.authRealm}/protocol/openid-connect/auth`,
        scope: 'openid',
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        state: uuidv4(),
        redirectUri: `${this.appConfig.system.dashUrl}/login/loginPopup`,
        responseType: NbOAuth2ResponseType.CODE,
      },
      redirect: {
        success: '/pages', // welcome page path
        failure: null, // stay on the same page
      },
      refresh: {
        endpoint: `/auth/realms/${this.appConfig.system.authRealm}/protocol/openid-connect/token`,
        grantType: NbOAuth2GrantType.REFRESH_TOKEN,
      },
    });
  }

  onContecxtItemSelection(title: string): void {
    if (title === this.translateService.instant('general.login.logout_button')) {
      this.loginService.logout().catch((error) => this.errorDialogService.openErrorDialog(error));
    }
  }

  ngOnInit(): void {
    this.menuService
      .onItemClick()
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((event) => {
        this.onContecxtItemSelection(event.item.title);
      });
  }

  ngOnDestroy(): void {
    console.log('ngOnDestroy');
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}
