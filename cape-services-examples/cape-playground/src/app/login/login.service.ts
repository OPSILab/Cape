import { Injectable, TemplateRef } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { NgxConfigureService } from 'ngx-configure';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { NbDialogService, NbDialogRef } from '@nebular/theme';



@Injectable({ providedIn: 'root' })
export class LoginService {

  environment;
  playgroundUrl: string;
  capeHost: string;
  idmHost: string;
  username: string;
  state: string;
  loginPopupUrl: string;
  sdkUrl: string;
  serviceUrl: string;
  clientId: string;

  dialogRef: NbDialogRef<unknown>;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private configService: NgxConfigureService,
    private http: HttpClient, private dialogService: NbDialogService) {
    this.environment = configService.config.system;
    this.playgroundUrl = this.environment.playgroundUrl;
    this.capeHost = this.environment.capeHost;
    this.idmHost = this.environment.idmHost;
    this.loginPopupUrl = this.environment.loginPopupUrl;
    this.sdkUrl = configService.config.services.sdkUrl;
    this.serviceUrl = configService.config.services.serviceUrl;
    this.clientId = this.environment.clientId;
  }

  /*
   * The Service User/Account is the one retrieved directly from the Keyrock Idm,
   * which is different from the Cape Account (although it uses also Keyrock account data to create a new Cape Account)
   *
   * */

  //cape_getAccount = (createDialogTemplate: TemplateRef<any>, errorDialogTemplate: TemplateRef<any>) => {

  //  const url = `${this.accountUrl}/accounts/${localStorage.getItem('accountId')}`;

  //  this.http.get(url).subscribe((res: any) => {

  //    console.log(res.username);
  //    this.closeLoginPopup();
  //  }, err => {
  //    console.log(err);
  //    if (err.error.statusCode === 500 && err.error.statusCode !== 401)
  //      this.dialogService.open(errorDialogTemplate, { closeOnBackdropClick: false });
  //    else
  //      this.dialogRef = this.dialogService.open(createDialogTemplate, { closeOnBackdropClick: false });
  //  });
  //};

  //cape_createAccount = (errorDialogTemplate: TemplateRef<any>) => {

  //  const url = `${this.accountUrl}/accounts`;
  //  const account = {
  //    username: localStorage.getItem('accountId'),
  //    account_info: {
  //      email: localStorage.accountEmail
  //    }
  //  };

  //  this.http.post(url, account)
  //    .subscribe(
  //      resp => {
  //        console.log(`Account created: ${resp} `);
  //        this.closeLoginPopup();
  //      },
  //      err => {
  //        console.log(err);
  //        this.dialogService.open(errorDialogTemplate, { context: { error: err.error }, closeOnBackdropClick: false });
  //        localStorage.clear();
  //      }
  //    );
  //};

  closeLoginPopup = () => {
    window.opener.document.location.href = this.serviceUrl;
    window.close();
  }

  //cancelCreateAccount() {
  //  this.dialogRef.close();
  //  this.closeLoginPopup();
  //  this.logout();
  //}

  //submitCreateAccount(errorDialogTemplate: TemplateRef<any>) {
  //  this.cape_createAccount(errorDialogTemplate);
  //  this.dialogRef.close();
  //}

  completeLogin = async (noAccountDialogRef: TemplateRef<unknown>, errorDialogRef: TemplateRef<unknown>, prevQueryParams: Params) => {


    const queryParams = this.activatedRoute.snapshot.queryParams;

    let token: string = queryParams.token;
    const code: string = queryParams.code;
    const state: string = queryParams.state;

    if (!state || state === '' || state !== sessionStorage.getItem('loginState') || ((!token || token === '') && (!code || code === '')))
      this.dialogService.open(errorDialogRef, { context: { error: new Error('Token or login State are empty or invalid') }, closeOnBackdropClick: false });

    if (token && token !== '') {

      console.log(`token: ${token}`);
      localStorage.setItem('token', token);
      sessionStorage.removeItem('loginState');

    } else if (code && code !== '') {

      let params = new HttpParams();
      params = params.append('grant_type', 'authorization_code');
      params = params.append('code', code);
      params = params.append('redirect_uri', this.playgroundUrl + this.loginPopupUrl);

      const resp: any = await this.http.post(`${this.sdkUrl}/idm/oauth2/token`, params.toString(), {
        responseType: "json", headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' })
      }).toPromise();

      token = resp.body.access_token;
      localStorage.setItem('token', token);
      sessionStorage.removeItem('loginState');
    }


    // Get Idm User Details to create the associated Cape Account
    this.http.get(`${this.sdkUrl}/idm/user?access_token=${token}`).subscribe((data: any) => {

      localStorage.setItem('accountId', data.username);
      localStorage.setItem('accountEmail', data.email);
      this.closeLoginPopup();
      // this.cape_getAccount(noAccountDialogRef, errorDialogRef);
    }, err => {
      console.log(err);
      this.dialogService.open(errorDialogRef, { context: { error: err }, closeOnBackdropClick: false });
    });
  }


  logout = () => {

    localStorage.clear();
    location.href = `${this.idmHost}/auth/external_logout?_method=DELETE&client_id=${this.clientId}`;
  }

}
