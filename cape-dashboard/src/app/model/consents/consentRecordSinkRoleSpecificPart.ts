import { UsageRules } from './usageRules';
import { ConsentRecordRoleSpecificPart } from './consentRecordRoleSpecificPart';

export interface ConsentRecordSinkRoleSpecificPart extends ConsentRecordRoleSpecificPart {
  source_cr_id: string;
}
