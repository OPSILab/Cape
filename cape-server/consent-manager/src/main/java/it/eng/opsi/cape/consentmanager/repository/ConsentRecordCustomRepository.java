package it.eng.opsi.cape.consentmanager.repository;

import java.util.List;

import it.eng.opsi.cape.consentmanager.model.ConsentRecordSigned;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordStatusEnum;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;

public interface ConsentRecordCustomRepository {

	public List<ConsentRecordSigned> findByAccountIdAndQuery(String accountId, String consentId, String serviceId,
			ConsentRecordStatusEnum status, String purposeCategory, ProcessingCategory processingCategory);
}
