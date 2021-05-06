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
  redirectUrlAfterLogin: string;
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
    this.redirectUrlAfterLogin = (this.configService.config as AppConfig).system.playgroundUrl;
    this.locale = (this.configService.config as AppConfig).i18n.locale;
    this.queryParams = this.activatedRoute.snapshot.queryParams;
  }

  async ngAfterViewInit(): Promise<void> {
    if (!(await this.authService.isAuthenticatedOrRefresh().toPromise())) {
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
  // const queryParams: Params = this.route.snapshot.queryParams;
  completeLogin = (token: OidcJWTToken): void => {
    try {
      // Get Idm User Details to create the associated Cape Account
      const tokenPayload = token.getAccessTokenPayload();

      localStorage.setItem('accountId', tokenPayload.email);
      localStorage.setItem('accountEmail', tokenPayload.email);
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

  closeLoginPopup = (): void => {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    window.opener.document.location.href = this.redirectUrlAfterLogin;
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
