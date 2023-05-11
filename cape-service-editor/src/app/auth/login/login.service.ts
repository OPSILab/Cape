import { Injectable, TemplateRef } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';
import { NbDialogService } from '@nebular/theme';
import { AppConfig, System } from '../../model/appConfig';
import { TranslateService } from '@ngx-translate/core';
import { NbAuthService } from '@nebular/auth';

@Injectable({ providedIn: 'root' })
export class LoginService {
  private environment: System;
  private authProfile: string;
  private authRealm: string;
  private idmHost: string;
  private serviceEditorUrl: string;

  constructor(
    private configService: NgxConfigureService,
    private dialogService: NbDialogService,
    private authService: NbAuthService,
    private translateService: TranslateService
  ) {
    this.environment = (this.configService.config as AppConfig).system;
    this.idmHost = this.environment.auth.idmHost;
    this.serviceEditorUrl = this.environment.serviceEditorUrl;
    this.authProfile = this.environment.auth.authProfile;
    this.authRealm = this.environment.auth.authRealm;
  }

  logout = async (): Promise<void> => {
    localStorage.removeItem('accountId');
    localStorage.removeItem('accountEmail');

    const authResult = await this.authService.logout(this.authProfile).toPromise();
    if (authResult.isSuccess()) {
      window.location.href = `${this.idmHost}/realms/${this.authRealm}/protocol/openid-connect/logout?redirect_uri=${this.serviceEditorUrl}/login`;
    } else {
      window.alert(this.translateService.instant('login.logout_error'));
    }
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
