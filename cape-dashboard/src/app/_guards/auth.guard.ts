import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { NgxConfigureService } from 'ngx-configure';
import { AppConfig } from '../model/appConfig';

@Injectable()
export class AuthGuard implements CanActivate {
  public config: AppConfig;

  public token: string = null;
  public accountId: string = null;

  constructor(private router: Router, private configService: NgxConfigureService) {
    this.config = configService.config as AppConfig;
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.config.system.disable_auth === 'true') {
      return true;
    }

    this.token = localStorage.getItem('token');
    this.accountId = localStorage.getItem('accountId');

    if (this.token && this.accountId) {
      return true;
    } else {
      if (state.url.startsWith('/serviceLinking'))
        void this.router.navigate(['/login'], {
          queryParams: {
            ...route.queryParams,
            redirectAfterLogin: state.url.split('?')[0],
          },
        });
      // not logged in so redirect to login page with the return url
      else void this.router.navigate(['/login']);

      return false;
    }
  }
}
