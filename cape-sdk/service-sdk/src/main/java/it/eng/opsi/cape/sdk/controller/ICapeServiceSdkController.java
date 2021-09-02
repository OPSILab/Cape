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
package it.eng.opsi.cape.sdk.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.nimbusds.jose.JOSEException;

import it.eng.opsi.cape.exception.CapeSdkManagerException;
import it.eng.opsi.cape.exception.ConsentRecordNotFoundException;
import it.eng.opsi.cape.exception.ConsentRecordNotValid;
import it.eng.opsi.cape.exception.ConsentStatusNotValidException;
import it.eng.opsi.cape.exception.ConsentStatusRecordNotValid;
import it.eng.opsi.cape.exception.DataRequestNotValid;
import it.eng.opsi.cape.exception.DatasetIdNotFoundException;
import it.eng.opsi.cape.exception.DataOperatorDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkStatusNotValidException;
import it.eng.opsi.cape.exception.ServiceLinkStatusRecordNotValid;
import it.eng.opsi.cape.exception.ServiceManagerException;
import it.eng.opsi.cape.exception.ServiceSignKeyNotFoundException;
import it.eng.opsi.cape.exception.SessionNotFoundException;
import it.eng.opsi.cape.exception.SessionStateNotAllowedException;
import it.eng.opsi.cape.exception.UserSurrogateIdLinkNotFoundException;
import it.eng.opsi.cape.sdk.model.DataOperatorDescription;
import it.eng.opsi.cape.sdk.model.ServiceSignKey;
import it.eng.opsi.cape.sdk.model.SurrogateIdResponse;
import it.eng.opsi.cape.sdk.model.account.Account;
import it.eng.opsi.cape.sdk.model.consenting.ChangeConsentStatusRequest;
import it.eng.opsi.cape.sdk.model.consenting.ConsentForm;
import it.eng.opsi.cape.sdk.model.consenting.ConsentFormRequest;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSignedPair;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordStatusEnum;
import it.eng.opsi.cape.sdk.model.datatransfer.DataTransferRequest;
import it.eng.opsi.cape.sdk.model.datatransfer.DataTransferResponse;
import it.eng.opsi.cape.sdk.model.linking.FinalLinkingResponse;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceSignSlrRequest;
import it.eng.opsi.cape.sdk.model.linking.ServiceSignSlrResponse;
import it.eng.opsi.cape.sdk.model.linking.StartLinkingRequest;
import it.eng.opsi.cape.sdk.model.linking.UserSurrogateIdLink;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;

public interface ICapeServiceSdkController {

	public abstract ResponseEntity<DataOperatorDescription> getDataOperatorDescription(String operatorId)
			throws DataOperatorDescriptionNotFoundException, ServiceManagerException;

	public abstract ResponseEntity<FinalLinkingResponse> startServiceLinking(StartLinkingRequest request)
			throws ServiceManagerException, DataOperatorDescriptionNotFoundException, JOSEException,
			ServiceDescriptionNotFoundException, SessionNotFoundException;

	public abstract ResponseEntity<SurrogateIdResponse> generateSurrogateId(String operatorId, String userId);

	public abstract ResponseEntity<ServiceEntry> registerServiceToCape(String serviceId)
			throws JOSEException, ServiceManagerException, JsonProcessingException, ServiceDescriptionNotFoundException;

	public abstract ResponseEntity<ServiceSignSlrResponse> signServiceLinkRecordPayload(ServiceSignSlrRequest request)
			throws JsonProcessingException, JOSEException, ParseException, ServiceSignKeyNotFoundException,
			SessionNotFoundException, ServiceManagerException, SessionStateNotAllowedException;

	public abstract ResponseEntity<Object> unregisterOrDeleteService(String serviceId, Boolean deleteServiceDescription)
			throws JOSEException, ServiceManagerException, ServiceSignKeyNotFoundException,
			ServiceDescriptionNotFoundException;

//	public abstract ResponseEntity<ServiceSignKey> replaceAndStoreServiceSignKey(String serviceId, String oldKid);

	public abstract ResponseEntity<List<ServiceLinkRecordDoubleSigned>> getAllServiceLinkRecords();

	public abstract ResponseEntity<List<ServiceLinkRecordDoubleSigned>> getServiceLinkRecordsByServiceId(
			String serviceId);

	public abstract ResponseEntity<ServiceLinkRecordDoubleSigned> getServiceLinkRecordBySlrId(String slrId)
			throws ServiceLinkRecordNotFoundException;

	public abstract ResponseEntity<ServiceLinkRecordDoubleSigned> getServiceLinkRecordBySurrogateId(String surrogateId)
			throws ServiceLinkRecordNotFoundException;

	public abstract ResponseEntity<ServiceLinkRecordDoubleSigned> getServiceLinkRecordBySurrogateIdAndServiceId(
			String surrogateId, String serviceId) throws ServiceLinkRecordNotFoundException;

	public abstract ResponseEntity<ServiceLinkStatusRecordSigned> getLastServiceLinkStatusRecord(String slrId)
			throws ServiceLinkRecordNotFoundException;

	public abstract ResponseEntity<UserSurrogateIdLink> linkUserToSurrogateId(UserSurrogateIdLink entity);

	public abstract ResponseEntity<UserSurrogateIdLink> getUserSurrogateLinkByUserIdAndServiceIdAndOperatorId(
			String userId, String serviceId, String operatorId) throws UserSurrogateIdLinkNotFoundException;

	public abstract ResponseEntity<ServiceLinkStatusRecordSigned> enableServiceLink(String slrId, String surrogateId,
			String serviceId) throws ServiceLinkRecordNotFoundException;

	public abstract ResponseEntity<ServiceLinkStatusRecordSigned> disableServiceLink(String slrId, String surrogateId,
			String serviceId) throws ServiceLinkRecordNotFoundException;

	public abstract ResponseEntity<String> notifyServiceLinkStatusChanged(String slrId,
			ServiceLinkStatusRecordSigned ssr) throws ServiceLinkRecordNotFoundException, JsonProcessingException,
			JOSEException, ParseException, ServiceLinkStatusRecordNotValid;

//	public abstract ResponseEntity<String> notifyConsentStatusChanged(String slrId, String crId,  ConsentStatusRecord csr);

	public abstract ResponseEntity<ConsentForm> fetchConsentForm(ConsentFormRequest request)
			throws ServiceManagerException, CapeSdkManagerException;

	public abstract ResponseEntity<ConsentRecordSigned> giveConsent(ConsentForm consentForm);

	public abstract ResponseEntity<String> storeNewConsentRecord(ConsentRecordSigned cr)
			throws ServiceLinkRecordNotFoundException, JsonProcessingException, ParseException, JOSEException,
			ConsentRecordNotValid, ConsentStatusRecordNotValid;

	public abstract ResponseEntity<?> deleteConsentRecord(String crId) throws ConsentRecordNotFoundException;

	public abstract ResponseEntity<String> updateConsentRecordWithNewStatus(String crId, ConsentRecordSigned updatedCr)
			throws ConsentStatusRecordNotValid, ConsentRecordNotFoundException, ServiceLinkRecordNotFoundException,
			JsonProcessingException, ParseException, JOSEException, ConsentRecordNotValid;

	public abstract ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsBySurrogateIdAndQuery(String surrogateId,
			String serviceId, String sourceServiceId, String datasetId, ConsentRecordStatusEnum status,
			String purposeId, String purposeName, PurposeCategory purposeCategory,
			ProcessingCategory processingCategory, Boolean checkConsentAtOperator, Sort.Direction iatSort);

	public abstract ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByUserIdAndQuery(String userId,
			String serviceId, String sourceServiceId, String datasetId, ConsentRecordStatusEnum status,
			String purposeId, String purposeName, PurposeCategory purposeCategory,
			ProcessingCategory processingCategory, Boolean checkConsentAtOperator, Sort.Direction iatSort);

	public abstract ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByServiceIdAndQuery(String serviceId,
			String userId, String sourceServiceId, String datasetId, ConsentRecordStatusEnum status, String purposeId,
			String purposeName, PurposeCategory purposeCategory, ProcessingCategory processingCategory,
			Boolean checkConsentAtOperator, Sort.Direction iatSort);

	public abstract ResponseEntity<ConsentRecordSigned> checkConsentRecordByServiceIdAndQuery(String serviceId,
			String userId, String sourceServiceId, String datasetId, String purposeId, String purposeName,
			PurposeCategory purposeCategory, ProcessingCategory processingCategory, Boolean checkConsentAtOperator,
			Sort.Direction iatSort) throws ConsentRecordNotFoundException;

	public abstract ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByBusinessIdAndQuery(String surrogateId,
			String serviceId, String sourceServiceId, String datasetId, ConsentRecordStatusEnum status,
			String purposeId, String purposeName, PurposeCategory purposeCategory,
			ProcessingCategory processingCategory, Boolean checkConsentAtOperator, Sort.Direction iatSort);

	public abstract ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsByBusinessIdAndQuery(
			String surrogateId, String serviceId, String sourceServiceId, String datasetId,
			ConsentRecordStatusEnum status, String purposeId, String purposeName, PurposeCategory purposeCategory,
			ProcessingCategory processingCategory, Sort.Direction iatSort);

	public abstract ResponseEntity<ConsentRecordSigned> changeConsentStatusFromService(String surrogateId, String slrId,
			String crId, ChangeConsentStatusRequest request)
			throws ConsentRecordNotFoundException, ConsentStatusNotValidException, ServiceLinkRecordNotFoundException,
			ServiceLinkStatusNotValidException, ServiceDescriptionNotFoundException;

	public abstract ResponseEntity<DataTransferResponse> startDataTransfer(DataTransferRequest dataTransfer,
			Boolean checkConsentAtOperator, String datasetId) throws ConsentRecordNotFoundException,
			JsonMappingException, JsonProcessingException, ParseException, ConsentStatusNotValidException,
			CapeSdkManagerException, JOSEException, ServiceManagerException, ServiceDescriptionNotFoundException;

	public abstract ResponseEntity<DataTransferResponse> postDataTransfer(DataTransferRequest body, String datasetId,
			String[] authorizationHeader) throws ConsentRecordNotFoundException, JsonMappingException,
			JsonProcessingException, ParseException, ServiceLinkRecordNotFoundException, ConsentStatusRecordNotValid,
			DatasetIdNotFoundException, JOSEException, DataRequestNotValid, CapeSdkManagerException;

	public abstract ResponseEntity<Object> enforceUsageRulesToPayload(String userId, String sinkServiceId,
			String sourceServiceId, String sinkServiceUrl, String sourceServiceUrl, String datasetId, String purposeId,
			String purposeName, PurposeCategory purposeCategory, ProcessingCategory processingCategory,
			Boolean checkConsentAtOperator, Map<String, Object> dataObject)
			throws ConsentRecordNotFoundException, ServiceManagerException, ServiceDescriptionNotFoundException;

	public abstract ResponseEntity<List<ServiceSignKey>> getRegisteredServicesKeys();

	public abstract ResponseEntity<List<ServiceEntry>> getServices(Boolean onlyRegistered, String serviceUrl)
			throws ServiceManagerException;

	public abstract ResponseEntity<ServiceEntry> getService(String serviceId, Boolean onlyRegistered)
			throws ServiceManagerException, ServiceDescriptionNotFoundException;

	public abstract ResponseEntity<String> getLinkSessionCode(String serviceId, String userId, String surrogateId,
			String returnUrl, Boolean forceLinking) throws ServiceManagerException, SessionNotFoundException;

	public abstract ResponseEntity<Account> createCapeAccount(Account account);

	public abstract ResponseEntity<?> deleteUserToSurrogateId(String surrogateId) throws CapeSdkManagerException;
}
