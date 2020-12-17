package it.eng.opsi.cape.sdk.repository;

import java.util.List;

import it.eng.opsi.cape.exception.ConsentRecordNotFoundException;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.sdk.model.consenting.ConsentStatusRecordSigned;

public interface ConsentRecordCustomRepository {

	public ConsentRecordSigned addStatusToCr(String crId, ConsentStatusRecordSigned csr) throws ConsentRecordNotFoundException;
}
