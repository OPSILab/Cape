import { Component, ViewChild, TemplateRef, OnDestroy, AfterViewInit } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';

import { LoginService } from '../login.service';
import { ActivatedRoute, Params } from '@angular/router';
import { AppConfig } from '../../../model/appConfig';
import { NbAuthOAuth2JWTToken, NbAuthService } from '@nebular/auth';
import { Subject } from 'rxjs';
import { NbDialogService } from '@nebular/theme';
import { OidcJWTToken } from '../../model/oidc';
import { AccountService } from '../../../pages/account/account.service';
import { Account } from '../../../model/account/account.model';
import { ErrorResponse } from '../../../model/errorResponse';
@Component({
  selector: 'login-popup',
  styleUrls: ['./loginPopup.component.scss'],
  templateUrl: './loginPopup.component.html',
})
export class LoginPopupComponent implements AfterViewInit, OnDestroy {
  dashboardUrl: string;
  locale: string;

  @ViewChild('noAccountDialog', { static: false })
  private noAccountDialogTemplateRef: TemplateRef<unknown>;
  @ViewChild('errorDialog', { static: false })
  private errorDialogTemplateRef: TemplateRef<unknown>;

  private destroy$ = new Subject<void>();
  token: NbAuthOAuth2JWTToken;

  private queryParams: Params;

  constructor(
    private configService: NgxConfigureService,
    private loginService: LoginService,
    private authService: NbAuthService,
    private accountService: AccountService,
    private activatedRoute: ActivatedRoute,
    private dialogService: NbDialogService
  ) {
    this.dashboardUrl = (this.configService.config as AppConfig).system.dashboardUrl;
    this.locale = (this.configService.config as AppConfig).i18n.locale;
    this.queryParams = this.activatedRoute.snapshot.queryParams;
  }

  async ngAfterViewInit(): Promise<void> {
    if (!(await this.authService.isAuthenticatedOrRefresh().toPromise())) {
      if (!(await this.authService.getToken().toPromise()).isValid() && !this.queryParams['code'])
        sessionStorage.setItem('queryParamsBeforeLogin', JSON.stringify(this.queryParams));
      const authResult = await this.authService.authenticate((this.configService.config as AppConfig).system.auth.authProfile).toPromise();

      if (authResult.isSuccess() && authResult.getToken()?.isValid()) {
        this.completeLogin(authResult.getToken() as OidcJWTToken);
      } else if (authResult.getErrors().length > 0)
        this.openDialog(this.errorDialogTemplateRef, { error: { message: authResult.getErrors().toString() } });
    }

    await this.getCapeAccount();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  delay(ms: number): Promise<unknown> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }

  // Propagates (if any) queryParams, in order to be propagated also in the redirected URL after authentication
  completeLogin = (token: OidcJWTToken): void => {
    try {
      // Get Idm User Details to create the associated Cape Account
      const tokenPayload = token.getAccessTokenPayload();

      localStorage.setItem('accountId', tokenPayload.email);
      localStorage.setItem('accountEmail', tokenPayload.email);
      sessionStorage.setItem('accountFirstName', tokenPayload.given_name);
      sessionStorage.setItem('accountFamilyName', tokenPayload.family_name);
      sessionStorage.setItem('isSpid', String(tokenPayload.isSpid));
    } catch (err) {
      console.log(err);
      this.openDialog(this.errorDialogTemplateRef, {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
        error: err as ErrorResponse,
      });
    }
  };

  cancel = (): void => {
    window.close();
  };

  getCapeAccount = async (): Promise<void> => {
    // const queryParams = this.activatedRoute.snapshot.queryParams;

    try {
      const account: Account = await this.accountService.getAccount(localStorage.getItem('accountId'));
      localStorage.setItem('currentLocale', account.language);

      /*
       * Close Login Popup and propagates query Params saved before Login, and eventually append redirectAfterLogin to the Base path
       */
      this.closeLoginPopup();
    } catch (err) {
      console.log(err);
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (err.status != 404 && err.error?.statusCode != 404 && err.error?.statusCode !== 401)
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
        this.openDialog(this.errorDialogTemplateRef, { error: err as ErrorResponse });
      // If error is 404 open Dialog to Create a new Cape Account
      else this.openDialog(this.noAccountDialogTemplateRef, {});
    }
  };

  createCapeAccount = async (): Promise<void> => {
    const account = {
      username: localStorage.accountId as string,
      account_info: {
        email: localStorage.accountEmail as string,
        firstname: sessionStorage.getItem('accountFirstName'),
        lastname: sessionStorage.getItem('accountFamilyName'),
      },
      language: this.locale,
    } as Account;

    try {
      await this.accountService.createAccount(account);

      this.closeLoginPopup();
    } catch (err) {
      console.log(err);
      // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
      this.openDialog(this.errorDialogTemplateRef, { error: err as ErrorResponse });
    }
  };

  cancelCreateAccount = async (): Promise<void> => {
    try {
      await this.loginService.logout(true);
    } catch (err) {
      console.log(err);
      this.openDialog(this.errorDialogTemplateRef, {
        error: err as ErrorResponse,
      });
    }
  };

  printQueryParamsString = (queryParams: Record<string, string>): string => {
    if (Object.keys(queryParams).length > 0)
      return Object.entries<string>(queryParams).reduce((acc, entry) => {
        return `${acc}&${entry[0]}=${entry[1]}`;
      }, '?');
    else return undefined;
  };

  /*
   * Close Login Popup and propagates query Params saved before Login, and eventually append redirectAfterLogin to the Base path
   */
  closeLoginPopup = (idmLogout?: boolean): void => {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    const queryParamsBeforeLogin = JSON.parse(sessionStorage.getItem('queryParamsBeforeLogin')) as Record<string, string>;
    const redirectAfterLogin = queryParamsBeforeLogin?.redirectAfterLogin;
    sessionStorage.removeItem('queryParamsBeforeLogin');
    delete queryParamsBeforeLogin?.redirectAfterLogin;

    if (redirectAfterLogin) {
      const queryString = this.printQueryParamsString(queryParamsBeforeLogin);

      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (idmLogout) window.opener.document.location.href = this.dashboardUrl + redirectAfterLogin + (queryString ? queryString : '');
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      else window.opener.document.location.href = this.dashboardUrl + redirectAfterLogin + (queryString ? queryString : '');

      // In Case of login with SPID, the opener is the SPID node under another domain, is not possible to set its href (Crosso-origin frame)

      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    } else if (sessionStorage.getItem('isSpid')?.toString() === 'true') {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      window.opener.opener.location.href = this.dashboardUrl;
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
      window.opener.close();
    } else {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      window.opener.document.location.href = this.dashboardUrl;
    }
    window.close();
  };

  openDialog = (dialogTemplate: TemplateRef<unknown>, ctx: unknown): void => {
    this.dialogService.open(dialogTemplate, {
      context: ctx,
      hasScroll: false,
      closeOnBackdropClick: false,
      closeOnEsc: false,
    });
  };
}
