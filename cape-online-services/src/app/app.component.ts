import { Component, OnDestroy, OnInit } from '@angular/core';
import { NbIconLibraries, NbMenuService } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';
import { AppConfig } from './model/appConfig';
import { NbAuthService, NbOAuth2AuthStrategy, NbOAuth2ClientAuthMethod, NbOAuth2GrantType, NbOAuth2ResponseType } from '@nebular/auth';
import { OidcJWTToken } from './auth/model/oidc';
import { v4 as uuidv4 } from 'uuid';
import { LoginService } from './auth/login/login.service';
import { Subject } from 'rxjs';
import { ErrorDialogService } from './pages/error-dialog/error-dialog.service';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  template: '<router-outlet></router-outlet>',
})
export class AppComponent implements OnInit, OnDestroy {
  private unsubscribe: Subject<void> = new Subject();
  private appConfig: AppConfig;

  constructor(
    private configService: NgxConfigureService,
    private iconLibraries: NbIconLibraries,
    private menuService: NbMenuService,
    private loginService: LoginService,
    private translate: TranslateService,
    private errorDialogService: ErrorDialogService,
    authService: NbAuthService, // force construction of the auth service
    oauthStrategy: NbOAuth2AuthStrategy
  ) {
    this.appConfig = this.configService.config as AppConfig;

    /******
     * Register additional Icon Packs
     */
    this.iconLibraries.registerFontPack('md-icon', { packClass: 'material-icons', iconClassPrefix: 'material-icons' });
    //this.iconLibraries.setDefaultPack('md-icon');

    /******
     * Set locale in App config as current Translate language
     */
    this.translate.setDefaultLang('en');
    this.translate.use(this.configService.config.i18n.locale);

    /******
     * Configure Oidc Auth Strategy
     */
    oauthStrategy.setOptions({
      name: 'oidc',
      clientId: this.appConfig.system.auth.clientId,
      clientSecret: '',
      baseEndpoint: this.appConfig.system.auth.idmHost,
      clientAuthMethod: NbOAuth2ClientAuthMethod.NONE,
      token: {
        endpoint: `/auth/realms/${this.appConfig.system.auth.authRealm}/protocol/openid-connect/token`,
        redirectUri: `${this.appConfig.system.onlineServicesUrl}/login/loginPopup`,
        class: OidcJWTToken,
        key: 'access_token',
      },
      authorize: {
        endpoint: `/auth/realms/${this.appConfig.system.auth.authRealm}/protocol/openid-connect/auth`,
        scope: 'openid',
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        state: uuidv4(),
        redirectUri: `${this.appConfig.system.onlineServicesUrl}/login/loginPopup`,
        responseType: NbOAuth2ResponseType.CODE,
        params: { kc_idp_hint: this.appConfig.system.auth.defaultIdP },
      },
      redirect: {
        success: '/pages', // welcome page path
        failure: null, // stay on the same page
      },
      refresh: {
        endpoint: `/auth/realms/${this.appConfig.system.auth.authRealm}/protocol/openid-connect/token`,
        grantType: NbOAuth2GrantType.REFRESH_TOKEN,
      },
    });
  }

  onContecxtItemSelection(title: string) {
    if (title === this.translate.instant('login.logout_button')) {
      this.loginService.logout().catch((error) => this.errorDialogService.openErrorDialog(error));
    }
  }

  ngOnInit() {
    this.menuService
      .onItemClick()
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((event) => {
        this.onContecxtItemSelection(event.item.title);
      });
  }

  ngOnDestroy() {
    console.log('ngOnDestroy');
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}
