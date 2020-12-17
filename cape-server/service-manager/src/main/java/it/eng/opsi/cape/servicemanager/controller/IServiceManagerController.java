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
package it.eng.opsi.cape.servicemanager.controller;

import java.text.ParseException;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;

import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.ConflictingSessionFoundException;
import it.eng.opsi.cape.exception.OperatorDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkStatusConflictingException;
import it.eng.opsi.cape.exception.ServiceLinkingRedirectUriMismatchException;
import it.eng.opsi.cape.exception.ServiceManagerException;
import it.eng.opsi.cape.exception.SessionNotFoundException;
import it.eng.opsi.cape.exception.SessionStateNotAllowedException;
import it.eng.opsi.cape.servicemanager.model.linking.ChangeSlrStatusRequestFrom;
import it.eng.opsi.cape.servicemanager.model.linking.ContinueLinkingRequest;
import it.eng.opsi.cape.servicemanager.model.linking.LinkingSession;
import it.eng.opsi.cape.servicemanager.model.linking.LinkingSessionStateEnum;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.servicemanager.model.linking.account.FinalStoreSlrResponse;

public interface IServiceManagerController {

	public abstract ResponseEntity<String> startLinkingFromOperatorRedirectToService(String accountId, String serviceId,
			Boolean forceLinking) throws OperatorDescriptionNotFoundException, ServiceManagerException,
			ServiceDescriptionNotFoundException, ConflictingSessionFoundException;
	
		public abstract ResponseEntity<String> startLinkingFromServiceAfterOperatorLogin(String accountId,
			String surrogateId, String serviceId, String returnUrl, Boolean forceLinking, Boolean forceCode)
			throws ServiceManagerException, ServiceDescriptionNotFoundException, ConflictingSessionFoundException,
			ServiceLinkingRedirectUriMismatchException;

	public abstract ResponseEntity<FinalStoreSlrResponse> linkService(ContinueLinkingRequest request)
			throws SessionNotFoundException, ServiceManagerException, ServiceDescriptionNotFoundException,
			OperatorDescriptionNotFoundException, JOSEException, JsonProcessingException, ParseException,
			SessionStateNotAllowedException;

	public abstract ResponseEntity<LinkingSession> getLinkingSessionByCode(String code) throws SessionNotFoundException;

	public abstract ResponseEntity<Object> cancelLinkingSessionByCode(String code) throws SessionNotFoundException;

	public abstract ResponseEntity<LinkingSession> changeLinkingSessionStateByCode(String code,
			LinkingSessionStateEnum newState) throws SessionNotFoundException, SessionStateNotAllowedException;

	public abstract ResponseEntity<ServiceLinkStatusRecordSigned> disableServiceLink(String accountId, String serviceId,
			String slrId, ChangeSlrStatusRequestFrom requestFrom)
			throws ServiceManagerException, ServiceLinkRecordNotFoundException, ServiceDescriptionNotFoundException,
			OperatorDescriptionNotFoundException, JsonProcessingException, JOSEException,
			ServiceLinkStatusConflictingException, AccountNotFoundException;

	public abstract ResponseEntity<ServiceLinkStatusRecordSigned> enableServiceLink(String accountId, String serviceId,
			String slrId, ChangeSlrStatusRequestFrom requestFrom)
			throws ServiceManagerException, ServiceLinkRecordNotFoundException, ServiceDescriptionNotFoundException,
			OperatorDescriptionNotFoundException, JsonProcessingException, JOSEException,
			ServiceLinkStatusConflictingException, AccountNotFoundException;

	public abstract ResponseEntity<Object> deleteLinkingSessionByAccountId(String accountId);

}
