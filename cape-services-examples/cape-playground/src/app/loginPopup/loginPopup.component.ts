import { Component, ViewChild, OnInit, TemplateRef, AfterContentInit, AfterViewInit, AfterViewChecked, OnChanges, ChangeDetectorRef } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { NbDialogService } from '@nebular/theme';

import { LoginService } from '../login/login.service';
import { ActivatedRoute, Params } from '@angular/router';

@Component({
  selector: 'login-popup',
  styleUrls: ['./loginPopup.component.scss'],
  templateUrl: './loginPopup.component.html',
})
export class LoginPopupComponent implements AfterViewInit {

  capeHost: string;
  rememberMe: boolean = true;

  @ViewChild('check', { static: false }) private checkInput;
  @ViewChild('noAccountDialog', { static: false }) private noAccountDialogTemplateRef: TemplateRef<any>;
  @ViewChild('errorDialog', { static: false }) private errorDialogTemplateRef: TemplateRef<any>;


  constructor(private configService: NgxConfigureService, private http: HttpClient,
    private dialogService: NbDialogService, private loginService: LoginService,
    private cdr: ChangeDetectorRef, private route: ActivatedRoute) {

    this.capeHost = configService.config.system.host;
  }

  async ngAfterViewInit() {
    if (localStorage.getItem('rememberMe') === 'true') {
      this.rememberMe = true;
      this.complete();
      this.cdr.detectChanges();
    } else {
      this.rememberMe = false;
      this.cdr.detectChanges();
    }
  }

  delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  complete() {

    // Propagates (if any) queryParams, in order to be propagated also in the redirected URL after authentication
    const queryParams: Params = this.route.snapshot.queryParams;

    this.loginService.completeLogin(this.noAccountDialogTemplateRef, this.errorDialogTemplateRef, queryParams);
  }


  cancel = () => {
    window.close();
  }

  oncheck = () => {
    localStorage.setItem('rememberMe', this.checkInput.checked ? 'true' : 'false');
  }

  //cancelCreateAccount() {
  //  this.loginService.cancelCreateAccount();
  //}


  //submitCreateAccount() {
  //  this.loginService.submitCreateAccount(this.errorDialogTemplateRef);
  //}



}

