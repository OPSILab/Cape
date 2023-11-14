export interface AppConfig {
  serviceRegistry: {
    url: string;
  };
  system: System;
  i18n: I18n;
}

export interface System {
  country: string;
  sdkUrl: string;
  dataMapEnumUrl: string;
  checkConsentAtOperator: boolean;
  serviceEditorUrl: string;
  editorSchemaPath: string;
  editorSchemaName: string;
  auth: Auth;
  serviceProviderBusinessId: string;
  mailTo:string;
  docsUrl:string;
  detailedErrors: boolean;
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
  languages: string[];
}
