import { NbAuthOAuth2JWTToken } from '@nebular/auth';

export class UserClaims {
  email?: string;
  email_verified?: boolean;
  family_name?: string;
  given_name?: string;
  locale?: string;
  name?: string;
  preferred_username?: string;
  picture?: string;
  sub?: string;
  updated_at?: string;
  roles?: string[];
}

export interface AccessTokenPayload {
  email: string;
  email_verified: boolean;
  family_name?: string;
  given_name?: string;
  name: string;
  preferred_username: string;
  sub: string;
}

export interface OidcToken {
  id_token: string;
  access_token: string;
}

export class OidcJWTToken extends NbAuthOAuth2JWTToken {
  // let's rename it to exclude name clashes
  static NAME = 'nb:auth:oidc:token';

  protected readonly token: OidcToken;

  getValue(): string {
    return this.token.access_token;
  }

  getAccessTokenPayload(): AccessTokenPayload {
    return super.getAccessTokenPayload() as AccessTokenPayload;
  }

  getUserClaims(): UserClaims {
    return super.getAccessTokenPayload() as UserClaims;
  }
}
