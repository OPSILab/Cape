import { Component } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';
import { TranslateService } from '@ngx-translate/core';


@Component({
  selector: 'login',
  styleUrls: ['./login.component.scss'],
  templateUrl: './login.component.html',
})
export class LoginComponent {

  environment;

  constructor(private configService: NgxConfigureService) {
    this.environment = this.configService.config.system;
  }


  public login() {

    // Generate random state since we are using Implicit Oauth2 flow
    const state = this.randomString(24);

    sessionStorage.setItem('loginState', state);

    const basePath = window.location.href.split('/');

    window.open(`${this.environment.idmHost}/oauth2/authorize?response_type=token&client_id=${this.environment.clientId}&state=${state}&redirect_uri=${basePath[0]}//${basePath[2]}${this.environment.loginPopupUrl}`
      , 'AuthPopup', 'width=600,height=500,menubar=false,resizable=true,scrollbars=false,status=false');
  }


  randomString = (length) => {
    return Math.round((Math.pow(36, length + 1) - Math.random() * Math.pow(36, length))).toString(36).slice(1);
  }

}

