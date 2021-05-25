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
package it.eng.opsi.cape.servicemanager;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.jwk.RSAKey;
import it.eng.opsi.cape.exception.SessionNotFoundException;
import it.eng.opsi.cape.exception.SessionStateNotAllowedException;
import it.eng.opsi.cape.servicemanager.model.ServicePopKey;
import it.eng.opsi.cape.servicemanager.model.linking.LinkingSession;
import it.eng.opsi.cape.servicemanager.model.linking.LinkingSessionStateEnum;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkRecordPayload;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkStatusRecordPayload;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkStatusEnum;
import it.eng.opsi.cape.servicemanager.repository.LinkingSessionRepository;

@Service
public class ServiceManager {

	@Autowired
	LinkingSessionRepository linkingSessionRepo;

	public String generateLinkingSessionCode(String accountId, String serviceId) {
		return RandomStringUtils.randomAlphanumeric(20);
	}

	public String generateCode(String operatorId, String serviceId, String surrogateId) {
		return operatorId + serviceId + surrogateId + RandomStringUtils.randomAlphanumeric(10);
	}

	public LinkingSession startSession(String sessionCode, String accountId, String serviceId, ZonedDateTime startedAt, Boolean toRecover) {

		/*
		 * Initializes surrogateId with serviceId, since that is provided only when
		 * writing partialSlrPayload
		 */
		LinkingSession newSession = new LinkingSession(sessionCode, LinkingSessionStateEnum.STARTED, accountId, serviceId,
				serviceId, startedAt, toRecover);
		return linkingSessionRepo.save(newSession);
	}

	public LinkingSession getSessionByCode(String sessionCode) throws SessionNotFoundException {

		return linkingSessionRepo.findBySessionCode(sessionCode)
				.orElseThrow(() -> new SessionNotFoundException("No active session with sessionCode: " + sessionCode + " was found"));
	}

	public LinkingSession getSessionByAccountIdAndServiceId(String accountId, String serviceId)
			throws SessionNotFoundException {

		return linkingSessionRepo.findFirstByAccountIdAndServiceId(accountId, serviceId)
				.orElseThrow(() -> new SessionNotFoundException("No active session for Account Id: " + accountId
						+ " and Service Id: " + serviceId + " was found"));
	}

	public LinkingSession changeSessionStateByCode(String sessionCode, LinkingSessionStateEnum newState)
			throws SessionNotFoundException, SessionStateNotAllowedException {

		LinkingSession currentLinkingSession = linkingSessionRepo.findBySessionCode(sessionCode)
				.orElseThrow(() -> new SessionNotFoundException("No active session with sessionCode: " + sessionCode + " was found"));

		LinkingSessionStateEnum currentState = currentLinkingSession.getState();

		checkStateAllowed(currentState, newState);

		currentLinkingSession.setState(newState);

		return linkingSessionRepo.save(currentLinkingSession);

	}

	public LinkingSession changeSessionState(LinkingSession sessionToChange, LinkingSessionStateEnum newState)
			throws SessionNotFoundException, SessionStateNotAllowedException {

		checkStateAllowed(sessionToChange.getState(), newState);
		sessionToChange.setState(newState);

		return linkingSessionRepo.save(sessionToChange);

	}

	public LinkingSession changeSessionState(LinkingSession sessionToChange, LinkingSessionStateEnum newState,
			Boolean toRecover) throws SessionNotFoundException, SessionStateNotAllowedException {

		checkStateAllowed(sessionToChange.getState(), newState);
		sessionToChange.setState(newState);
		sessionToChange.setToRecover(toRecover);
		
		return linkingSessionRepo.save(sessionToChange);

	}

	private void checkStateAllowed(LinkingSessionStateEnum currentState, LinkingSessionStateEnum newState)
			throws SessionStateNotAllowedException {

		Boolean stateAllowed = true;

		switch (newState) {

		case SLR_ID_STORED:
			if (!currentState.equals(LinkingSessionStateEnum.STARTED))
				stateAllowed = false;
			break;
		case ACCOUNT_SIGNED_SLR:
			if (!currentState.equals(LinkingSessionStateEnum.SLR_ID_STORED))
				stateAllowed = false;
			break;
		case DOUBLE_SIGNED_SLR:
			if (!currentState.equals(LinkingSessionStateEnum.ACCOUNT_SIGNED_SLR))
				stateAllowed = false;
			break;

		case FINAL_SLR_STORED:
			if (!currentState.equals(LinkingSessionStateEnum.DOUBLE_SIGNED_SLR))
				stateAllowed = false;
			break;

		case COMPLETED:
			if (!currentState.equals(LinkingSessionStateEnum.FINAL_SLR_STORED))
				stateAllowed = false;
			break;

		default:
			break;
		}

		if (!stateAllowed)
			throw new SessionStateNotAllowedException("The new Linking Session State: " + newState
					+ " is not allowed for current state: " + currentState);

	}

	public LinkingSession updateLinkingSession(LinkingSession toUpdate) {

		return linkingSessionRepo.save(toUpdate);

	}

	public void cancelSessionBySessionCode(String sessionCode) throws SessionNotFoundException {

		Long deletedItems = linkingSessionRepo.deleteLinkingSessionBySessionCode(sessionCode);

		if (deletedItems != 1)
			throw new SessionNotFoundException("No active session with sessionCode: " + sessionCode + " was found");

	}

	public void cancelSessionByAccountIdAndServiceId(String accountId, String serviceId)
			throws SessionNotFoundException {

		Long deletedItems = linkingSessionRepo.deleteLinkingSessionByAccountIdAndServiceId(accountId, serviceId);

		if (deletedItems != 1)
			throw new SessionNotFoundException("No active session for accountId: " + accountId + " and serviceId: "
					+ serviceId + " pair was found");

	}

	public void deleteSessionByAccountId(String accountId) {

		linkingSessionRepo.deleteLinkingSessionByAccountId(accountId);

	}

	public ServiceLinkRecordPayload generatePartialSlrPayload(String version, String slrId, String operatorId,
			String serviceId, String serviceUri, String serviceName, ServicePopKey popKey,
			String serviceDescriptionVersion, String surrogateId, RSAKey operatorKey, ZonedDateTime iat,
			String accountId, String username) {

		return new ServiceLinkRecordPayload(version, slrId, operatorId, serviceId, serviceUri, serviceName, popKey,
				serviceDescriptionVersion, surrogateId, operatorKey, iat);
	}

	public ServiceLinkStatusRecordPayload generateSsrPayload(String version, String ssrId, String surrogate,
			String slrId, ZonedDateTime iat, ServiceLinkStatusEnum status, String prevRecordId) {
		return new ServiceLinkStatusRecordPayload(version, ssrId, surrogate, slrId, status, iat, prevRecordId);

	}

}
