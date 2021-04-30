export interface AppConfig {
  serviceRegistry: {
    url: string;
  };
  system: System;
  i18n: I18n;
}

export interface System {
  operatorId: string;
  dashUrl: string;
  idmHost: string;
  clientId: string;
  accountUrl: string;
  serviceManagerUrl: string;
  auditLogUrl: string;
  consentManagerUrl: string;
  disableAuth: string;
  authProfile: string;
  authRealm: string;
}

export interface I18n {
  locale: string;
}
