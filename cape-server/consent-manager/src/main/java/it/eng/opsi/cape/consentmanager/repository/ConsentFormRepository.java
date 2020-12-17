package it.eng.opsi.cape.consentmanager.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.eng.opsi.cape.consentmanager.model.ConsentForm;

public interface ConsentFormRepository extends MongoRepository<ConsentForm, String> {

	List<ConsentForm> findBySurrogateIdAndSinkIdAndUsageRules_purposeId(String surrogateId, String sinkId,
			String purposeId);

	List<ConsentForm> findBySurrogateIdAndSinkIdAndSourceIdAndUsageRules_purposeId(String surrogateId,
			String sinkId, String sourceId, String purposeId);

	Long deleteConsentFormBySurrogateId(String surrogateId);
}
