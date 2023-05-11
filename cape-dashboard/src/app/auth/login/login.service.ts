import { Injectable, TemplateRef } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';
import { NbDialogService, NbWindowModule } from '@nebular/theme';
import { AppConfig, System } from '../../model/appConfig';
import { NbAuthService } from '@nebular/auth';
import { TranslateService } from '@ngx-translate/core';

@Injectable({ providedIn: 'root' })
export class LoginService {
  private environment: System;
  private authProfile: string;
  private authRealm: string;
  private idmHost: string;
  private dashboardUrl: string;

  constructor(
    private configService: NgxConfigureService,
    private dialogService: NbDialogService,
    private authService: NbAuthService,
    private translateService: TranslateService
  ) {
    this.environment = (this.configService.config as AppConfig).system;
    this.idmHost = this.environment.auth.idmHost;
    this.dashboardUrl = this.environment.dashboardUrl;
    this.authProfile = this.environment.auth.authProfile;
    this.authRealm = this.environment.auth.authRealm;
  }

  logout = async (redirectIntoOpener = false): Promise<void> => {
    localStorage.removeItem('accountId');
    localStorage.removeItem('accountEmail');

    const authResult = await this.authService.logout(this.authProfile).toPromise();
    if (authResult.isSuccess()) {
      const redirectedHref = `${this.idmHost}/realms/${this.authRealm}/protocol/openid-connect/logout?redirect_uri=${this.dashboardUrl}/login`;

      if (redirectIntoOpener) {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        window.opener.location.href = redirectedHref;
        window.close();
      } else window.location.href = redirectedHref;
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
