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
import { ServiceLinkRecordAccountSigned } from './serviceLinkRecordAccountSigned';

export interface ServiceSignSlrRequest {
  account_signed_slr: ServiceLinkRecordAccountSigned;
  surrogate_id: string;
  operator_id: string;
  session_code: string;
}
