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
import { Policy } from './policy';
import { ShareWith } from '../shareWith';
import { Storage } from '../storage';
import { Obligation } from '../obligation';
import { ProcessingBasisLegalBasis, ProcessingBasisProcessingCategories, ProcessingBasisRecipients } from '../processingBasis';


export interface SinkUsageRules {
  purposeId: string;
  purposeCategory: string;
  legalBasis: ProcessingBasisLegalBasis;
  purposeName: string;
  processingCategories: Array<ProcessingBasisProcessingCategories>;
  policy: Policy;
  storage?: Storage;
  recipients?: Array<ProcessingBasisRecipients>;
  shareWith?: Array<ShareWith>;
  obligations?: Array<Obligation>;
  collectionMethod?: string;
  termination?: string;
}



