package it.eng.opsi.cape.sdk.repository;

import java.util.Optional;

import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkStatusRecordSigned;

public interface ServiceLinkRecordDoubleSignedCustomRepository {

	public ServiceLinkRecordDoubleSigned addStatusToSlr(String slrId, ServiceLinkStatusRecordSigned ssr)
			throws ServiceLinkRecordNotFoundException;
}
