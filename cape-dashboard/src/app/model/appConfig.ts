export interface AppConfig {
  serviceRegistry: {
    url: string;
  };
  system: System;
  i18n: I18n;
}

export interface System {
  operatorId: string;
  dashboardUrl: string;
  accountUrl: string;
  serviceManagerUrl: string;
  auditLogUrl: string;
  consentManagerUrl: string;
  auth: Auth;
}

export interface Auth {
  idmHost: string;
  clientId: string;
  disableAuth: string;
  authProfile: string;
  authRealm: string;
  defaultIdP: string;
  oidcField?: string;
}

export interface I18n {
  locale: string;
}
