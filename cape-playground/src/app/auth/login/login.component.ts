import { Component } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';
import { ActivatedRoute } from '@angular/router';
import { AppConfig, System } from '../../model/appConfig';
import { NbDialogService } from '@nebular/theme';

@Component({
  selector: 'login',
  styleUrls: ['./login.component.scss'],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private environment: System;

  constructor(private dialogService: NbDialogService, private configService: NgxConfigureService, private route: ActivatedRoute) {
    this.environment = (this.configService.config as AppConfig).system;
  }

  public login = (): void => {
    // Propagates (if any) queryParams, in order to be propagated also in the redirected URL after authentication
    const queryParams = this.route.snapshot.queryParams;
    let queryString = '';
    if (queryParams && Object.keys(queryParams).length > 0)
      queryString = Object.entries<string>(queryParams).reduce((acc, entry) => {
        return `${acc}&${entry[0]}=${entry[1]}`;
      }, '');

    this.popupCenter({
      url: `${this.environment.playgroundUrl}/login/loginPopup?${queryString}`,
      title: 'AuthPopup',
      w: 780,
      h: 650,
    });
  };

  // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
  popupCenter = ({ url, title, w, h }) => {
    // Fixes dual-screen position                             Most browsers      Firefox
    const dualScreenLeft = window.screenLeft !== undefined ? window.screenLeft : window.screenX;
    const dualScreenTop = window.screenTop !== undefined ? window.screenTop : window.screenY;

    const width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    const height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

    const systemZoom = width / window.screen.availWidth;
    const left = (width - w) / 2 / systemZoom + dualScreenLeft;
    const top = (height - h) / 2 / systemZoom + dualScreenTop;
    const newWindow = window.open(
      url,
      title,
      `
      toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no,
      width=${w / systemZoom},
      height=${h / systemZoom},
      top=${top + 50},
      left=${left}
      `
    );

    if (window.focus) newWindow.focus();
  };
}
