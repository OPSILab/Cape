import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpHeaders } from '@angular/common/http';

import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { NbAuthService, NbAuthToken } from '@nebular/auth';
import { switchMap } from 'rxjs/operators';
Injectable();
@Injectable()
export class HttpConfigInterceptor implements HttpInterceptor {
  constructor() {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (sessionStorage.getItem('authToken') != undefined)
      request = request.clone({
        headers: new HttpHeaders({ Authorization: `Bearer ${sessionStorage.getItem('authToken')}` }),
      });
    return next.handle(request);
  }
}
