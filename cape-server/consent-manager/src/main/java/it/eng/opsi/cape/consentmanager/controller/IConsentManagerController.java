package it.eng.opsi.cape.consentmanager.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;

import it.eng.opsi.cape.consentmanager.model.AuthorisationTokenResponse;
import it.eng.opsi.cape.consentmanager.model.ChangeConsentStatusRequest;
import it.eng.opsi.cape.consentmanager.model.ConsentForm;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordSigned;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordSignedPair;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordStatusEnum;
import it.eng.opsi.cape.consentmanager.model.ConsentStatusRecordSigned;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.ChangeConsentStatusException;
import it.eng.opsi.cape.exception.ConsentManagerException;
import it.eng.opsi.cape.exception.ConsentRecordNotFoundException;
import it.eng.opsi.cape.exception.ConsentStatusNotValidException;
import it.eng.opsi.cape.exception.DataMappingNotFoundException;
import it.eng.opsi.cape.exception.DatasetIdNotFoundException;
import it.eng.opsi.cape.exception.ResourceSetIdNotFoundException;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkStatusNotValidException;
import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;

public interface IConsentManagerController {

	public abstract ResponseEntity<ConsentForm> fetchConsentFormFromService(String surrogateId, String serviceId,
			String purposeId, String sourceDatasetId, String sourceServiceId) throws ConsentManagerException,
			ServiceLinkRecordNotFoundException, ServiceDescriptionNotFoundException, DataMappingNotFoundException,
			DatasetIdNotFoundException, AccountNotFoundException, ServiceLinkStatusNotValidException;

	public abstract ResponseEntity<List<DataMapping>> getMatchingDatasets(String serviceId, String purposeId,
			String sourceDatasetId, String sourceServiceId) throws DataMappingNotFoundException,
			DatasetIdNotFoundException, ConsentManagerException, ServiceDescriptionNotFoundException;

	public abstract ResponseEntity<ConsentRecordSigned> giveConsentFromService(String surrogateId,
			ConsentForm consentForm) throws ResourceSetIdNotFoundException, ConsentManagerException,
			ServiceLinkRecordNotFoundException, AccountNotFoundException;

	public abstract ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByAccountId(String accountId);

	public abstract ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsByAccountId(String accountId,
			String consentId, String serviceId, ConsentRecordStatusEnum status, String purposeCategory,
			ProcessingCategory processingCategory) throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsByAccountIdAndSlrId(
			String accountId, String slrId) throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsBySurrogateIdAndSlrId(
			String surrogateId, String slrId) throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsBySurrogateId(String surrogateId);

	public abstract ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByAccountIdAndSlrId(String accountId,
			String slrId);

	public abstract ResponseEntity<ConsentRecordSigned> getConsentRecordByAccountIdAndCrId(String accountId,
			String crId) throws ConsentRecordNotFoundException;

	public abstract ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsBySurrogateIdAndPurposeId(
			String surrogateId, String purposeId) throws ConsentRecordNotFoundException;

	public abstract ResponseEntity<ConsentRecordSigned[]> getConsentRecordsByServiceId(String serviceId);

	public abstract ResponseEntity<ConsentRecordSigned[]> getConsentRecordsByServiceProviderBusinessId(
			String businessId);

	public abstract ResponseEntity<List<ConsentStatusRecordSigned>> getConsentStatusRecordsByAccountIdAndSlrIdAndCrId(
			String accountId, String slrId, String crId) throws ConsentRecordNotFoundException;

	public abstract ResponseEntity<ConsentStatusRecordSigned> getConsentStatusRecordByAccountIdAndSlrIdAndCrIdAndRecordId(
			String accountId, String slrId, String crId, String recordId) throws ConsentRecordNotFoundException;

	public abstract ResponseEntity<ConsentStatusRecordSigned> getLastConsentStatusRecordByAccountIdAndSlrIdAndCrId(
			String accountId, String slrId, String crId) throws ConsentRecordNotFoundException;

	public abstract ResponseEntity<ConsentStatusRecordSigned> changeConsentStatus(String accountId, String slrId,
			String crId, ChangeConsentStatusRequest request)
			throws ConsentRecordNotFoundException, ConsentManagerException, ConsentStatusNotValidException,
			ResourceSetIdNotFoundException, ServiceLinkRecordNotFoundException, ServiceLinkStatusNotValidException,
			ServiceDescriptionNotFoundException, ChangeConsentStatusException;

	public abstract ResponseEntity<ConsentRecordSigned> deleteConsentRecordsByAccountId(String accountId,
			Boolean deleteConsentForm) throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<String> deleteConsentFormsBySurrogateId(String surrogateId);

	public abstract ResponseEntity<AuthorisationTokenResponse> getAuthorisationToken(String crId)
			throws ConsentRecordNotFoundException, ConsentManagerException, ServiceLinkRecordNotFoundException;

	public abstract ResponseEntity<ConsentRecordSigned> getConsentRecordBySurrogateIdAndConsentRecordId(
			String surrogateId, String crId) throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<ConsentRecordSignedPair> getConsentRecordPairBySurrogateIdAndConsentRecordId(
			String surrogateId, String crId) throws ConsentRecordNotFoundException, ConsentManagerException;

}
