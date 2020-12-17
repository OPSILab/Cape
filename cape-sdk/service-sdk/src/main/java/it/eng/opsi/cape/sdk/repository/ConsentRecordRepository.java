package it.eng.opsi.cape.sdk.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSigned;

public interface ConsentRecordRepository
		extends MongoRepository<ConsentRecordSigned, String>, ConsentRecordCustomRepository {

	public ConsentRecordSigned[] findByPayload_commonPart_surrogateId(String surrogateId);

	public ConsentRecordSigned[] findByPayload_commonPart_subjectId(String subjectId);

	public ConsentRecordSigned[] findByPayload_commonPart_serviceProviderBusinessId(String businessId);

	public ConsentRecordSigned[] findByPayload_commonPart_surrogateIdAndPayload_commonPart_rsDescription_resourceSet_datasets_purposeIdOrderByPayload_commonPart_iatDesc(
			String surrogateId, String purposeId);

	public Optional<ConsentRecordSigned> findByPayload_commonPart_crId(String crId);

	public Optional<ConsentRecordSigned> findByPayload_commonPart_surrogateIdAndPayload_commonPart_crId(
			String surrogateId, String crId);

}
