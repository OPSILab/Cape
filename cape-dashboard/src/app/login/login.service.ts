import { Injectable, TemplateRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { NgxConfigureService } from 'ngx-configure';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { NbDialogService, NbDialogRef } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { AccountService } from '../pages/account/account.service';
import { Account } from '../model/account/account.model';


@Injectable({ providedIn: 'root' })
export class LoginService {

  environment;
  accountUrl: string; capeHost: string; dashUrl: string; idmHost: string;
  username: string; state: string; loginPopupUrl: string; clientId: string;
  dialogRef: NbDialogRef<unknown>;


  constructor(private router: Router, private activatedRoute: ActivatedRoute, private configService: NgxConfigureService,
    private http: HttpClient, private dialogService: NbDialogService, private translateService: TranslateService, private accountService: AccountService) {

    this.environment = configService.config.system;
    this.capeHost = this.environment.host;
    this.accountUrl = this.environment.accountUrl;
    this.dashUrl = this.environment.dashUrl;
    this.idmHost = this.environment.idmHost;
    this.loginPopupUrl = this.environment.loginPopupUrl;
    this.clientId = this.environment.clientId;
  }


  cape_getAccount = async (createDialogTemplate: TemplateRef<unknown>, errorDialogTemplate: TemplateRef<unknown>, queryParams: Params) => {

    try {
      const account: Account = await this.accountService.getAccount(localStorage.getItem('accountId'));
      localStorage.setItem('currentLocale', account.language);
      const redirectAfterLogin = queryParams.redirectAfterLogin;

      if (redirectAfterLogin) {
        const queryString = this.printQueryParamsString(queryParams);
        window.opener.document.location.href = this.dashUrl + redirectAfterLogin + (queryString ? queryString : '');
        window.close();
        //   this.router.navigate([redirectAfterLogin], { relativeTo: this.activatedRoute, queryParams: queryParams });
      } else
        this.closeLoginPopup();
    } catch (err) {
      console.log(err);
      if (err.error.statusCode === 500 && err.error.statusCode !== 401)
        this.openDialog(errorDialogTemplate, { error: err });
      else
        this.openDialog(createDialogTemplate, { queryParams: queryParams });
    }
  }

  printQueryParamsString = (queryParams: Params) => {

    if (Object.keys(queryParams).length > 0)
      return Object.entries(queryParams).reduce((acc, entry) => { return `${acc}&${entry[0]}=${entry[1]}`; }, '?');
    else return undefined;
  }

  cape_createAccount = (errorDialogTemplate: TemplateRef<unknown>, queryParams: Params) => {

    const url = `${this.accountUrl}/accounts`;
    const account = {
      username: localStorage.accountId,
      account_info: {
        email: localStorage.accountEmail
      },
      language: this.configService.config.i18n.locale
    } as Account;

    this.http.post(url, account)
      .subscribe(
        resp => {
          console.log(`Account created: ${resp} `);

          const redirectAfterLogin = queryParams.redirectAfterLogin;
          if (redirectAfterLogin) {
            const queryString = this.printQueryParamsString(queryParams);
            window.opener.document.location.href = this.dashUrl + redirectAfterLogin + (queryString ? queryString : '');
            window.close();
          } else
            this.closeLoginPopup();
        },
        err => {
          console.log(err);
          this.openDialog(errorDialogTemplate, { error: err });

          localStorage.clear();
        }
      );
  }

  closeLoginPopup = () => {
    window.opener.document.location.href = this.dashUrl;
    window.close();
  }

  cancelCreateAccount = async (errorDialogRef: TemplateRef<unknown>) => {

    try {
      this.dialogRef.close();
      await this.logout();
      this.closeLoginPopup();

    } catch (err) {
      console.log(err);
      this.openDialog(errorDialogRef, {
        error: err
      });
    }
  }

  submitCreateAccount(errorDialogTemplate: TemplateRef<unknown>, queryParams: Params) {
    this.cape_createAccount(errorDialogTemplate, queryParams);
    this.dialogRef.close();
  }


  completeLogin = async (noAccountDialogRef: TemplateRef<unknown>, errorDialogRef: TemplateRef<unknown>, prevQueryParams: Params) => {

    const queryParams = this.activatedRoute.snapshot.queryParams;

    let token: string = queryParams.token;
    const code: string = queryParams.code;
    const state: string = queryParams.state;

    try {
      // if (!state || state === '' || state !== sessionStorage.getItem('loginState') || ((!token || token === '') && (!code || code === '')))
      //   this.openDialog(errorDialogRef, { error: new Error('Token or login State are empty or invalid') });

      if (token && token !== '') {

        console.log(`token: ${token}`);
        localStorage.setItem('token', token);
        sessionStorage.removeItem('loginState');

      } else if (code && code !== '') {

        let params = new HttpParams();
        params = params.append('grant_type', 'authorization_code')
          .append('code', code)
          .append('redirect_uri', this.dashUrl + this.loginPopupUrl);

        const resp: any = await this.http.post(`${this.accountUrl}/idm/oauth2/token`, params.toString(), {
          responseType: "json", headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' })
        }).toPromise();

        token = resp.body.access_token;
        localStorage.setItem('token', token);
        sessionStorage.removeItem('loginState');

      }


      // Get Idm User Details to create the associated Cape Account
      const resp: any = await this.http.get(`${this.accountUrl}/idm/user?token=${token}`).toPromise();

      localStorage.setItem('accountId', resp.username);
      localStorage.setItem('accountEmail', resp.email);

      this.cape_getAccount(noAccountDialogRef, errorDialogRef, queryParams);


    } catch (err) {

      console.log(err);
      this.openDialog(errorDialogRef, {
        error: err
      });
    }

  }


  logout = async () => {

    localStorage.removeItem('accountId');
    localStorage.removeItem('accountEmail');
    localStorage.removeItem('token');
    const logout_redirect = await this.http.get<any>(`${this.idmHost}/auth/external_logout?client_id=${this.clientId}&_method=DELETE`, { withCredentials: true }).toPromise();
    location.href = logout_redirect.redirect_sign_out_uri;
  }


  openDialog = (dialogTemplate: TemplateRef<unknown>, ctx: unknown) => {

    this.dialogRef = this.dialogService.open(dialogTemplate,
      {
        context: ctx,
        hasScroll: false,
        closeOnBackdropClick: false,
        closeOnEsc: false
      });
  }

}
