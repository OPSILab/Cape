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

  closeLoginPopup = () => {
    window.opener.document.location.href = this.serviceUrl;
    window.close();
  }

  cancel = async (errorDialogRef: TemplateRef<unknown>) => {

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
          .append('redirect_uri', this.playgroundUrl + this.loginPopupUrl);

        const resp: any = await this.http.post(`${this.sdkUrl}/idm/oauth2/token`, params.toString(), {
          responseType: "json", headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' })
        }).toPromise();

        token = resp.body.access_token;
        localStorage.setItem('token', token);
        sessionStorage.removeItem('loginState');
      }


      // Get Idm User Details to create the associated Cape Account
      const resp: any = await this.http.get(`${this.sdkUrl}/idm/user?token=${token}`).toPromise();
      localStorage.setItem('accountId', resp.username);
      localStorage.setItem('accountEmail', resp.email);
      this.closeLoginPopup();

    } catch (err) {

      console.log(err);
      this.openDialog(errorDialogRef, {
        error: err
      });
    }

  }


  logout = async () => {
    localStorage.clear();
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
