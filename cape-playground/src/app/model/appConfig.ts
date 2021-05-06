export interface AppConfig {
  serviceRegistry: {
    url: string;
  };
  system: System;
  i18n: I18n;
}

export interface System {
  operatorId: string;
  playgroundUrl: string;
  dashboardUrl: string;
  auth: Auth;
}

export interface Auth {
  idmHost: string;
  clientId: string;
  disableAuth: string;
  authProfile: string;
  authRealm: string;
}

export interface I18n {
  locale: string;
}
