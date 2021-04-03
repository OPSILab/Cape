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


}
