import { Component, OnInit } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';
import { Router, ActivatedRoute } from '@angular/router';


@Component({
  selector: 'login',
  styleUrls: ['./login.component.scss'],
  templateUrl: './login.component.html',
})
export class LoginComponent {

  private environment;

  constructor(private configService: NgxConfigureService, private route: ActivatedRoute) {
    this.environment = this.configService.config.system;
  }


  public login() {

    // Generate random state since we are using Implicit Oauth2 flow
    const state = this.randomString(24);
    sessionStorage.setItem('loginState', state);

    const basePath = window.location.href.split('/');

    // Propagates (if any) queryParams, in order to be propagated also in the redirected URL after authentication
    const queryParams = this.route.snapshot.queryParams;
    let queryString = '';
    if (queryParams !== {})
      for (const [key, value] of Object.entries(queryParams)) queryString += `&${key}=${value}`;


    window.open(`${this.environment.idmHost}/oauth2/authorize?response_type=token${queryString}&client_id=${this.environment.clientId}&state=${state}&redirect_uri=${basePath[0]}//${basePath[2]}${this.environment.loginPopupUrl}`
      , 'AuthPopup', 'width=800,height=500,menubar=false,resizable=true,scrollbars=false,status=false');
  }


  randomString = (length) => {
    return Math.round((Math.pow(36, length + 1) - Math.random() * Math.pow(36, length))).toString(36).slice(1);
  }

}

