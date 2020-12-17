package it.eng.opsi.cape.accountmanager.model;

import it.eng.opsi.cape.accountmanager.model.audit.AuditLog;
import it.eng.opsi.cape.accountmanager.model.audit.EventLog;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordDoubleSigned;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountExport {

	AccountInfo accountInfo;

	ServiceLinkRecordDoubleSigned[] serviceLinks;

	ConsentRecordSigned[] consentRecords;

	EventLog[] eventLogs;

	AuditLog auditLog;

}
