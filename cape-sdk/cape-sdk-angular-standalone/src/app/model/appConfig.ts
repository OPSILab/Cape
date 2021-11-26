export interface AppConfig {
  system: System;
  services: Services;
  i18n: I18n;
}

export interface System {
  operatorId: string;
  onlineServicesUrl: string;
  dashboardUrl: string;
  cancelReturnUrl: string;
  auth: Auth;
}

export interface Auth {
  idmHost: string;
  clientId: string;
  disableAuth: string;
  authProfile: string;
  authRealm: string;
}

export interface Services {
  sdkUrl: string;
  checkConsentAtOperator: boolean;
}

export interface I18n {
  locale: string;
}
