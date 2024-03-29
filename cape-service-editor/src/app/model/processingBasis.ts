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
import { Storage } from './storage';
import { Obligation } from './obligation';
import { TextualDescription2 } from './service-linking/textualDescription2';
import { Organization } from './service-linking/organization';

export interface ProcessingBasis {
  purposeId: string;
  purposeName: string;
  legalBasis: ProcessingBasisLegalBasisEnum;
  purposeCategory: ProcessingBasisPurposeCategoryEnum;
  processingCategories: Set<ProcessingBasisProcessingCategoriesEnum>;
  description: Array<TextualDescription2>;
  requiredDatasets: Set<string>;
  storage: Storage;
  recipients: Set<ProcessingBasisRecipientsEnum>;
  shareWith: Array<Organization>;
  obligations: Array<Obligation>;
  policyRef: string;
  collectionMethod: string;
  collectionOperator?: string;
  termination: string;
}

export enum ProcessingBasisLegalBasisEnum {
  Consent = 'Consent',
  Contract = 'Contract',
  LegalObligation = 'Legal obligation',
  VitalInterest = 'Vital interest',
  PublicInterest = 'Public interest',
  LegitimateInterest = 'Legitimate interest',
}

export enum ProcessingBasisPurposeCategoryEnum {
  AcademicResearch = 'AcademicResearch',
  AccessControl = 'AccessControl',
  Advertising = 'Advertising ',
  CommercialInterest = 'CommercialInterest',
  CommercialResearch = 'CommercialResearch',
  CommunicationForCustomerCare = 'CommunicationForCustomerCare',
  CreateEventRecommendations = 'CreateEventRecommendations',
  CreatePersonalizedRecommendations = 'CreatePersonalizedRecommendations',
  CreateProductRecommendations = 'CreateProductRecommendations',
  CustomerCare = 'CustomerCare',
  DeliveryOfGoods = 'DeliveryOfGoods',
  DirectMarketing = 'DirectMarketing',
  FraudPreventionAndDetection = 'FraudPreventionAndDetection',
  IdentityVerification = 'IdentityVerification',
  ImproveExistingProductsAndServices = 'ImproveExistingProductsAndServices',
  ImproveInternalCRMProcesses = 'ImproveInternalCRMProcesses',
  IncreaseServiceRobustness = 'IncreaseServiceRobustness',
  InternalResourceOptimisation = 'InternalResourceOptimisation',
  LegalCompliance = 'LegalCompliance',
  Marketing = 'Marketing',
  NonCommercialResearch = 'NonCommercialResearch',
  OptimisationForConsumer = 'OptimisationForConsumer',
  OptimisationForController = 'OptimisationForController',
  OptimiseUserInterface = 'OptimiseUserInterface',
  Payment = 'Payment',
  PersonalisedAdvertising = 'PersonalisedAdvertising',
  PersonalisedBenefits = 'PersonalisedBenefits',
  RegistrationAuthentication = 'RegistrationAuthentication',
  ResearchAndDevelopment = 'ResearchAndDevelopment',
  Security = 'Security',
  SellDataToThirdParties = 'SellDataToThirdParties',
  SellInsightsFromData = 'SellInsightsFromData',
  SellProductsToDataSubject = 'SellProductsToDataSubject',
  SellTargettedAdvertisements = 'SellTargettedAdvertisements',
  ServiceOptimization = 'ServiceOptimization',
  ServicePersonalization = 'ServicePersonalization',
  ServiceProvision = 'ServiceProvision',
  SocialMediaMarketing = 'SocialMediaMarketing',
  UsageAnalytics = 'UsageAnalytics',
  UserInterfacePersonalisation = 'UserInterfacePersonalisation',
}
export enum ProcessingBasisProcessingCategoriesEnum {
  Acquire = 'Acquire',
  Adapt = 'Adapt',
  Align = 'Align',
  Alter = 'Alter',
  Analyse = 'Analyse',
  Anonymise = 'Anonymise',
  AutomatedDecisionMaking = 'AutomatedDecisionMaking',
  Collect = 'Collect',
  Combine = 'Combine',
  Consult = 'Consult',
  Copy = 'Copy',
  DataSource = 'DataSource',
  Derive = 'Derive',
  Destruct = 'Destruct',
  Disclose = 'Disclose',
  DiscloseByTransmission = 'DiscloseByTransmission',
  Disseminate = 'Disseminate',
  Erase = 'Erase',
  EvaluationScoring = 'EvaluationScoring',
  InnovativeUseOfNewTechnologies = 'InnovativeUseOfNewTechnologies',
  LargeScaleProcessing = 'LargeScaleProcessing',
  MakeAvailable = 'MakeAvailable',
  MatchingCombining = 'MatchingCombining',
  Move = 'Move',
  Obtain = 'Obtain',
  Organise = 'Organise',
  Profiling = 'Profiling',
  PseudoAnonymise = 'Pseudo-Anonymise',
  Record = 'Record',
  Remove = 'Remove',
  Restrict = 'Restrict',
  Retrieve = 'Retrieve',
  Share = 'Share',
  Store = 'Store',
  Structure = 'Structure',
  SystematicMonitoring = 'SystematicMonitoring',
  Transfer = 'Transfer',
  Transform = 'Transform',
  Transmit = 'Transmit',
}

export enum ProcessingBasisRecipientsEnum {
  Ours = 'Ours',
  Delivery = 'Delivery',
  Same = 'Same',
  OtherRecipient = 'Other recipient',
  Unrelated = 'Unrelated',
  Public = 'Public',
}
