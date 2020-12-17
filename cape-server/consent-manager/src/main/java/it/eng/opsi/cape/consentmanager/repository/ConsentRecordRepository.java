package it.eng.opsi.cape.consentmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import it.eng.opsi.cape.consentmanager.model.ConsentRecordSigned;
import it.eng.opsi.cape.consentmanager.model.ConsentStatusRecordSigned;

public interface ConsentRecordRepository
		extends MongoRepository<ConsentRecordSigned, String>, ConsentRecordCustomRepository {

	public List<ConsentRecordSigned> findByPayload_commonPart_surrogateId(String surrogateId);

	public ConsentRecordSigned[] findByPayload_commonPart_subjectId(String subjectId);

	public ConsentRecordSigned[] findByPayload_commonPart_serviceProviderBusinessId(String businessId);

	public Optional<List<ConsentRecordSigned>> findByPayload_commonPart_surrogateIdAndPayload_commonPart_rsDescription_resourceSet_datasets_purposeIdOrderByPayload_commonPart_iatDesc(
			String surrogateId, String purposeId);

	public Optional<ConsentRecordSigned> findByPayload_commonPart_surrogateIdAndPayload_commonPart_crId(
			String surrogateId, String crId);

	public List<ConsentRecordSigned> findByPayload_commonPart_surrogateIdAndPayload_commonPart_slrId(String surrogateId,
			String slrId);

	public List<ConsentRecordSigned> findByAccountId(String accountId);

	public List<ConsentRecordSigned> findByAccountIdAndPayload_commonPart_slrId(String accountId, String slrId);

	public Optional<ConsentRecordSigned> findByAccountIdAndPayload_commonPart_crId(String accountId, String crId);

	@Aggregation(pipeline = {
			"{ $match: { $and: [{'accountId': ?0},{'payload.commonPart.slrId': ?1}, {'payload.commonPart.crId': ?2}]}}",
			"{ $unwind : '$consentStatusList'}", "{ $replaceRoot: { newRoot : '$consentStatusList'}}" })
	public Optional<List<ConsentStatusRecordSigned>> getConsentStatusRecordsByAccountIdAndSlrIdAndCrId(String accountId,
			String slrId, String crId);

	@Aggregation(pipeline = {
			"{ $match: { $and: [{'accountId': ?0},{'payload.commonPart.slrId': ?1}, {'payload.commonPart.crId': ?2}]}}",
			"{ $unwind : '$consentStatusList'}", "{ $match: {'consentStatusList.payload.recordId': ?3 }}",
			"{ $replaceRoot: { newRoot : '$consentStatusList'}}" })
	public Optional<ConsentStatusRecordSigned> getConsentStatusRecordByAccountIdAndSlrIdAndCrIdAndRecordId(
			String accountId, String slrId, String crId, String recordId);

	@Aggregation(pipeline = {
			"{ $match: { $and: [{'accountId': ?0},{'payload.commonPart.slrId': ?1}, {'payload.commonPart.crId': ?2}]}}",
			"{ $unwind : '$consentStatusList'}", "{ $sort: { 'consentStatusList.payload.iat': -1}}", "{ $limit: 1 }",
			"{ $replaceRoot: { newRoot : '$consentStatusList'}}" })
	public Optional<ConsentStatusRecordSigned> getLastConsentStatusRecordByAccountIdAndSlrIdAndCrId(String accountId,
			String slrId, String crId);

	public Optional<ConsentRecordSigned[]> findByAccountIdAndPayload_commonPart_crPairId(String accountId,
			String crPairId);

	public Optional<ConsentRecordSigned[]> findByPayload_commonPart_surrogateIdAndPayload_commonPart_crPairId(
			String surrogateId, String crPairId);

	public Optional<ConsentRecordSigned[]> findByPayload_commonPart_crPairId(String crPairId);

	public Optional<ConsentRecordSigned[]> findByPayload_commonPart_rsDescription_resourceSet_rsId(String rsId);

	public Long deleteConsentRecordSignedByAccountId(String accountId);

}
