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

import java.net.URI;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import it.eng.opsi.cape.servicemanager.ApplicationProperties;
import it.eng.opsi.cape.servicemanager.ServiceManager;
import it.eng.opsi.cape.servicemanager.model.OperatorDescription;
import it.eng.opsi.cape.servicemanager.model.ServicePopKey;
import it.eng.opsi.cape.servicemanager.model.audit.EventType;
import it.eng.opsi.cape.servicemanager.model.audit.ServiceLinkActionType;
import it.eng.opsi.cape.servicemanager.model.audit.ServiceLinkEventLog;
import it.eng.opsi.cape.servicemanager.model.consenting.ChangeConsentStatusRequest.ChangeConsentStatusRequestFrom;
import it.eng.opsi.cape.servicemanager.model.consenting.ConsentRecordPayload;
import it.eng.opsi.cape.servicemanager.model.consenting.ConsentRecordSignedPair;
import it.eng.opsi.cape.servicemanager.model.consenting.ConsentRecordStatusEnum;
import it.eng.opsi.cape.servicemanager.model.consenting.ConsentStatusRecordPayload;
import it.eng.opsi.cape.servicemanager.model.linking.ChangeSlrStatusRequestFrom;
import it.eng.opsi.cape.servicemanager.model.linking.ContinueLinkingRequest;
import it.eng.opsi.cape.servicemanager.model.linking.ContinueSinkLinkingRequest;
import it.eng.opsi.cape.servicemanager.model.linking.account.FinalStoreSlrResponse;
import it.eng.opsi.cape.servicemanager.model.linking.LinkingSession;
import it.eng.opsi.cape.servicemanager.model.linking.LinkingSessionStateEnum;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkRecordPayload;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkStatusEnum;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkStatusRecordPayload;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.servicemanager.model.linking.account.AccountSignSlrResponse;
import it.eng.opsi.cape.servicemanager.model.linking.account.ServiceLinkInitResponse;
import it.eng.opsi.cape.servicemanager.model.linking.service.ServiceSignSlrResponse;
import it.eng.opsi.cape.servicemanager.repository.OperatorDescriptionRepository;
import it.eng.opsi.cape.servicemanager.service.ClientService;
import it.eng.opsi.cape.servicemanager.service.CryptoService;
import it.eng.opsi.cape.serviceregistry.data.Cert;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import lombok.extern.slf4j.Slf4j;

@OpenAPIDefinition(tags = {
		@Tag(name = "Service Linking", description = "Service Manager APIs to handle internal service linking operations.") }, info = @Info(title = "CaPe Service Manager API", description = "Service Manager API used by CaPe compliant services.", version = "2.0"))
@RestController
@RequestMapping("/api/v2")
@Slf4j
@CrossOrigin(exposedHeaders = "*,Location")
public class ServiceManagerController implements IServiceManagerController {

	private final ApplicationProperties appProperty;
	private final String operatorId;

	@Autowired
	OperatorDescriptionRepository operatorRepo;

	@Autowired
	ClientService clientService;

	@Autowired
	CryptoService cryptoService;

	@Autowired
	ServiceManager serviceManager;

	@Autowired
	public ServiceManagerController(ApplicationProperties appProperty) {
		this.appProperty = appProperty;
		this.operatorId = this.appProperty.getCape().getOperatorId();
	}

	/**
	 * (Linking started from Operator) Initiate Service Linking from Operator ---->
	 * Service Manager
	 * 
	 * @throws OperatorDescriptionNotFoundException
	 * @throws ServiceDescriptionNotFoundException
	 * @throws ServiceManagerException
	 * @throws ConflictingSessionFoundException
	 */
	@Operation(summary = "Start Process of creating Service Link Record (from CaPe).", description = "Entrypoint for creating new Service Link Record with service."
			+ "Will take Service Id to link with as parameter. Returns redirect to Service Login. This endpoint will start Service Linking process.", tags = {
					"Service Linking" }, responses = {
							@ApiResponse(description = "Returns Redirect to Service Login page for authentication.", responseCode = "302") })
	@Override
	@GetMapping(value = "/slr/account/{accountId}/service/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> startLinkingFromOperatorRedirectToService(@PathVariable("accountId") String accountId,
			@PathVariable("serviceId") String serviceId,
			@RequestParam(name = "forceLinking", defaultValue = "false") Boolean forceLinking)
			throws OperatorDescriptionNotFoundException, ServiceManagerException, ServiceDescriptionNotFoundException,
			ConflictingSessionFoundException {

		/*
		 * getServiceDescription
		 */
		ServiceEntry serviceDescription = clientService.getServiceDescriptionFromRegistry(serviceId);

		/*
		 * generateCode if there is no pending Linking Session for the accountId -
		 * serviceId pair
		 */
		Boolean toRecover = false; // To be set with the values of eventual existing Linking Session

		try {
			LinkingSession session = serviceManager.getSessionByAccountIdAndServiceId(accountId, serviceId);

			/*
			 * 
			 */
			// Bad Request, trying to start an already started Linking Session for Account -
			// Service combination
			if (session.getState().equals(LinkingSessionStateEnum.COMPLETED))
				// TODO Check also if there is the corresponding Service Link Record in the
				// Account? (It should be, since the Linking Session is COMPLETED)
				throw new ConflictingSessionFoundException(
						"Trying to start a Linking Session for an already existing Service Link between Account and Service",
						LinkingSessionStateEnum.COMPLETED);
			else if (!forceLinking) {
				throw new ConflictingSessionFoundException(
						"Trying to start an already pending Linking Session for Account - Service combination, try to complete or cancel the pending linking",
						session.getState());

				// Force Linking -> Delete previous linking session and continue linking
			} else {
				toRecover = session.getToRecover(); // TO propagate the value to the new forced Linking Session
				serviceManager.cancelSessionByAccountIdAndServiceId(accountId, serviceId);
				throw new SessionNotFoundException("It triggers the catch below to continue linking");
			}

		} catch (SessionNotFoundException e) {

			/*
			 * No Linking Session found, create a new one
			 */
			String sessionCode = serviceManager.generateLinkingSessionCode(accountId, serviceId);

			String serviceLoginUri = serviceDescription.getServiceInstance().getServiceUrls().getLoginUri();

			OperatorDescription operatorDescription = operatorRepo.findByOperatorId(operatorId).orElseThrow(
					() -> new OperatorDescriptionNotFoundException("No operator found with Id: " + operatorId));
			String operatorLinkingReturnUrl = operatorDescription.getOperatorUrls().getLinkingRedirectUri();

			HttpHeaders headers = new HttpHeaders();
			headers.add("Location", serviceLoginUri + "?sessionCode=" + sessionCode + "&operatorId=" + operatorId
					+ "&returnUrl=" + operatorLinkingReturnUrl + "&linkingFrom=Operator");

			/*
			 * startSession
			 */
			// TODO Check if Account corresponding to AccountId exists (call Account
			// Manager)
			serviceManager.startSession(sessionCode, accountId, serviceId, ZonedDateTime.now(ZoneId.of("UTC")),
					toRecover);

			return new ResponseEntity<String>(null, headers, HttpStatus.OK);

		}
	}

	/**
	 * (Linking started from Service) Initiate linking after Operator login or with
	 * forceCode automatically from Service ----> Service Manager
	 * 
	 * @throws OperatorDescriptionNotFoundException
	 * @throws ServiceDescriptionNotFoundException
	 * @throws ServiceManagerException
	 * @throws ConflictingSessionFoundException
	 * @throws ServiceLinkingRedirectUriMismatchException
	 */
	@Operation(summary = "Start Process of creating Service Link Record.", description = "Entrypoint for creating new Service Link Record with service."
			+ "Will take Service Id to link with as parameter. If forceLinkCode=true return sessionCode to finalize service link from service side (sdk). This endpoint will start Service Linking process.", tags = {
					"Service Linking" }, responses = {
							@ApiResponse(description = "Returns Redirect to Service Login page for authentication.", responseCode = "302") })
	@Override
	@GetMapping(value = "/slr/service/{serviceId}/account/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> startLinkingFromServiceAfterOperatorLogin(@PathVariable("accountId") String accountId,
			@RequestParam("surrogateId") String surrogateId, @PathVariable("serviceId") String serviceId,
			@RequestParam("returnUrl") String returnUrl,
			@RequestParam(name = "forceLinking", defaultValue = "false") Boolean forceLinking,
			@RequestParam(name = "forceLinkCode", defaultValue = "false") Boolean forceCode)
			throws ServiceManagerException, ServiceDescriptionNotFoundException, ConflictingSessionFoundException,
			ServiceLinkingRedirectUriMismatchException {

		/*
		 * getServiceDescription
		 */
		ServiceEntry serviceDescription = clientService.getServiceDescriptionFromRegistry(serviceId);

		/*
		 * generateCode if there is no pending Linking Session for the accountId -
		 * serviceId pair
		 */

		Boolean toRecover = false; // To be set with the values of eventual existing Linking Session
		try {
			LinkingSession session = serviceManager.getSessionByAccountIdAndServiceId(accountId, serviceId);

			/*
			 * 
			 */
			// Bad Request, trying to start an already started Linking Session for Account -
			// Service combination (if not Force Linking)

			if (session.getState().equals(LinkingSessionStateEnum.COMPLETED))
				// TODO Check also if there is the corresponding Service Link Record in the
				// Account? (It should be, since the Linking Session is COMPLETED)
				throw new ConflictingSessionFoundException(
						"Trying to start a Linking Session for an already existing Service Link between Account and Service",
						LinkingSessionStateEnum.COMPLETED);
			else if (!forceLinking) {
				throw new ConflictingSessionFoundException(
						"Trying to start an already pending Linking Session for Account - Service combination, try to complete the pending linking by retrying with forceLinking=true",
						session.getState());

				// Force Linking -> Delete previous linking session and continue linking

			} else {
				toRecover = session.getToRecover(); // TO propagate the value to the new forced Linking Session
				serviceManager.cancelSessionByAccountIdAndServiceId(accountId, serviceId);
				throw new SessionNotFoundException("It triggers the catch below to continue linking");
			}

		} catch (SessionNotFoundException e) {

			/*
			 * No Linking Session found, create a new one
			 */
			String sessionCode = serviceManager.generateLinkingSessionCode(accountId, serviceId);

			String serviceLinkingUri = serviceDescription.getServiceInstance().getServiceUrls().getLinkingUri();

			String serviceLinkingReturnUrl = serviceDescription.getServiceInstance().getServiceUrls()
					.getLinkingRedirectUri();

			// Check if input Service returnUrl starts with the linkingReturnUrl in the
			// corresponding Service Description
			if (!returnUrl.startsWith(serviceLinkingReturnUrl))
				throw new ServiceLinkingRedirectUriMismatchException(
						"The Return Url in input mismatches with the one present in the Service description");
			/*
			 * startSession
			 */
			// TODO Check if Account corresponding to AccountId exists (call Account
			// Manager)
			// In case of automatic linking (forceCode=true) the accountId in input is
			// instead the Service User Id
			serviceManager.startSession(sessionCode, accountId, serviceId, ZonedDateTime.now(ZoneId.of("UTC")),
					toRecover);

			/*
			 * If forceCode is true, return directly the Linking Session Code in order to
			 * let the Service to perform automatic linking without performing redirect to
			 * Operator (Cape Dashboard)
			 */
			if (forceCode)
				return ResponseEntity.ok(sessionCode);
			else {
				clientService.callStartServiceLinking(sessionCode, surrogateId, operatorId, serviceId,
						serviceLinkingUri);
				return ResponseEntity.ok(null);
			}

		}
	}

	/**
	 * 
	 * Continue Service Linking after authenticating both at Service and Operator
	 * (and the Service created the PoPKey). SDK ----> Service Manager
	 * 
	 * @throws SessionNotFoundException
	 * @throws ServiceDescriptionNotFoundException
	 * @throws ServiceManagerException
	 * @throws OperatorDescriptionNotFoundException
	 * @throws JOSEException
	 * @throws ParseException
	 * @throws JsonProcessingException
	 * @throws SessionStateNotAllowedException
	 * 
	 */
	@SuppressWarnings("finally")
	@Operation(summary = "Inform Operator we're ready to continue creation of SLR.", description = "End point where Service Mgmnt POSTs after user has been authenticated by the Service in order to deliver surrogate id to Operator. This endpoint is part of Service Linking transaction and should not be called independently.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 CREATED and the created SLR", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FinalStoreSlrResponse.class))), })
	@Override
	@PostMapping(value = "/slr/link")
	public ResponseEntity<FinalStoreSlrResponse> linkService(@RequestBody @Valid ContinueLinkingRequest request)
			throws SessionNotFoundException, ServiceManagerException, ServiceDescriptionNotFoundException,
			OperatorDescriptionNotFoundException, JOSEException, JsonProcessingException, ParseException,
			SessionStateNotAllowedException {

		String sessionCode = request.getSessionCode();
		String surrogateId = request.getSurrogateId();
		String serviceId = request.getServiceId();

		/*
		 * getServiceDescription from Service Registry
		 */
		ServiceEntry serviceDescription = clientService.getServiceDescriptionFromRegistry(serviceId);
		String serviceUri = serviceDescription.getIdentifier();
		String serviceSdkHost = serviceDescription.getServiceInstance().getServiceUrls().getLibraryDomain();
		String serviceDescriptionVersion = serviceDescription.getServiceDescriptionVersion();
		String serviceName = serviceDescription.getName();
		Cert serviceCertificate = serviceDescription.getServiceInstance().getCert();

		/*
		 * Get session by input sessionCode, check if is in an allowed State and if its
		 * serviceId matches the ones in input
		 */
		LinkingSession session = serviceManager.getSessionByCode(sessionCode);
		if (!session.getState().equals(LinkingSessionStateEnum.STARTED))
			throw new SessionStateNotAllowedException(
					"The Linking Session should be in STARTED state, " + session.getState() + " found instead");

		// Get AccountId starting from the retrieved Session
		String accountId = session.getAccountId();

		if (!session.getServiceId().equals(serviceId))
			throw new SessionNotFoundException("The Session serviceId does not match the one in input");

		/*
		 * Check if there is an existing, conflicting SurrogateId (for Linking started
		 * from Operator, which started a new Session only checking AccountId and
		 * ServiceId. Try to update the Session with the surrogateId, in which there is
		 * a unique index
		 */
		session.setSurrogateId(surrogateId);

		try {
			serviceManager.updateLinkingSession(session);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SessionStateNotAllowedException(
					"The Linking Session could not be continued because there is already another session with surrogateId: "
							+ surrogateId);
		}
		/*
		 * Get Operator Description
		 */
		OperatorDescription operatorDescription = operatorRepo.findByOperatorId(operatorId).orElseThrow(
				() -> new OperatorDescriptionNotFoundException("No operator found with Id: " + operatorId));

		/*
		 * Get Operator Public key (JWK) from Operator X509 certificate
		 */
		RSAKey operatorPublicKey = (RSAKey) cryptoService.getPublicKeyFromCertificate(operatorDescription);

		/*
		 * generateServiceLinkRecordId
		 */
		String slrId = new ObjectId().toString();

		// if Sink Service, get the POP Key from the request
		ServiceLinkInitResponse storeSlrIdResponse = null;
		ServicePopKey popKey = null;

		if (request instanceof ContinueSinkLinkingRequest) {
			popKey = ((ContinueSinkLinkingRequest) request).getPopKey();

			// Call storeSinkSlrId
			storeSlrIdResponse = clientService.callStoreSinkSlrId(accountId, sessionCode, slrId, serviceId, surrogateId,
					popKey);
		} else {
			// Call storeSourceSlrId
			storeSlrIdResponse = clientService.callStoreSourceSlrId(accountId, sessionCode, slrId, serviceId,
					surrogateId);
		}

		/*
		 * Update The Linking Session to the next state: STARTED -> SLR_ID_STORED
		 */
		serviceManager.changeSessionState(session, LinkingSessionStateEnum.SLR_ID_STORED);

		/*
		 * TODO Get username of Account from Account Manager ? For now username and
		 * accountId match
		 */

		/*
		 * Generate Partial SLR Payload
		 */
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
		ServiceLinkRecordPayload partialSlrPayload = serviceManager.generatePartialSlrPayload(
				appProperty.getCape().getVersion(), slrId, operatorId, serviceId, serviceUri, serviceName, popKey,
				serviceDescriptionVersion, surrogateId, operatorPublicKey, now, accountId, accountId);

		/*
		 * Sign SLR with Account Owner key
		 */
		AccountSignSlrResponse accountSignResponse;
		try {
			accountSignResponse = clientService.callAccountSignSlr(accountId, slrId, partialSlrPayload, sessionCode);

			/*
			 * In case of Failure, do compensating transaction/rollback (Delete Partial SLR
			 * on Account Manager)
			 */
		} catch (Exception e) {
			try {
				clientService.callDeletePartialSlr(accountId, slrId);
				serviceManager.changeSessionState(session, LinkingSessionStateEnum.STARTED, false);

			} catch (Exception ex) {
				/*
				 * If rollback failed, set toRecover, in order to let Account Manager in
				 * eventual further linking attempts that the partial SLR payload has to be
				 * restored
				 * 
				 */
				serviceManager.changeSessionState(session, LinkingSessionStateEnum.STARTED, true);
				throw ex;
			} finally {
				throw new ServiceManagerException(
						"There was an error while contacting Account Manager to sign partial SLR payload: "
								+ e.getMessage());
			}

		}

		/*
		 * Update The Linking Session to the next state: SLR_ID_STORED ->
		 * ACCOUNT_SIGNED_SLR
		 */
		serviceManager.changeSessionState(session, LinkingSessionStateEnum.ACCOUNT_SIGNED_SLR, false);

		/*
		 * Sign Account Signed SLR with Service Key
		 */
		ServiceSignSlrResponse serviceSignResponse = clientService.callServiceSignSlr(serviceSdkHost,
				accountSignResponse.getAccountSignedSlr(), surrogateId, slrId, sessionCode);

		/*
		 * Update The Linking Session to the next state: ACCOUNT_SIGNED_SLR ->
		 * DOUBLE_SIGNED_SLR
		 */
		serviceManager.changeSessionState(session, LinkingSessionStateEnum.DOUBLE_SIGNED_SLR);

		// Get Service Certificate directly from x5c to get Service
		// Public key or from getting certificate through x5u URI

		String certificateUri = serviceCertificate.getX5u();

		Base64 encodedCert = null;
		if (serviceCertificate.getX5c().get(0) != null)
			encodedCert = Base64.from(serviceCertificate.getX5c().get(0));
		else if (StringUtils.isNotBlank(certificateUri))
			encodedCert = clientService.getCertificateFromUri(certificateUri);
		else
			throw new ServiceManagerException("The Service with id: " + serviceId + " has no valid Certificate");

		// Verify Service Signature
		if (!cryptoService.verifyServiceSignature(serviceSignResponse.getServiceSignedSlr(), encodedCert))
			throw new ServiceManagerException("Verification of SLR payload (Service signature) failed");

		/*
		 * generateServiceLinkStatusRecordId
		 */
		String ssrId = new ObjectId().toString();

		ServiceLinkStatusRecordPayload ssrPayload = serviceManager.generateSsrPayload(
				appProperty.getCape().getVersion(), ssrId, surrogateId, slrId, now, ServiceLinkStatusEnum.ACTIVE,
				"none");

		/*
		 * Call Account Manager to sign and store SSR and final double signed SLR
		 */
		ResponseEntity<FinalStoreSlrResponse> storeSlrResponse = clientService.callStoreFinalSlr(accountId, sessionCode,
				slrId, serviceSignResponse.getServiceSignedSlr(), ssrPayload);

		/*
		 * Update The Linking Session to the next state: DOUBLE_SIGNED_SLR ->
		 * FINAL_SLR_STORED
		 */
		serviceManager.changeSessionState(session, LinkingSessionStateEnum.FINAL_SLR_STORED);

		// Call to Audit
		clientService.callAddEventLog(new ServiceLinkEventLog(now, EventType.SERVICE_LINK, accountId,
				LegalBasis.CONSENT, "Service Link Created", slrId, serviceId, serviceName, serviceUri,
				ServiceLinkActionType.CREATE));

		/*
		 * Update The Linking Session to the next state: FINAL_SLR_STORED -> COMPLETED
		 */
		serviceManager.changeSessionState(session, LinkingSessionStateEnum.COMPLETED);

		// Return SLR (to service SDK)
		return storeSlrResponse;

	}

	@Operation(summary = "Get Linking Session by Code", description = "Used internally by Account and Service Manager to get LinkingSession corresponding to a generated sessionCode.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns corresponding LinkingSession", responseCode = "200"),
					@ApiResponse(description = "No Linking Session found for input sessionCode", responseCode = "404") })
	@Override
	@GetMapping(value = "/slr/linkingSession/{sessionCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkingSession> getLinkingSessionByCode(@PathVariable String sessionCode)
			throws SessionNotFoundException {

		LinkingSession session = serviceManager.getSessionByCode(sessionCode);
		return ResponseEntity.ok(session);
	}

	@Operation(summary = "Change Linking Session state by Code", description = "Used internally by Account and Service Manager to change LinkingSession state corresponding to a generated sessionCode.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns LinkingSession with updated state", responseCode = "200"),
					@ApiResponse(description = "No Liking Session found for input sessionCode", responseCode = "404") })
	@Override
	@PutMapping(value = "/slr/linkingSession/{sessionCode}/state/{newState}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkingSession> changeLinkingSessionStateByCode(@PathVariable String sessionCode,
			@PathVariable LinkingSessionStateEnum newState)
			throws SessionNotFoundException, SessionStateNotAllowedException {

		LinkingSession updatedSession = serviceManager.changeSessionStateByCode(sessionCode, newState);
		return ResponseEntity.ok(updatedSession);
	}

	@Operation(summary = "Cancel Linking Session by Session Code", description = "Used internally by Account, Service Manager and Service SDK to cancel Linking Session (e.g. whatever an error occurred in the Service Linking flow).", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Session correctly cancelled", responseCode = "204"),
					@ApiResponse(description = "No Liking Session found for input sessionCode", responseCode = "404") })
	@Override
	@DeleteMapping(value = "/slr/linkingSession/{sessionCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> cancelLinkingSessionByCode(@PathVariable String sessionCode)
			throws SessionNotFoundException {

		serviceManager.cancelSessionBySessionCode(sessionCode);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Cancel Linking Sessions by AccountId", description = "Used internally by Account Manager to delete all Linking Session of an Account.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Session correctly cancelled", responseCode = "204"),
					@ApiResponse(description = "No Liking Session found for input accountId", responseCode = "404") })
	@Override
	@DeleteMapping(value = "/slr/accounts/{accountId}/linkingSessions", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> deleteLinkingSessionsByAccountId(@PathVariable String accountId) {

		serviceManager.deleteSessionByAccountId(accountId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Disables Active Service Link from Operator.", description = "Disables Active Service Link by issuing a new signed Service Link Status Record with Removed state.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 CREATED and the created SLR", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
	@Override
	@DeleteMapping(value = "/slr/account/{accountOrSurrogateId}/services/{serviceId}/slr/{slrId}")
	public ResponseEntity<ServiceLinkStatusRecordSigned> disableServiceLink(@PathVariable String accountOrSurrogateId,
			@PathVariable String serviceId, @PathVariable String slrId,
			@RequestParam ChangeSlrStatusRequestFrom requestFrom)
			throws ServiceManagerException, ServiceLinkRecordNotFoundException, ServiceDescriptionNotFoundException,
			OperatorDescriptionNotFoundException, JsonProcessingException, JOSEException,
			ServiceLinkStatusConflictingException, AccountNotFoundException {

		return changeSlrStatus(accountOrSurrogateId, serviceId, slrId, ServiceLinkActionType.DISABLE, requestFrom);
	}

	@Operation(summary = "Disables Active Service Link from Operator.", description = "Disables Active Service Link by issuing a new signed Service Link Status Record with Removed state.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 CREATED and the created SLR", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
	@Override
	@PutMapping(value = "/slr/account/{accountOrSurrogateId}/services/{serviceId}/slr/{slrId}")
	public ResponseEntity<ServiceLinkStatusRecordSigned> enableServiceLink(@PathVariable String accountOrSurrogateId,
			@PathVariable String serviceId, @PathVariable String slrId,
			@RequestParam ChangeSlrStatusRequestFrom requestFrom)
			throws ServiceManagerException, ServiceLinkRecordNotFoundException, ServiceDescriptionNotFoundException,
			OperatorDescriptionNotFoundException, JsonProcessingException, JOSEException,
			ServiceLinkStatusConflictingException, AccountNotFoundException {

		return changeSlrStatus(accountOrSurrogateId, serviceId, slrId, ServiceLinkActionType.ENABLE, requestFrom);
	}

	private ResponseEntity<ServiceLinkStatusRecordSigned> changeSlrStatus(String accountOrSurrogateId, String serviceId,
			String slrId, ServiceLinkActionType actionType, ChangeSlrStatusRequestFrom requestFrom)
			throws ServiceManagerException, ServiceLinkRecordNotFoundException, OperatorDescriptionNotFoundException,
			JsonProcessingException, JOSEException, ServiceDescriptionNotFoundException,
			ServiceLinkStatusConflictingException, AccountNotFoundException {

		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
		ServiceLinkRecordDoubleSigned existingSlr = null;

		/*
		 * If the request started from Operator, get SLR by accountId, else if started
		 * from Service, get SLR by surrogateId
		 */
		existingSlr = clientService.callGetServiceLinkRecord(accountOrSurrogateId, serviceId, slrId, requestFrom)
				.getBody();

		if (!existingSlr.getPayload().getServiceId().equals(serviceId))
			throw new ServiceManagerException(
					"The serviceId in input does not match with the one in the retrieved Service Link Record");
		/*
		 * Create new Service Link Status Record Payload chaining with Previous Record
		 * Id generateServiceLinkStatusRecordId
		 */

		ServiceLinkStatusRecordSigned lastCurrentSsr = existingSlr.getServiceLinkStatuses()
				.get(existingSlr.getServiceLinkStatuses().size() - 1);

		ServiceLinkStatusEnum newStatus = actionType.equals(ServiceLinkActionType.ENABLE) ? ServiceLinkStatusEnum.ACTIVE
				: ServiceLinkStatusEnum.REMOVED;

		if (lastCurrentSsr.getPayload().getServiceLinkStatus().equals(newStatus))
			throw new ServiceLinkStatusConflictingException(
					"The Service Link Status is already in " + newStatus + " state");

		String previousRecordId = lastCurrentSsr.getPayload().get_id();
		String ssrId = new ObjectId().toString();

		ServiceLinkStatusRecordPayload ssrPayload = serviceManager.generateSsrPayload(
				appProperty.getCape().getVersion(), ssrId, existingSlr.getPayload().getSurrogateId(), slrId, now,
				newStatus, previousRecordId);

		ServiceLinkStatusRecordSigned signedSsr = null;
		URI newSsrLocation = null;

		/*
		 * Get Consent Records for this Service Link either by accountId or by
		 * surrogateId
		 */
		ConsentRecordSignedPair[] crPairs = null;

		/*
		 * If Request generated from Operator (Cape) -> Call Account Manager to sign
		 * with Account private key and store the newly generated SSR
		 */
		if (requestFrom.equals(ChangeSlrStatusRequestFrom.OPERATOR)) {
			ResponseEntity<ServiceLinkStatusRecordSigned> signAndStoreSsrResponse = clientService
					.callSignAndStoreServiceLinkStatus(accountOrSurrogateId, slrId, ssrPayload);

			signedSsr = signAndStoreSsrResponse.getBody();
			newSsrLocation = signAndStoreSsrResponse.getHeaders().getLocation();
			crPairs = clientService.callGetConsentPairsByAccountIdAndSlrId(accountOrSurrogateId, slrId).getBody();

		} else if (requestFrom.equals(ChangeSlrStatusRequestFrom.SERVICE)) {
			/*
			 * Else if the request generated from Service -> Call Account Manager to store
			 * the newly generated SSR, signed with the Operator's key
			 */
			/*
			 * Get Operator Key Pair and sign SSR with private key
			 */

			RSAKey operatorKeyPair = cryptoService.getKeyPairByOperatorId(this.operatorId);
			signedSsr = cryptoService.signSSR(operatorKeyPair, ssrPayload);

			/*
			 * Retrieve the transient accountId from Slr, filled by the Account Manager only
			 * for this transition
			 */

			ResponseEntity<ServiceLinkStatusRecordSigned> storeSsrResponse = clientService
					.callStoreSignedServiceLinkStatus(accountOrSurrogateId, slrId, signedSsr);
			newSsrLocation = storeSsrResponse.getHeaders().getLocation();

			crPairs = clientService.callGetConsentPairsBySurrogateIdAndSlrId(accountOrSurrogateId, slrId).getBody();
		}

		/*
		 * ***********************************************************************
		 * Notify to the Service the status change of all Consent Records related to
		 * this Service Link Record (and also for the other CR if we have a Consent pair
		 * -> Third party re-use case
		 **************************************************************************/

		Arrays.asList(crPairs).forEach(pair -> {

			ConsentRecordStatusEnum newCrStatus;

			/*
			 * For each Active Consent Record pair (both for Sink and Source), change their
			 * Consent Status Record to Disabled.
			 */

			if (newStatus.equals(ServiceLinkStatusEnum.REMOVED)) {

				ConsentRecordStatusEnum lastStatus = pair.getSink().getConsentStatusList()
						.get(pair.getSink().getConsentStatusList().size() - 1).getPayload().getConsentStatus();

				if (lastStatus.equals(ConsentRecordStatusEnum.Active))
					newCrStatus = ConsentRecordStatusEnum.Disabled;
				else
					return;
			} else {

				/*
				 * In case of activating SLR, re-activate only Consent Records disabled
				 * previously by this method. Namely: check if the CSR iat date is after the
				 * last SSR iat date
				 */
				ZonedDateTime lastCurrentSsIat = lastCurrentSsr.getPayload().getIat();

				ConsentStatusRecordPayload lastStatus = pair.getSink().getConsentStatusList()
						.get(pair.getSink().getConsentStatusList().size() - 1).getPayload();
				ZonedDateTime csrIat = lastStatus.getIat();

				// If the CR was disabled
				if (csrIat.isAfter(lastCurrentSsIat))
					newCrStatus = ConsentRecordStatusEnum.Active;
				else
					return;
			}

			ConsentRecordPayload sinkCrPayload = pair.getSink().getPayload();

			try {
				ChangeConsentStatusRequestFrom changeConsentStatusRequestFrom = requestFrom
						.equals(ChangeSlrStatusRequestFrom.SERVICE) ? ChangeConsentStatusRequestFrom.SERVICE
								: ChangeConsentStatusRequestFrom.OPERATOR;

				/*
				 * Call the Consent Manager to change the Sink Consent Status, it will change
				 * also the Source. No need to call the Consent Status change for the Source
				 * here
				 */
				clientService.callChangeConsentStatus(accountOrSurrogateId, sinkCrPayload.getCommonPart().getSlrId(),
						sinkCrPayload.getCommonPart().getCrId(), sinkCrPayload, newCrStatus,
						changeConsentStatusRequestFrom);

				newCrStatus = null;
			} catch (ServiceManagerException e) {
				e.printStackTrace();
			}

		});

		/*
		 * Get Service Description to retrieve its SDK Url
		 */
		ServiceEntry serviceDescription = clientService.getServiceDescriptionFromRegistry(serviceId);
		String serviceSdkHost = serviceDescription.getServiceInstance().getServiceUrls().getLibraryDomain();

		/*
		 * If request has started from Operator, notify to the Service the status change
		 * of Service Link Record, sending the newly signed Service Link Status Record
		 */
		if (requestFrom.equals(ChangeSlrStatusRequestFrom.OPERATOR))
			clientService.callNotifyServiceLinkStatusChanged(serviceSdkHost, slrId, signedSsr);

		/*
		 * Call to Audit
		 */

		clientService.callAddEventLog(
				new ServiceLinkEventLog(now, EventType.SERVICE_LINK, accountOrSurrogateId, LegalBasis.CONSENT,
						"Service Link Status Updated", slrId, serviceId, existingSlr.getPayload().getServiceName(),
						existingSlr.getPayload().getServiceUri(), actionType, requestFrom));

		return ResponseEntity.created(newSsrLocation).body(signedSsr);
	}

}
