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
import { RSDescription } from './rSDescription';
import { HumanReadableDescription } from '../humanReadableDescription';
import { DataController } from '../dataController';

export interface CommonPart {
  iat: string;
  nbf?: string;
  exp?: string;
  operator: string;
  jurisdiction: string;
  role: CommonPartRole;
  version: string;
  cr_id: string;
  cr_pair_id?: string;
  surrogate_id: string;
  rs_description: RSDescription;
  slr_id: string;
  subject_id: string;
  subject_name: string;
  subject_humanreadable_descriptions: Array<HumanReadableDescription>;
  source_subject_id: string;
  source_subject_name: string;
  source_subject_humanreadable_descriptions: Array<HumanReadableDescription>;
  data_controller: DataController;
  service_description_version: string;
  service_description_signature: string;
  service_provider_business_id: string;
}

export enum CommonPartRole {
  Sink = 'Sink',
  Source = 'Source',
}
