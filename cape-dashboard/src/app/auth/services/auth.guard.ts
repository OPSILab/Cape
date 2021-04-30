import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { NbAuthService } from '@nebular/auth';
import { NgxConfigureService } from 'ngx-configure';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';

import { AppConfig } from '../../model/appConfig';
import { tap } from 'rxjs/operators';

@Injectable()
export class AuthGuard implements CanActivate {
  public config: AppConfig;

  constructor(private authService: NbAuthService, private router: Router, configService: NgxConfigureService) {
    this.config = configService.config as AppConfig;
  }

  canActivate(route: ActivatedRouteSnapshot, routerState: RouterStateSnapshot): Observable<boolean> {
    if (this.config.system.disableAuth === 'true') {
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
          // not logged in so redirect to login page with the return url
          else void this.router.navigate(['/login']);
        }
      })
    );
  }
}
