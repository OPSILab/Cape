/*******************************************************************************
 * CaPe - a Consent Based Personal Data Suite
 *   Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.opsi.cape.accountmanager.controller;

import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.Base64;

import it.eng.opsi.cape.accountmanager.model.consenting.ConsentStatusRecordPayload;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.accountmanager.model.consenting.ThirdPartyReuseConsentSignRequest;
import it.eng.opsi.cape.accountmanager.model.consenting.ThirdPartyReuseConsentSignResponse;
import it.eng.opsi.cape.accountmanager.model.consenting.WithinServiceConsentSignRequest;
import it.eng.opsi.cape.accountmanager.model.consenting.WithinServiceConsentSignResponse;
import it.eng.opsi.cape.accountmanager.model.linking.AccountSignSlrRequest;
import it.eng.opsi.cape.accountmanager.model.linking.AccountSignSlrResponse;
import it.eng.opsi.cape.accountmanager.model.linking.FinalStoreSlrRequest;
import it.eng.opsi.cape.accountmanager.model.linking.FinalStoreSlrResponse;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkInitResponse;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkStatusRecordPayload;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.accountmanager.model.linking.SinkServiceLinkInitRequest;
import it.eng.opsi.cape.accountmanager.model.linking.SourceServiceLinkInitRequest;
import it.eng.opsi.cape.exception.AccountManagerException;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordAlreadyPresentException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.exception.SessionNotFoundException;
import it.eng.opsi.cape.exception.SessionStateNotAllowedException;

public interface IServiceLinkingController {

	public abstract ResponseEntity<ServiceLinkInitResponse> storeSinkSlrId(String accountId,
			SinkServiceLinkInitRequest request) throws SessionNotFoundException, AccountManagerException,
			AccountNotFoundException, ServiceLinkRecordAlreadyPresentException, SessionStateNotAllowedException;

	public abstract ResponseEntity<ServiceLinkInitResponse> storeSourceSlrId(String accountId,
			SourceServiceLinkInitRequest request) throws SessionNotFoundException, AccountManagerException,
			AccountNotFoundException, ServiceLinkRecordAlreadyPresentException, SessionStateNotAllowedException;

	public abstract ResponseEntity<AccountSignSlrResponse> accountSignSlr(String accountId, String slrId,
			AccountSignSlrRequest request) throws SessionNotFoundException, AccountManagerException, JOSEException,
			AccountNotFoundException, JsonProcessingException, SessionStateNotAllowedException;

	public abstract ResponseEntity<FinalStoreSlrResponse> storeFinalSlr(FinalStoreSlrRequest request, String accountId,
			String slrId) throws SessionNotFoundException, AccountManagerException, JsonProcessingException,
			ParseException, JOSEException, AccountNotFoundException, ServiceLinkRecordAlreadyPresentException,
			ServiceLinkRecordNotFoundException, SessionStateNotAllowedException;

	public abstract ResponseEntity<ServiceLinkStatusRecordSigned> signAndStoreServiceLinkStatus(String accountId,
			String slrId, ServiceLinkStatusRecordPayload newStatus)
			throws ServiceLinkRecordNotFoundException, AccountNotFoundException, JsonProcessingException, JOSEException;

	public abstract ResponseEntity<ServiceLinkStatusRecordSigned> storeSignedServiceLinkStatus(String surrogateId,
			String slrId, @Valid ServiceLinkStatusRecordSigned newSsr)
			throws ServiceLinkRecordNotFoundException, AccountNotFoundException, JsonProcessingException, JOSEException;

	public abstract ResponseEntity<WithinServiceConsentSignResponse> signWithinServiceConsent(String surrogateId,
			String slrId, WithinServiceConsentSignRequest request)
			throws AccountNotFoundException, JsonProcessingException, JOSEException;

	public abstract ResponseEntity<ThirdPartyReuseConsentSignResponse> signThirdPartyReuseConsent(String surrogateId,
			String sinkSlrId, String sourceSlrId, ThirdPartyReuseConsentSignRequest request)
			throws AccountNotFoundException, ServiceLinkRecordNotFoundException, JsonProcessingException, JOSEException;

	public abstract ResponseEntity<ConsentStatusRecordSigned> signConsentStatusRecord(String accountId, String slrId,
			ConsentStatusRecordPayload consentStatusRecord)
			throws AccountNotFoundException, JsonProcessingException, JOSEException, ServiceLinkRecordNotFoundException;

	ResponseEntity<Base64> signConsentStatusRecordsList(String accountId, String slrId,
			@Valid List<ConsentStatusRecordSigned> consentStatusRecord)
			throws AccountNotFoundException, JsonProcessingException, JOSEException, ServiceLinkRecordNotFoundException;

}
