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
import { Description2 } from './description2';
import { ShareWith } from './shareWith';
import { Storage } from './storage';
import { Obligation } from './obligation';


export interface ProcessingBasis {
  purposeId?: string;
  purposeName?: string;
  legalBasis?: ProcessingBasisLegalBasis;
  purposeCategory?: string;
  processingCategories?: Array<ProcessingBasisProcessingCategories>;
  description?: Array<Description2>;
  requiredDatasets?: Array<string>;
  storage?: Storage;
  recipients?: Array<ProcessingBasisRecipients>;
  shareWith?: Array<ShareWith>;
  obligations?: Array<Obligation>;
  policyRef?: string;
  collectionMethod?: string;
  termination?: string;
}
export enum ProcessingBasisLegalBasis {
  Consent = 'CONSENT',
  Contract = 'CONTRACT',
  LegalObligation = 'LEGAL_OBLIGATION',
  VitalInterest = 'VITAL_INTEREST',
  PublicInterest = 'PUBLIC_INTEREST',
  LegitimateInterest = 'LEGITIMATE_INTEREST'
}

export enum ProcessingBasisProcessingCategories {
  Aggregate = 'AGGREGATE',
  Analyse = 'ANALYSE',
  Anonymize = 'ANONYMIZE',
  Collect = 'COLLECT',
  Copy = 'COPY',
  Derive = 'DERIVE',
  Move = 'MOVE',
  Query = 'QUERY',
  Transfer = 'TRANSFER'
}

export enum ProcessingBasisRecipients {
  Ours = 'Ours',
  Delivery = 'Delivery',
  Same = 'Same',
  OtherRecipient = 'Other recipient',
  Unrelated = 'Unrelated',
  Public = 'Public'
}


