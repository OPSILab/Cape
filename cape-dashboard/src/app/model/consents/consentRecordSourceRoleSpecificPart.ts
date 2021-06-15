import { ConsentRecordRoleSpecificPart } from './consentRecordRoleSpecificPart';
import { ServicePopKey } from '../service-linking/servicePopKey';
import { RSAKey } from '../service-linking/rSAKey';

export interface ConsentRecordSourceRoleSpecificPart extends ConsentRecordRoleSpecificPart {
  pop_key: ServicePopKey;
  token_issuer_key: RSAKey;
}
