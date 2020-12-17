/*******************************************************************************
 * CaPe - a Consent Based Personal Data Suite
 *  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package it.eng.opsi.cape.sdk.controller;

import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
import it.eng.opsi.cape.exception.OperatorDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkStatusNotValidException;
import it.eng.opsi.cape.exception.ServiceLinkStatusRecordNotValid;
import it.eng.opsi.cape.exception.ServiceManagerException;
import it.eng.opsi.cape.exception.ServiceSignKeyNotFoundException;
import it.eng.opsi.cape.exception.SessionNotFoundException;
import it.eng.opsi.cape.exception.SessionStateNotAllowedException;
import it.eng.opsi.cape.exception.UserSurrogateIdLinkNotFoundException;
import it.eng.opsi.cape.sdk.model.OperatorDescription;
import it.eng.opsi.cape.sdk.model.ServiceSignKey;
import it.eng.opsi.cape.sdk.model.SurrogateIdResponse;
import it.eng.opsi.cape.sdk.model.consenting.ChangeConsentStatusRequest;
import it.eng.opsi.cape.sdk.model.consenting.ConsentForm;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.sdk.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.sdk.model.datatransfer.DataTransferRequest;
import it.eng.opsi.cape.sdk.model.datatransfer.DataTransferResponse;
import it.eng.opsi.cape.sdk.model.linking.FinalLinkingResponse;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceSignSlrRequest;
import it.eng.opsi.cape.sdk.model.linking.ServiceSignSlrResponse;
import it.eng.opsi.cape.sdk.model.linking.StartLinkingRequest;
import it.eng.opsi.cape.sdk.model.linking.UserSurrogateIdLink;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;

public interface ICapeServiceSdkController {

	public abstract ResponseEntity<OperatorDescription> getOperatorDescription(String operatorId)
			throws OperatorDescriptionNotFoundException, ServiceManagerException;

	public abstract ResponseEntity<FinalLinkingResponse> startServiceLinking(StartLinkingRequest request)
			throws ServiceManagerException, OperatorDescriptionNotFoundException, JOSEException,
			ServiceDescriptionNotFoundException, SessionNotFoundException;

	public abstract ResponseEntity<SurrogateIdResponse> generateSurrogateId(String operatorId, String userId);

	public abstract ResponseEntity<ServiceEntry> registerServiceToCape(String serviceId)
			throws JOSEException, ServiceManagerException, JsonProcessingException, ServiceDescriptionNotFoundException;

	public abstract ResponseEntity<ServiceSignSlrResponse> signServiceLinkRecordPayload(ServiceSignSlrRequest request)
			throws JsonProcessingException, JOSEException, ParseException, ServiceSignKeyNotFoundException,
			SessionNotFoundException, ServiceManagerException, SessionStateNotAllowedException;

	public abstract ResponseEntity<Object> unregisterService(String serviceId, Boolean deleteServiceDescription)
			throws JOSEException, ServiceManagerException, ServiceSignKeyNotFoundException;

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

	public abstract ResponseEntity<ConsentForm> fetchConsentForm(String surrogateId, String serviceId, String purposeId,
			String sourceDatasetId, String sourceServiceId) throws ServiceManagerException, CapeSdkManagerException;

	public abstract ResponseEntity<ConsentRecordSigned> giveConsent(String surrogateId, ConsentForm consentForm);

	public abstract ResponseEntity<String> storeNewConsentRecord(ConsentRecordSigned cr)
			throws ServiceLinkRecordNotFoundException, JsonProcessingException, ParseException, JOSEException,
			ConsentRecordNotValid, ConsentStatusRecordNotValid;

	public abstract ResponseEntity<String> updateConsentRecordWithNewStatus(@Valid ConsentRecordSigned updatedCr)
			throws ConsentStatusRecordNotValid, ConsentRecordNotFoundException, ServiceLinkRecordNotFoundException,
			JsonProcessingException, ParseException, JOSEException, ConsentRecordNotValid;

	public abstract ResponseEntity<ConsentRecordSigned[]> getConsentRecordsBySurrogateId(String surrogateId,
			Boolean checkConsentAtOperator);

	public abstract ResponseEntity<ConsentRecordSigned[]> getConsentRecordsBySurrogateIdAndPurposeId(String surrogateId,
			String purposeId, Boolean checkConsentAtOperator);

	public abstract ResponseEntity<ConsentRecordSigned[]> getConsentRecordsByServiceId(String serviceId,
			Boolean checkConsentAtOperator);

	public abstract ResponseEntity<ConsentRecordSigned[]> getConsentRecords(Boolean checkConsentAtOperator);

	public abstract ResponseEntity<ConsentStatusRecordSigned> changeConsentStatusFromService(String surrogateId,
			String slrId, String crId, ChangeConsentStatusRequest request)
			throws ConsentRecordNotFoundException, ConsentStatusNotValidException, ServiceLinkRecordNotFoundException,
			ServiceLinkStatusNotValidException, ServiceDescriptionNotFoundException;

	public abstract ResponseEntity<DataTransferResponse> startDataTransfer(DataTransferRequest dataTransfer,
			Boolean checkConsentAtOperator, String datasetId) throws ConsentRecordNotFoundException,
			JsonMappingException, JsonProcessingException, ParseException, ConsentStatusNotValidException,
			CapeSdkManagerException, JOSEException, ServiceManagerException, ServiceDescriptionNotFoundException;

	public abstract ResponseEntity<DataTransferResponse> postDataTransfer(@Valid DataTransferRequest body,
			String datasetId, String[] authorizationHeader) throws ConsentRecordNotFoundException, JsonMappingException,
			JsonProcessingException, ParseException, ServiceLinkRecordNotFoundException, ConsentStatusRecordNotValid,
			DatasetIdNotFoundException, JOSEException, DataRequestNotValid, CapeSdkManagerException;

	public abstract ResponseEntity<List<ServiceSignKey>> getRegisteredServicesKeys();

	public abstract ResponseEntity<List<ServiceEntry>> getServices(Boolean onlyRegistered)
			throws ServiceManagerException;

	public abstract ResponseEntity<ServiceEntry> getService(String serviceId, Boolean onlyRegistered)
			throws ServiceManagerException, ServiceDescriptionNotFoundException;

	public abstract ResponseEntity<String> getLinkSessionCode(String serviceId, String userId, String surrogateId, String returnUrl)
			throws ServiceManagerException, SessionNotFoundException;

}
