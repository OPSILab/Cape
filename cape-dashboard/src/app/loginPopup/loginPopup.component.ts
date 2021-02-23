import { Component, ViewChild, TemplateRef, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';

import { LoginService } from '../login/login.service';
import { ActivatedRoute } from '@angular/router';
import { AppConfig } from '../model/appConfig';

@Component({
  selector: 'login-popup',
  styleUrls: ['./loginPopup.component.scss'],
  templateUrl: './loginPopup.component.html',
})
export class LoginPopupComponent implements AfterViewInit {
  capeHost: string;
  rememberMe = true;

  @ViewChild('noAccountDialog', { static: false })
  private noAccountDialogTemplateRef: TemplateRef<unknown>;
  @ViewChild('errorDialog', { static: false })
  private errorDialogTemplateRef: TemplateRef<unknown>;

  constructor(
    private configService: NgxConfigureService,
    private loginService: LoginService,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute
  ) {
    this.capeHost = (this.configService.config as AppConfig).system.dashUrl;
  }

  async ngAfterViewInit(): Promise<void> {
    if (localStorage.getItem('rememberMe') === 'true') {
      this.rememberMe = true;
      await this.complete();
    } else {
      this.rememberMe = false;
    }

    this.cdr.detectChanges();
  }

  delay(ms: number): Promise<unknown> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }

  async complete(): Promise<void> {
    // Propagates (if any) queryParams, in order to be propagated also in the redirected URL after authentication
    // const queryParams: Params = this.route.snapshot.queryParams;

    await this.loginService.completeLogin(this.noAccountDialogTemplateRef, this.errorDialogTemplateRef);
  }

  cancel = (): void => {
    window.close();
  };

  oncheck = (checked: boolean): void => {
    localStorage.setItem('rememberMe', String(checked));
  };

  cancelCreateAccount(): void {
    void this.loginService.cancelCreateAccount(this.errorDialogTemplateRef);
  }

  submitCreateAccount(queryParams: unknown): void {
    this.loginService.submitCreateAccount(this.errorDialogTemplateRef, queryParams);
  }
}
