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
package it.eng.opsi.cape.servicemanager;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.CurrentDateTimeProvider;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.X509CertUtils;

import it.eng.opsi.cape.exception.SessionNotFoundException;
import it.eng.opsi.cape.exception.SessionStateNotAllowedException;
import it.eng.opsi.cape.servicemanager.model.ServicePopKey;
import it.eng.opsi.cape.servicemanager.model.linking.LinkingSession;
import it.eng.opsi.cape.servicemanager.model.linking.LinkingSessionStateEnum;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkRecordPayload;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkStatusRecordPayload;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkRecordDoubleSigned.ServiceLinkRecordSignature;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkStatusEnum;
import it.eng.opsi.cape.servicemanager.model.linking.service.ServiceSignSlrResponse;
import it.eng.opsi.cape.servicemanager.repository.LinkingSessionRepository;

@Service
public class ServiceManager {

	@Autowired
	LinkingSessionRepository linkingSessionRepo;

	public String generateCode(String accountId, String serviceId) {
		return RandomStringUtils.randomAlphanumeric(20);
	}

	public String generateCode(String operatorId, String serviceId, String surrogateId) {
		return operatorId + serviceId + surrogateId + RandomStringUtils.randomAlphanumeric(10);
	}

	public LinkingSession startSession(String code, String accountId, String serviceId, ZonedDateTime startedAt) {

		/*
		 * Initializes surrogateId with serviceId, since that is provided only when
		 * writing partialSlrPayload
		 */
		LinkingSession newSession = new LinkingSession(code, LinkingSessionStateEnum.STARTED, accountId, serviceId,
				serviceId, startedAt);
		return linkingSessionRepo.save(newSession);
	}

	public LinkingSession getSessionByCode(String code) throws SessionNotFoundException {

		return linkingSessionRepo.findByCode(code)
				.orElseThrow(() -> new SessionNotFoundException("No active session with code: " + code + " was found"));
	}

	public LinkingSession getSessionByAccountIdAndServiceId(String accountId, String serviceId)
			throws SessionNotFoundException {

		return linkingSessionRepo.findFirstByAccountIdAndServiceId(accountId, serviceId)
				.orElseThrow(() -> new SessionNotFoundException("No active session for Account Id: " + accountId
						+ " and Service Id: " + serviceId + " was found"));
	}

	public LinkingSession changeSessionStatusByCode(String code, LinkingSessionStateEnum newState)
			throws SessionNotFoundException, SessionStateNotAllowedException {

		LinkingSession currentLinkingSession = linkingSessionRepo.findByCode(code)
				.orElseThrow(() -> new SessionNotFoundException("No active session with code: " + code + " was found"));

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

	public void cancelSessionByCode(String code) throws SessionNotFoundException {

		Long deletedItems = linkingSessionRepo.deleteLinkingSessionByCode(code);

		if (deletedItems != 1)
			throw new SessionNotFoundException("No active session with code: " + code + " was found");

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
				serviceDescriptionVersion, surrogateId, operatorKey, new ArrayList<RSAKey>(), iat);
	}

	public ServiceLinkStatusRecordPayload generateSsrPayload(String version, String ssrId, String surrogate,
			String slrId, ZonedDateTime iat, ServiceLinkStatusEnum status, String prevRecordId) {
		return new ServiceLinkStatusRecordPayload(version, ssrId, surrogate, slrId, status, iat, prevRecordId);

	}

}
