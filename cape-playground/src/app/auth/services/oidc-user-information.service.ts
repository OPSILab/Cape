import { Injectable } from '@angular/core';
import { of as observableOf } from 'rxjs';
import { NbAuthService } from '@nebular/auth';
import { OidcJWTToken, UserClaims } from '../model/oidc';
import { BehaviorSubject, Observable } from 'rxjs';
import { NgxConfigureService } from 'ngx-configure';

@Injectable({
  providedIn: 'root',
})
export class OidcUserInformationService {
  user: UserClaims;
  protected user$: BehaviorSubject<UserClaims> = new BehaviorSubject({});

  constructor(private authService: NbAuthService, private configService: NgxConfigureService) {
    this.authService.onTokenChange().subscribe((token: OidcJWTToken) => {
      if (token.isValid()) {
        this.user = token.getUserClaims();
        this.publishUser(this.user);
      }
    });
  }

  getRole(): Observable<string[]> {
    return this.user?.roles ? observableOf(this.user.roles.map((role) => role.toUpperCase())) : observableOf(['USER']);
  }

  getUser(): Observable<UserClaims> {
    return observableOf(this.user);
  }

  private publishUser(user: UserClaims) {
    this.user$.next(user);
  }

  onUserChange(): Observable<UserClaims> {
    return this.user$.asObservable();
  }
}
