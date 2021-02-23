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

export interface ShareWith {
  orgName?: string;
  orgUrl?: string;
  businessType?: ShareWithBusinessType;
  required?: boolean;
  initial?: boolean;
  current?: boolean;
}
export enum ShareWithBusinessType {
  Profit = 'Profit',
  NoProfit = 'No profit',
}
