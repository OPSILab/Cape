import { Injectable, TemplateRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { NgxConfigureService } from 'ngx-configure';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { NbDialogService, NbDialogRef } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { AccountService } from '../pages/account/account.service';
import { Account } from '../model/account/account.model';
import { AppConfig, I18n, System } from '../model/appConfig';
import { ErrorResponse } from '../model/errorResponse';

interface IdmUser {
  username: string;
  email: string;
}

interface IdmTokenResponse {
  access_token: string;
}

interface IdmLogoutResponse {
  redirect_sign_out_uri: string;
}

@Injectable({ providedIn: 'root' })
export class LoginService {
  private environment: System;
  private i18n: I18n;
  private accountUrl: string;
  private dashUrl: string;
  private idmHost: string;
  private username: string;
  private state: string;
  private loginPopupUrl: string;
  private clientId: string;
  private dialogRef: NbDialogRef<unknown>;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private configService: NgxConfigureService,
    private http: HttpClient,
    private dialogService: NbDialogService,
    private translateService: TranslateService,
    private accountService: AccountService
  ) {
    this.environment = (this.configService.config as AppConfig).system;
    this.i18n = (this.configService.config as AppConfig).i18n;
    this.accountUrl = this.environment.accountUrl;
    this.dashUrl = this.environment.dashUrl;
    this.idmHost = this.environment.idmHost;
    this.loginPopupUrl = this.environment.loginPopupUrl;
    this.clientId = this.environment.clientId;
  }

  cape_getAccount = async (
    createDialogTemplate: TemplateRef<unknown>,
    errorDialogTemplate: TemplateRef<unknown>,
    queryParams: Params
  ): Promise<void> => {
    try {
      const account: Account = await this.accountService.getAccount(localStorage.getItem('accountId'));
      localStorage.setItem('currentLocale', account.language);
      const redirectAfterLogin = queryParams.redirectAfterLogin as string;

      if (redirectAfterLogin) {
        const queryString = this.printQueryParamsString(queryParams);
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        window.opener.document.location.href = this.dashUrl + redirectAfterLogin + (queryString ? queryString : '');
        window.close();
        //   this.router.navigate([redirectAfterLogin], { relativeTo: this.activatedRoute, queryParams: queryParams });
      } else this.closeLoginPopup();
    } catch (err) {
      console.log(err);
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (err.error.statusCode === 500 && err.error.statusCode !== 401)
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
        this.openDialog(errorDialogTemplate, { error: err });
      else this.openDialog(createDialogTemplate, { queryParams: queryParams });
    }
  };

  printQueryParamsString = (queryParams: Params): string => {
    if (Object.keys(queryParams).length > 0)
      return Object.entries<string>(queryParams).reduce((acc, entry) => {
        return `${acc}&${entry[0]}=${entry[1]}`;
      }, '?');
    else return undefined;
  };

  cape_createAccount = (errorDialogTemplate: TemplateRef<unknown>, queryParams: Params): void => {
    const url = `${this.accountUrl}/accounts`;
    const account = {
      username: localStorage.accountId as string,
      account_info: {
        email: localStorage.accountEmail as string,
      },
      language: this.i18n.locale,
    } as Account;

    this.http.post(url, account).subscribe(
      (resp) => {
        console.log(`Account created: ${resp.toString()} `);

        const redirectAfterLogin = queryParams.redirectAfterLogin as string;
        if (redirectAfterLogin) {
          const queryString = this.printQueryParamsString(queryParams);
          // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
          window.opener.document.location.href = this.dashUrl + redirectAfterLogin + (queryString ? queryString : '');
          window.close();
        } else this.closeLoginPopup();
      },
      (err) => {
        console.log(err);
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
        this.openDialog(errorDialogTemplate, { error: err as Error });

        localStorage.clear();
      }
    );
  };

  closeLoginPopup = (): void => {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    window.opener.document.location.href = this.dashUrl;
    window.close();
  };

  cancelCreateAccount = async (errorDialogRef: TemplateRef<unknown>): Promise<void> => {
    try {
      this.dialogRef.close();
      await this.logout();
      this.closeLoginPopup();
    } catch (err) {
      console.log(err);
      this.openDialog(errorDialogRef, {
        error: err as ErrorResponse,
      });
    }
  };

  submitCreateAccount(errorDialogTemplate: TemplateRef<unknown>, queryParams: Params): void {
    this.cape_createAccount(errorDialogTemplate, queryParams);
    this.dialogRef.close();
  }

  completeLogin = async (noAccountDialogRef: TemplateRef<unknown>, errorDialogRef: TemplateRef<unknown>): Promise<void> => {
    const queryParams = this.activatedRoute.snapshot.queryParams;

    const token: string = queryParams.token as string;
    const code: string = queryParams.code as string;
    // const state: string = queryParams.state as string;

    try {
      // if (!state || state === '' || state !== sessionStorage.getItem('loginState') || ((!token || token === '') && (!code || code === '')))
      //   this.openDialog(errorDialogRef, { error: new Error('Token or login State are empty or invalid') });

      if (token && token !== '') {
        console.log(`token: ${token}`);
        localStorage.setItem('token', token);
        sessionStorage.removeItem('loginState');
      } else if (code && code !== '') {
        let params = new HttpParams();
        params = params
          .append('grant_type', 'authorization_code')
          .append('code', code)
          .append('redirect_uri', this.dashUrl + this.loginPopupUrl);

        const resp = await this.http
          .post<IdmTokenResponse>(`${this.accountUrl}/idm/oauth2/token`, params.toString(), {
            responseType: 'json',
            headers: new HttpHeaders({
              'Content-Type': 'application/x-www-form-urlencoded',
            }),
          })
          .toPromise();

        localStorage.setItem('token', resp.access_token);
        sessionStorage.removeItem('loginState');
      }

      // Get Idm User Details to create the associated Cape Account
      const resp = await this.http.get<IdmUser>(`${this.accountUrl}/idm/user?token=${token}`).toPromise();

      localStorage.setItem('accountId', resp.username);
      localStorage.setItem('accountEmail', resp.email);

      void this.cape_getAccount(noAccountDialogRef, errorDialogRef, queryParams);
    } catch (err) {
      console.log(err);
      this.openDialog(errorDialogRef, {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
        error: err,
      });
    }
  };

  logout = async (): Promise<void> => {
    localStorage.removeItem('accountId');
    localStorage.removeItem('accountEmail');
    localStorage.removeItem('token');

    const logout_redirect = await this.http
      .get<IdmLogoutResponse>(`${this.idmHost}/auth/external_logout?client_id=${this.clientId}&_method=DELETE`, { withCredentials: true })
      .toPromise();
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    location.href = logout_redirect.redirect_sign_out_uri;
  };

  openDialog = (dialogTemplate: TemplateRef<unknown>, ctx: unknown): void => {
    this.dialogRef = this.dialogService.open(dialogTemplate, {
      context: ctx,
      hasScroll: false,
      closeOnBackdropClick: false,
      closeOnEsc: false,
    });
  };
}
