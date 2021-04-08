export interface AppConfig {
  serviceRegistry: {
    url: string;
  };
  system: System;
  i18n: I18n;
}

export interface System {
  sdkUrl: string;
  serviceEditorUrl: string;
  idmHost: string;
  clientId: string;
  loginPopupUrl: string;
  disable_auth: string;
  assetsDataDir: string;
  editorSchemaPath: string;
}

export interface I18n {
  locale: string;
}
