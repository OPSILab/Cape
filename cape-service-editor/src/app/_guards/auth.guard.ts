import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { NgxConfigureService } from 'ngx-configure';

@Injectable()
export class AuthGuard implements CanActivate {

  public config: any;

  public token: string = null;
  public profile: any = null;
  public accountId: any = null;

  constructor(private router: Router, private configService: NgxConfigureService) {
    this.config = configService.config;
  }


  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

    
    if (this.config.system.disable_auth === 'true') {
        return true;
    }
    this.token = localStorage.getItem('token');
    this.accountId = localStorage.getItem('accountId');



    if (this.token && this.accountId) {
      //console.log("in guard true");
      return true;
    } else {
       // not logged in so redirect to login page with the return url
       this.router.navigate(['/login'], { queryParams: { ...route.queryParams, redirectAfterLogin: state.url.split('?')[0] } });
       return false;
    }


  }



} 
