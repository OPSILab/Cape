/**
 * SDK Service API
 * SDK Service API for integration with cape
 *
 * The version of the OpenAPI document: 2.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { RSAKey } from './rSAKey';
import { ServiceProvider } from './serviceProvider';
import { OperatorUrls } from './operatorUrls';
import { ServiceCertificate } from './serviceCertificate';

export interface DataOperatorDescription {
  operatorId: string;
  serviceProvider?: ServiceProvider;
  operatorServiceDescriptionVersion?: string;
  supportedProfiles?: Array<SupportedProfilesEnum>;
  cert?: ServiceCertificate;
  keyPair?: RSAKey;
  operatorUrls: OperatorUrls;
  createdOnDate?: string;
  createdByUserId?: string;
}
export enum SupportedProfilesEnum {
  Consenting = 'Consenting',
  _3rdPartyReUse = '3rd party re-use',
  Notification = 'Notification',
  Objection = 'Objection',
}
