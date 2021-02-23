import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpHeaders } from '@angular/common/http';

import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';

Injectable();
@Injectable()
export class HttpConfigInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token: string = localStorage.getItem('token');

    if (token && !request.url.includes('/idm/')) {
      request = request.clone({
        headers: new HttpHeaders({ Authorization: 'Bearer ' + token }),
      });
    }

    //if (!request.headers.has('Content-Type')) {
    //  request = request.clone({ headers: request.headers.append('Content-Type', 'application/json') });
    //}

    //request = request.clone({ headers: request.headers.append('Accept', 'application/json') });

    return next.handle(
      request
    ); /*.pipe(
      map((event: HttpEvent<any>) => {
        if (event instanceof HttpResponse) {
          console.log('event--->>>', event);
        }
        return event;
      }));*/
  }
}
