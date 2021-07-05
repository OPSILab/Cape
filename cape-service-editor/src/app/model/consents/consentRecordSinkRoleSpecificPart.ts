import { UsageRules } from './usageRules';
import { ConsentRecordRoleSpecificPart } from './consentRecordRoleSpecificPart';

export interface ConsentRecordSinkRoleSpecificPart extends ConsentRecordRoleSpecificPart {
  usage_rules: UsageRules;
  source_cr_id: string;
}
