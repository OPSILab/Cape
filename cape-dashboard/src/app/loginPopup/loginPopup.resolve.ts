import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';

@Injectable()
export class RememberMeResolve implements Resolve<unknown> {
  resolve(): unknown {
    if (localStorage.getItem('rememberMe') === 'true') {
      return false;
    } else {
      return true;
    }
  }
}
