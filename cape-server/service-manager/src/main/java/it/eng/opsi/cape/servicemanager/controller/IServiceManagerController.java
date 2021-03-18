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
