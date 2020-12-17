package it.eng.opsi.cape.sdk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkStatusRecordSigned;

public interface ServiceLinkRecordDoubleSignedRepository
		extends MongoRepository<ServiceLinkRecordDoubleSigned, String>, ServiceLinkRecordDoubleSignedCustomRepository {

	public List<ServiceLinkRecordDoubleSigned> findByPayload_ServiceId(String serviceId);

	public Optional<ServiceLinkRecordDoubleSigned> findByPayload_SlrId(String slrId);

	public Optional<ServiceLinkRecordDoubleSigned> findByPayload_SurrogateId(String surrogateId);

	public Optional<ServiceLinkRecordDoubleSigned> findByPayload_SurrogateIdAndPayload_ServiceId(String surrogateId,
			String serviceId);

	@Aggregation(pipeline = { "{ $match: {'payload.slrID': ?0}}", "{ $unwind : '$payload.serviceLinkStatuses'}",
			"{ $sort: { 'payload.serviceLinkStatuses.payload.iat': -1}}", "{ $limit: 1 }",
			"{ $replaceRoot: { newRoot : '$payload.serviceLinkStatuses'}}" })
	public Optional<ServiceLinkStatusRecordSigned> getLastServiceLinkStatusRecordBySlrId(String slrId);

}
