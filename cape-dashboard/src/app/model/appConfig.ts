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
  loginPopupUrl: string;
  accountUrl: string;
  serviceManagerUrl: string;
  auditLogUrl: string;
  consentManagerUrl: string;
  disable_auth: string;
}

export interface I18n {
  locale: string;
}
