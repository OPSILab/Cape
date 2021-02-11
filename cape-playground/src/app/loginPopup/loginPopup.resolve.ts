import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { LoginService } from '../login/login.service';

@Injectable()
export class RememberMeResolve implements Resolve<any> {
  constructor(private loginService: LoginService, private router: Router) { }
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {

    if (localStorage.getItem('rememberMe') === 'true') {
      return false;
    } else {
      return true;
    }


  }
}
