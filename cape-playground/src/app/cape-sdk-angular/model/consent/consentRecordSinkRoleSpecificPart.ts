import { SinkUsageRules } from './sinkUsageRules';
import { ConsentRecordRoleSpecificPart } from './consentRecordRoleSpecificPart';

export interface ConsentRecordSinkRoleSpecificPart extends ConsentRecordRoleSpecificPart {

  usage_rules: SinkUsageRules;
  source_cr_id: string;
}
