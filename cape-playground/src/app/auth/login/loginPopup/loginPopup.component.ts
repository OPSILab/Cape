import { Component, ViewChild, TemplateRef, OnDestroy, AfterViewInit } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';

import { LoginService } from '../login.service';
import { ActivatedRoute, Params } from '@angular/router';
import { NbAuthOAuth2JWTToken, NbAuthService } from '@nebular/auth';
import { Subject } from 'rxjs';
import { NbDialogService } from '@nebular/theme';
import { OidcJWTToken } from '../../model/oidc';
import { AppConfig } from 'src/app/model/appConfig';
import { ErrorResponse } from 'src/app/model/errorResponse';
@Component({
  selector: 'login-popup',
  styleUrls: ['./loginPopup.component.scss'],
  templateUrl: './loginPopup.component.html',
})
export class LoginPopupComponent implements AfterViewInit, OnDestroy {
  playgroundUrl: string;
  locale: string;

  @ViewChild('errorDialog', { static: false })
  private errorDialogTemplateRef: TemplateRef<unknown>;

  private destroy$ = new Subject<void>();
  token: NbAuthOAuth2JWTToken;

  private queryParams: Params;

  constructor(
    private configService: NgxConfigureService,
    private loginService: LoginService,
    private authService: NbAuthService,
    private activatedRoute: ActivatedRoute,
    private dialogService: NbDialogService
  ) {
    this.playgroundUrl = (this.configService.config as AppConfig).system.playgroundUrl;
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
      } else if (authResult.getErrors().length > 0) this.openDialog(this.errorDialogTemplateRef, { error: { message: authResult.getErrors().toString() } });
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // Propagates (if any) queryParams, in order to be propagated also in the redirected URL after authentication
  completeLogin = (token: OidcJWTToken): void => {
    try {
      // Get Idm User Details to create the associated Cape Account
      const tokenPayload = token.getAccessTokenPayload();

      localStorage.setItem('accountId', tokenPayload.email);
      localStorage.setItem('accountEmail', tokenPayload.email);

      /*
       * Close Login Popup and propagates query Params saved before Login, and eventually append redirectAfterLogin to the Base path
       */
      this.closeLoginPopup();
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

  /*
   * Close Login Popup and propagates query Params saved before Login, and eventually append redirectAfterLogin to the Base path
   */
  closeLoginPopup = (): void => {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    const queryParamsBeforeLogin = JSON.parse(sessionStorage.getItem('queryParamsBeforeLogin')) as Record<string, string>;
    const redirectAfterLogin = queryParamsBeforeLogin?.redirectAfterLogin;
    sessionStorage.removeItem('queryParamsBeforeLogin');
    delete queryParamsBeforeLogin?.redirectAfterLogin;

    if (redirectAfterLogin) {
      const queryString = this.printQueryParamsString(queryParamsBeforeLogin);
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      window.opener.document.location.href = this.playgroundUrl + redirectAfterLogin + (queryString ? queryString : '');
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    } else window.opener.document.location.href = this.playgroundUrl;

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

  printQueryParamsString = (queryParams: Record<string, string>): string => {
    if (Object.keys(queryParams).length > 0)
      return Object.entries<string>(queryParams).reduce((acc, entry) => {
        return `${acc}&${entry[0]}=${entry[1]}`;
      }, '?');
    else return undefined;
  };
}
