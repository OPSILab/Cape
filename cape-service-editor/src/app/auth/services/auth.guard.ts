import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

import { NgxConfigureService } from 'ngx-configure';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';

import { AppConfig } from '../../model/appConfig';
import { map, tap } from 'rxjs/operators';
import { NbAuthService } from '@nebular/auth';
import { NbAccessChecker } from '@nebular/security';

@Injectable()
export class AuthGuard implements CanActivate {
  public config: AppConfig;

  constructor(
    private authService: NbAuthService,
    private router: Router,
    private configService: NgxConfigureService,
    private accessChecker: NbAccessChecker
  ) {
    this.config = this.configService.config as AppConfig;
  }

  canActivate(route: ActivatedRouteSnapshot, routerState: RouterStateSnapshot): Observable<boolean> {
    if (this.config.system.auth.disableAuth === 'true') {
      return Observable.of(true);
    }

    return this.authService.isAuthenticated().pipe(
      tap((authenticated) => {
        if (!authenticated) {
          if (routerState.url.startsWith('/serviceLinking'))
            void this.router.navigate(['/login'], {
              queryParams: {
                ...route.queryParams,
                redirectAfterLogin: routerState.url.split('?')[0],
              },
            });
        }
      })
    );
  }
}
