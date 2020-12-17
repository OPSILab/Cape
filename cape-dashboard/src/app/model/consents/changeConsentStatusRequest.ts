/**
 * CaPe API - Consent Manager
 * CaPe APIs used to manage CaPe Consent Form, Consent Records and consenting operations
 *
 * The version of the OpenAPI document: 2.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { SinkUsageRules } from './sinkUsageRules';
import { ResourceSet } from './resourceSet';
import { ConsentStatusEnum } from './consentStatusRecordPayload';
import { ChangeConsentStatusRequestFrom } from './changeSlrStatusRequestFrom';


export interface ChangeConsentStatusRequest {
  status: ConsentStatusEnum;
  resource_set: ResourceSet;
  usage_rules: SinkUsageRules;
  request_from: ChangeConsentStatusRequestFrom;
}



