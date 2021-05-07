import { HttpInterceptor, HttpRequest, HttpResponse, HttpHandler, HttpEvent, HttpErrorResponse, HttpHeaders } from '@angular/common/http';

import { Observable, throwError } from 'rxjs';
import { Injectable } from '@angular/core';
import { NbAuthService, NbAuthToken } from '@nebular/auth';
import { switchMap } from 'rxjs/operators';

Injectable();
@Injectable()
export class HttpConfigInterceptor implements HttpInterceptor {
  constructor(private authService: NbAuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return this.authService.getToken().pipe(
      switchMap((token: NbAuthToken) => {
        if (!request.url.includes('openid-connect/token') && token.getValue())
          request = request.clone({
            headers: new HttpHeaders({ Authorization: `Bearer ${token.getValue()}` }),
          });
        return next.handle(request);
      })
    );
  }
}
