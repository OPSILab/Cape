export interface AppConfig {
  serviceRegistry: {
    url: string;
  };
  system: System;
  i18n: I18n;
}

export interface System {
  sdkUrl: string;
  checkConsentAtOperator: boolean;
  serviceEditorUrl: string;
  editorSchemaPath: string;
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
