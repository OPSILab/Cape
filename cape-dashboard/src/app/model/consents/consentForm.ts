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
import { DataController } from '../dataController';
import { ResourceSet } from './resourceSet';
import { TextualDescription } from '../service-linking/textualDescription';
import { UsageRules } from './usageRules';

export interface ConsentForm {
  surrogate_id: string;
  source_surrogate_id?: string;
  source_service_id?: string;
  sink_service_id: string;
  source_name?: string;
  sink_name: string;
  source_library_domain_url?: string;
  sink_library_domain_url: string;
  source_humanreadable_descriptions?: Array<TextualDescription>;
  sink_humanreadable_descriptions?: Array<TextualDescription>;
  resource_set: ResourceSet;
  usage_rules: UsageRules;
  jurisdiction?: string;
  data_controller: DataController;
  service_description_version: string;
  service_description_signature: string;
  source_service_description_version?: string;
  source_service_description_signature?: string;
  service_provider_business_id: string;
  /**
   * Role (SINK, SOURCE) of requester Service initiating the Consenting request (is the Service related to the input Surrogate Id
   */
  requester_role: ConsentFormRequesterRoleEnum;
  /**
   * Identifier of the physical operator collecting the Consent on behalf of the Data Subject (e.g. after Data Subject gave previously the consent offline, for instance by paper). Identifier values can have any meaning, according to specific agreement made with the Service Provider
   */
  collection_operator_id?: string;
}
export enum ConsentFormRequesterRoleEnum {
  Sink = 'Sink',
  Source = 'Source',
}
