import { ConsentStatusEnum } from '../consents/consentStatusRecordPayload';
import { SinkUsageRules } from '../consents/sinkUsageRules';
import { ProcessingBasisLegalBasis } from '../processingBasis';
import { DataMapping } from '../dataMapping';

export interface AuditLog {
  linkedServicesCount: number;
  givenConsentsCount: number;
  totalProcessedPersonalDataCount: number;
  processedPersonalDataCount: object;
  processingCategoryPersonalData: object;
  purposeCategoryCount: object;
  legalBasisCount: object;
  storageLocationPersonalData: object;
}

export interface EventLog {
  created: Date;
  accountId: string;
  legalBasis: ProcessingBasisLegalBasis;
  message: string;
  type: EventType;
}

export enum EventType {
  Consent = 'Consent',
  ServiceLink = 'ServiceLink',
  DataProcessing = 'DataProcessing'
}

export interface ServiceLinkEventLog extends EventLog {
  serviceId: string;
  serviceName: string;
  serviceUri: string;
  action: ServiceLinkActionType;
}

export interface ConsentEventLog extends EventLog {
  usageRules: SinkUsageRules;
  sinkId: string;
  sourceId: string;
  dataConcepts: DataMapping[];
  previousDataConcepts: DataMapping[];
  action: ConsentActionType;
  consentRecordId: string;
  previousStatus: ConsentStatusEnum;
}

export interface AuditDataMapping extends DataMapping {
  count: number;
}


export enum ConsentActionType {
  GIVE = 'Give',
  UPDATE = 'Update',
  DISABLE = 'Disable',
  ACTIVATE = 'Activate',
  WITHDRAW = 'Withdraw',
  SEND = 'Send'
}


export enum ServiceLinkActionType {
  CREATE = 'Create',
  DISABLE = 'Disable',
  DELETE = 'Delete'
}

export interface DateRange {
  start: Date;
  end: Date;
}
