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

    // Propagates (if any) queryParams, in order to be propagated also in the redirected URL after authentication
    const queryParams = this.route.snapshot.queryParams;
    let queryString = '';
    if (queryParams && Object.keys(queryParams).length > 0)
      queryString = Object.entries(queryParams).reduce((acc, entry) => { return `${acc}&${entry[0]}=${entry[1]}`; }, '');

    this.popupCenter({
      url: `${this.environment.idmHost}/oauth2/authorize?response_type=token${queryString}&client_id=${this.environment.clientId}&state=${state}&redirect_uri=${this.environment.dashUrl}${this.environment.loginPopupUrl}`,
      title: 'AuthPopup',
      w: 900,
      h: 500
    });
  }


  randomString = (length) => {
    return Math.round((Math.pow(36, length + 1) - Math.random() * Math.pow(36, length))).toString(36).slice(1);
  }


  popupCenter = ({ url, title, w, h }) => {
    // Fixes dual-screen position                             Most browsers      Firefox
    const dualScreenLeft = window.screenLeft !== undefined ? window.screenLeft : window.screenX;
    const dualScreenTop = window.screenTop !== undefined ? window.screenTop : window.screenY;

    const width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    const height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

    const systemZoom = width / window.screen.availWidth;
    const left = (width - w) / 2 / systemZoom + dualScreenLeft
    const top = (height - h) / 2 / systemZoom + dualScreenTop;
    const newWindow = window.open(url, title,
      `
      toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no,
      width=${w / systemZoom}, 
      height=${h / systemZoom}, 
      top=${top + 50}, 
      left=${left}
      `
    )

    if (window.focus) newWindow.focus();
  }

}

