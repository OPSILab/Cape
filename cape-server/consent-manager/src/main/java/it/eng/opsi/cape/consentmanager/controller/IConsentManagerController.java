/*******************************************************************************
 * CaPe - A Consent Based Personal Data Suite
 *  Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;
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
			ConsentForm consentForm)
			throws ResourceSetIdNotFoundException, ConsentManagerException, ServiceLinkRecordNotFoundException,
			AccountNotFoundException, ConsentRecordNotFoundException, ConsentStatusNotValidException,
			ServiceLinkStatusNotValidException, ServiceDescriptionNotFoundException, ChangeConsentStatusException;

	public abstract ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByAccountIdAndQuery(String accountId,
			String consentId, String serviceId, String sourceServiceId, String datasetId,
			ConsentRecordStatusEnum status, String purposeId, String purposeName, PurposeCategory purposeCategory,
			ProcessingCategory processingCategory) throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsByAccountIdAndQuery(
			String accountId, String consentId, String serviceId, String sourceServiceId, String datasetId,
			ConsentRecordStatusEnum status, String purposeId, String purposeName, PurposeCategory purposeCategory,
			ProcessingCategory processingCategory) throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByAccountIdAndSlrId(String accountId,
			String slrId);

	public abstract ResponseEntity<ConsentRecordSigned> getConsentRecordByAccountIdAndCrId(String accountId,
			String crId) throws ConsentRecordNotFoundException;

	public abstract ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsByAccountIdAndSlrId(
			String accountId, String slrId) throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsBySurrogateIdAndQuery(String surrogateId,
			String serviceId, String sourceServiceId, String datasetId, ConsentRecordStatusEnum status,
			String purposeId, String purposeName, PurposeCategory purposeCategory,
			ProcessingCategory processingCategory);

	public abstract ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordsPairsBySurrogateIdAndQuery(
			String surrogateId, String serviceId, String sourceServiceId, String datasetId,
			ConsentRecordStatusEnum status, String purposeId, String purposeName, PurposeCategory purposeCategory,
			ProcessingCategory processingCategory) throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsBySurrogateIdAndSlrId(
			String surrogateId, String slrId) throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<ConsentRecordSigned> getConsentRecordBySurrogateIdAndCrId(String surrogateId,
			String crId) throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<ConsentRecordSignedPair> getConsentRecordPairBySurrogateIdAndCrId(String surrogateId,
			String crId) throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByServiceIdAndQuery(String serviceId,
			String sourceServiceId, String datasetId, ConsentRecordStatusEnum status, String purposeId,
			String purposeName, PurposeCategory purposeCategory, ProcessingCategory processingCategory);

	public abstract ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsByServiceIdAndQuery(
			String serviceId, String sourceServiceId, String datasetId, ConsentRecordStatusEnum status,
			String purposeId, String purposeName, PurposeCategory purposeCategory,
			ProcessingCategory processingCategory) throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByServiceProviderBusinessIdAndQuery(
			String businessId, String surrogateId, String serviceId, String sourceServiceId, String datasetId,
			ConsentRecordStatusEnum status,String purposeId, String purposeName, PurposeCategory purposeCategory, ProcessingCategory processingCategory);

	public abstract ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsByServiceProviderBusinessIdAndQuery(
			String businessId, String surrogateId, String serviceId, String sourceServiceId, String datasetId,
			ConsentRecordStatusEnum status,String purposeId, String purposeName, PurposeCategory purposeCategory, ProcessingCategory processingCategory)
			throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<List<ConsentStatusRecordSigned>> getConsentStatusRecordsByAccountIdAndSlrIdAndCrId(
			String accountId, String slrId, String crId) throws ConsentRecordNotFoundException;

	public abstract ResponseEntity<ConsentStatusRecordSigned> getConsentStatusRecordByAccountIdAndSlrIdAndCrIdAndRecordId(
			String accountId, String slrId, String crId, String recordId) throws ConsentRecordNotFoundException;

	public abstract ResponseEntity<ConsentStatusRecordSigned> getLastConsentStatusRecordByAccountIdAndSlrIdAndCrId(
			String accountId, String slrId, String crId) throws ConsentRecordNotFoundException;

	public abstract ResponseEntity<ConsentRecordSigned> changeConsentStatus(String accountId, String slrId, String crId,
			ChangeConsentStatusRequest request) throws ConsentRecordNotFoundException, ConsentManagerException,
			ConsentStatusNotValidException, ResourceSetIdNotFoundException, ServiceLinkRecordNotFoundException,
			ServiceLinkStatusNotValidException, ServiceDescriptionNotFoundException, ChangeConsentStatusException;

	public abstract ResponseEntity<ConsentRecordSigned> deleteConsentRecordsByAccountId(String accountId,
			Boolean deleteConsentForm) throws ConsentRecordNotFoundException, ConsentManagerException;

	public abstract ResponseEntity<String> deleteConsentFormsBySurrogateId(String surrogateId);

	public abstract ResponseEntity<AuthorisationTokenResponse> getAuthorisationToken(String crId)
			throws ConsentRecordNotFoundException, ConsentManagerException, ServiceLinkRecordNotFoundException;

}
