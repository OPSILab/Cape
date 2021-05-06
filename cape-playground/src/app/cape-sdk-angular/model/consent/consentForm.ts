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
import { SinkUsageRules } from './sinkUsageRules';
import { DataController } from '../dataController';
import { HumanReadableDescription } from '../humanReadableDescription';
import { ResourceSet } from './resourceSet';

export interface ConsentForm {
  surrogate_id: string;
  source_surrogate_id?: string;
  source_service_id?: string;
  sink_service_id: string;
  source_name?: string;
  sink_name: string;
  source_library_domain_url?: string;
  sink_library_domain_url?: string;
  source_humanreadable_descriptions?: Array<HumanReadableDescription>;
  sink_humanreadable_descriptions?: Array<HumanReadableDescription>;
  resource_set: ResourceSet;
  usage_rules: SinkUsageRules;
  jurisdiction?: string;
  data_controller: DataController;
  service_description_version: string;
  service_description_signature: string;
}
