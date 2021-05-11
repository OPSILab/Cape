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

import java.net.URI;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

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
import it.eng.opsi.cape.sdk.ApplicationProperties;
import it.eng.opsi.cape.sdk.model.ServicePopKey;
import it.eng.opsi.cape.sdk.model.ServiceSignKey;
import it.eng.opsi.cape.sdk.model.OperatorDescription;
import it.eng.opsi.cape.sdk.model.SurrogateIdResponse;
import it.eng.opsi.cape.sdk.model.account.Account;
import it.eng.opsi.cape.sdk.model.consenting.ChangeConsentStatusRequest;
import it.eng.opsi.cape.sdk.model.consenting.CommonPart;
import it.eng.opsi.cape.sdk.model.consenting.ConsentForm;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordPayload;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSignedPair;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordStatusEnum;
import it.eng.opsi.cape.sdk.model.consenting.ConsentStatusRecordPayload;
import it.eng.opsi.cape.sdk.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.sdk.model.consenting.Dataset;
import it.eng.opsi.cape.sdk.model.consenting.RSDescription;
import it.eng.opsi.cape.sdk.model.datatransfer.AuthorisationTokenPayload;
import it.eng.opsi.cape.sdk.model.datatransfer.AuthorisationTokenResponse;
import it.eng.opsi.cape.sdk.model.datatransfer.DataRequestAuthorizationPayload;
import it.eng.opsi.cape.sdk.model.datatransfer.DataTransferRequest;
import it.eng.opsi.cape.sdk.model.datatransfer.DataTransferResponse;
import it.eng.opsi.cape.sdk.model.linking.FinalLinkingResponse;
import it.eng.opsi.cape.sdk.model.linking.LinkingSession;
import it.eng.opsi.cape.sdk.model.linking.LinkingSessionStateEnum;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordAccountSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkStatusEnum;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkStatusRecordPayload;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceSignSlrRequest;
import it.eng.opsi.cape.sdk.model.linking.ServiceSignSlrResponse;
import it.eng.opsi.cape.sdk.model.linking.StartLinkingRequest;
import it.eng.opsi.cape.sdk.model.linking.UserSurrogateIdLink;
import it.eng.opsi.cape.sdk.repository.AuthorisationTokenRepository;
import it.eng.opsi.cape.sdk.repository.ConsentRecordRepository;
import it.eng.opsi.cape.sdk.repository.ServiceLinkRecordDoubleSignedRepository;
import it.eng.opsi.cape.sdk.repository.UserSurrogateIdLinkRepository;
import it.eng.opsi.cape.sdk.service.CapeServiceSdkManager;
import it.eng.opsi.cape.sdk.service.ClientService;
import it.eng.opsi.cape.sdk.service.CryptoService;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;
import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@OpenAPIDefinition(security = { @SecurityRequirement(name = "bearer-key") }, tags = {
		@Tag(name = "Service Linking", description = "Cape SDK APIs to handle Service Linking internal operations."),
		@Tag(name = "Service Management", description = "Cape SDK APIs to handle Services managed by this SDK"),
		@Tag(name = "Service Link Record", description = "Cape SDK APIs to manage Service Link Records."),
		@Tag(name = "Consenting", description = "Consent Manager APIs to perform CaPe Consenting operations.") }, info = @Info(title = "SDK Service API", description = "SDK Service API for integration with cape", version = "2.0"))
@RestController
@RequestMapping("/api/v2")
@Slf4j
public class CapeServiceSdkController implements ICapeServiceSdkController {

	@Autowired
	private CapeServiceSdkManager sdkManager;

	@Autowired
	private ServiceLinkRecordDoubleSignedRepository slrRepo;

	@Autowired
	private UserSurrogateIdLinkRepository userSurrogateIdRepo;

	@Autowired
	ConsentRecordRepository consentRecordRepo;

	@Autowired
	AuthorisationTokenRepository authTokenRepo;

	@Autowired
	ServiceLinkRecordDoubleSignedRepository serviceLinkRecordRepo;

	@Autowired
	private ClientService clientService;

	private final ApplicationProperties appProperty;

	private final String businessId;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	public CapeServiceSdkController(ApplicationProperties appProperty) {
		this.appProperty = appProperty;
		this.businessId = this.appProperty.getCape().getServiceSdk().getBusinessId();
	}

	@Operation(summary = "Get Operator Description for CaPe by Operator Id", tags = {
			"Operator Description" }, responses = {
					@ApiResponse(description = "Returns the requested Operator Description.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OperatorDescription.class))) })
	@Override
	@GetMapping(value = "/operatorDescriptions/{operatorId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OperatorDescription> getOperatorDescription(@PathVariable("operatorId") String operatorId)
			throws OperatorDescriptionNotFoundException, ServiceManagerException {

		/*
		 * Check if operator exists TODO user its linkingRedirectUri to return new SLR
		 * to the appopriate URL (asynch push service?)
		 */

		OperatorDescription operatorDescription = clientService.fetchOperatorDescription(operatorId);

		return ResponseEntity.ok().body(operatorDescription);
	}

	@Operation(summary = "Get Linking sessionCode from Service Manager for automatic linking starting", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns the requested linking sessionCode.", responseCode = "200", content = @Content(mediaType = "application/json")) })
	@Override
	@GetMapping(value = "/slr/linking/sessionCode", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getLinkSessionCode(@RequestParam("serviceId") String serviceId,
			@RequestParam("userId") String userId, @RequestParam("surrogateId") String surrogateId,
			@RequestParam("returnUrl") String returnUrl, @RequestParam("forceLinking") Boolean forceLinking)
			throws ServiceManagerException, SessionNotFoundException {

		String sessionCode = clientService.callGetLinkingCode(serviceId, userId, surrogateId, returnUrl, forceLinking);

		return ResponseEntity.ok(sessionCode);
	}

	/**
	 * 
	 * Initiate Service Linking (Operator/Service -> ServiceSDK)
	 * 
	 * @throws ServiceDescriptionNotFoundException
	 * @throws SessionNotFoundException
	 */

	@Operation(summary = "Link Service Account to CaPe Account", description = "Initiate linking process", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 CREATED and the created Service Link Record and Service Link Status Record", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FinalLinkingResponse.class))) })
	@Override
	@PostMapping(value = "/slr/linking", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FinalLinkingResponse> startServiceLinking(@RequestBody @Valid StartLinkingRequest request)
			throws ServiceManagerException, OperatorDescriptionNotFoundException, JOSEException,
			ServiceDescriptionNotFoundException, SessionNotFoundException {

		ServicePopKey popKey = null;
		String sessionCode = request.getSessionCode();
		String operatorId = request.getOperatorId();
		String serviceId = request.getServiceId();
		String surrogateId = request.getSurrogateId();

		/*
		 * Check if operator exists TODO user its linkingRedirectUri to return new SLR
		 * to the appopriate URL (asynch push service?)
		 */

		OperatorDescription operatorDescription = clientService.fetchOperatorDescription(operatorId);

		/*
		 * Get session by input sessionCode and check its serviceId matches the ones in
		 * input
		 */
		LinkingSession session = clientService.callGetLinkingSession(request.getSessionCode());

		if (!session.getServiceId().equals(serviceId))
			throw new SessionNotFoundException("The Session serviceId does not match with the one in input");

		FinalLinkingResponse operatorLinkingResponse;
		ServiceEntry serviceDescription = clientService.getServiceDescriptionFromRegistry(serviceId, true);

		// If the Service type is SINK (taken from Service Description)
		if (serviceDescription.getRole().equals(ServiceEntry.Role.SINK)) {
			popKey = sdkManager.getPopKey(operatorId, serviceId, request.getSurrogateId());
			operatorLinkingResponse = clientService.callLinkSinkService(sessionCode, serviceId, surrogateId, popKey);
			// else if Source
		} else {
			operatorLinkingResponse = clientService.callLinkSourceService(sessionCode, serviceId, surrogateId);
		}

		ServiceLinkRecordDoubleSigned slr = operatorLinkingResponse.getData().getSlr();
		slr.setServiceLinkStatuses(Arrays.asList(operatorLinkingResponse.getData().getSsr()));
		slrRepo.insert(slr);

		return ResponseEntity.status(HttpStatus.CREATED).body(operatorLinkingResponse);
	}

	@Operation(summary = "Generate surrogate_id for given CaPe Account and Service Account.", description = "Generate surrogate_id", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 CREATED and the created surrogate_id ", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurrogateIdResponse.class))) })
	@Override
	@GetMapping(value = "/slr/surrogate_id")
	public ResponseEntity<SurrogateIdResponse> generateSurrogateId(@RequestParam(required = true) String operatorId,
			@RequestParam(required = true) String userId) {

		String surrogateId = sdkManager.generateSurrogateId(operatorId, userId);

		return ResponseEntity.status(HttpStatus.CREATED).body(new SurrogateIdResponse(surrogateId));

	}

	@Operation(summary = "Register a Service existing in the Service Registry as managed by CaPe.", description = "Register the Service as managed by this SDK and create Service key pair and x509 Certificate", tags = {
			"Service Management" }, responses = {
					@ApiResponse(description = "Returns 201 CREATED and the Service Description containing generated Public X509 Certificate.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceEntry.class))) })
	@Override
	@PostMapping(value = "/services/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceEntry> registerServiceToCape(@PathVariable String serviceId) throws JOSEException,
			ServiceManagerException, JsonProcessingException, ServiceDescriptionNotFoundException {

		ServiceEntry certificateFilledAndSignedDescription = sdkManager.registerServiceToCape(serviceId);

		return ResponseEntity.status(HttpStatus.CREATED).body(certificateFilledAndSignedDescription);
	}

	@Operation(summary = "Unregister a Service managed by CaPe. Optionally delete also Service Description from Service Registry", description = "Delete Service Sign key pair (and PoP key if Sink) and cert from Service Description at Registry", tags = {
			"Service Management" }, responses = {
					@ApiResponse(description = "Returns 204 No Content", responseCode = "204"),
					@ApiResponse(description = "Returns 404, no Service or Sign Key was found", responseCode = "404") })
	@Override
	@DeleteMapping(value = "/services/{serviceId}")
	public ResponseEntity<Object> unregisterService(@PathVariable String serviceId,
			@RequestParam(defaultValue = "false") Boolean deleteServiceDescription)
			throws JOSEException, ServiceManagerException, ServiceSignKeyNotFoundException {

		sdkManager.unregisterService(serviceId, deleteServiceDescription);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Get sign keys of services registered to Cape by this SDK", description = "Get registered services sign keys managed by this SDK", tags = {
			"Service Management" }, responses = {
					@ApiResponse(description = "Returns 200 OK and the list of registered services", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceSignKey.class))) })
	@Override
	@GetMapping(value = "/services/signKeys")
	public ResponseEntity<List<ServiceSignKey>> getRegisteredServicesKeys() {

		return ResponseEntity.status(HttpStatus.OK).body(sdkManager.getRegisteredServicesKeys());

	}

	@Operation(summary = "Get services (optionally only the ones registered to CaPe) managed by this SDK.", description = "Get services managed by this SDK", tags = {
			"Service Management" }, responses = {
					@ApiResponse(description = "Returns 200 OK and the list of registered services", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceEntry.class))) })
	@Override
	@GetMapping(value = "/services")
	public ResponseEntity<List<ServiceEntry>> getServices(@RequestParam(defaultValue = "false") Boolean onlyRegistered)
			throws ServiceManagerException {

		return ResponseEntity.status(HttpStatus.OK).body(sdkManager.getServices(onlyRegistered, businessId));
	}

	@Operation(summary = "Get service by ServiceId (optionally only the ones registered to CaPe) managed by this SDK.", description = "Get service managed by this SDK", tags = {
			"Service Management" }, responses = {
					@ApiResponse(description = "Returns 200 OK and the list of registered services", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceEntry.class))) })
	@Override
	@GetMapping(value = "/services/{serviceId}")
	public ResponseEntity<ServiceEntry> getService(@PathVariable String serviceId,
			@RequestParam(defaultValue = "false") Boolean onlyRegistered)
			throws ServiceManagerException, ServiceDescriptionNotFoundException {

		return ResponseEntity.status(HttpStatus.OK).body(sdkManager.getService(serviceId, onlyRegistered));
	}

	@Operation(summary = "Sign the Service Link Record payload with Service key.", description = "Sign the Service Link Record payload with Service key after verifying Account signature", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 CREATED and the created Service Description containing generated Public X509 Certificate.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceEntry.class))) })
	@Override
	@PostMapping(value = "/slr/slr", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceSignSlrResponse> signServiceLinkRecordPayload(
			@RequestBody @Valid ServiceSignSlrRequest request)
			throws JsonProcessingException, JOSEException, ParseException, ServiceSignKeyNotFoundException,
			SessionNotFoundException, ServiceManagerException, SessionStateNotAllowedException {

		/*
		 * Get session by input sessionCode, check if is in an allowed State and if its
		 * accountId matches the ones in input
		 */
		LinkingSession session = clientService.callGetLinkingSession(request.getSessionCode());
		if (!session.getState().equals(LinkingSessionStateEnum.ACCOUNT_SIGNED_SLR))
			throw new SessionStateNotAllowedException("The Linking Session should be in ACCOUNT_SIGNED_SLR state, "
					+ session.getState() + " found instead");

		ServiceLinkRecordAccountSigned accountSignedSlr = request.getAccountSignedSlr();
		String inputServiceId = accountSignedSlr.getPayload().getServiceId();

		if (!session.getServiceId().equals(inputServiceId))
			throw new SessionNotFoundException(
					"The Session Service Id does not match with the one in the Service Link Record payload");

		ServiceLinkRecordDoubleSigned doubleSignedSlr = sdkManager
				.verifyAndSignServiceLinkRecordPayload(accountSignedSlr);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ServiceSignSlrResponse(request.getSessionCode(), doubleSignedSlr));
	}

	@Operation(summary = "Return all the Service Link Records", tags = { "Service Link Record" }, responses = {
			@ApiResponse(description = "Returns Service Link Records.", responseCode = "200") })
	@Override
	@GetMapping(value = "/slr")
	public ResponseEntity<List<ServiceLinkRecordDoubleSigned>> getAllServiceLinkRecords() {

		List<ServiceLinkRecordDoubleSigned> result = slrRepo.findAll();

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Return Service Link Records by Service Id", tags = { "Service Link Record" }, responses = {
			@ApiResponse(description = "Returns Service Link Records.", responseCode = "200") })
	@Override
	@GetMapping(value = "/services/{serviceId}/slr")
	public ResponseEntity<List<ServiceLinkRecordDoubleSigned>> getServiceLinkRecordsByServiceId(
			@PathVariable String serviceId) {

		List<ServiceLinkRecordDoubleSigned> result = slrRepo.findByPayload_ServiceId(serviceId);

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Return Service Link Records by Slr Id", tags = { "Service Link Record" }, responses = {
			@ApiResponse(description = "Returns the Service Link Record.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkRecordDoubleSigned.class))) })
	@Override
	@GetMapping(value = "/slr/{slrId}")
	public ResponseEntity<ServiceLinkRecordDoubleSigned> getServiceLinkRecordBySlrId(@PathVariable String slrId)
			throws ServiceLinkRecordNotFoundException {

		ServiceLinkRecordDoubleSigned result = slrRepo.findByPayload_SlrId(slrId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException(
						"No Service Link Record with slrId: " + slrId + " was found"));

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Return Service Link Records by Surrogate Id", tags = { "Service Link Record" }, responses = {
			@ApiResponse(description = "Returns the Service Link Record.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkRecordDoubleSigned.class))) })
	@Override
	@GetMapping(value = "/slr/surrogate/{surrogateId}")
	public ResponseEntity<ServiceLinkRecordDoubleSigned> getServiceLinkRecordBySurrogateId(
			@PathVariable String surrogateId) throws ServiceLinkRecordNotFoundException {

		ServiceLinkRecordDoubleSigned result = slrRepo.findByPayload_SurrogateId(surrogateId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException(
						"No Service Link Record with surrogateId: " + surrogateId + " was found"));

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Return Service Link Records by Surrogate Id and Service Id", tags = {
			"Service Link Record" }, responses = {
					@ApiResponse(description = "Returns the Service Link Record.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkRecordDoubleSigned.class))) })
	@Override
	@GetMapping(value = "/slr/surrogate/{surrogateId}/services/{serviceId}")
	public ResponseEntity<ServiceLinkRecordDoubleSigned> getServiceLinkRecordBySurrogateIdAndServiceId(
			@PathVariable String surrogateId, @PathVariable String serviceId)
			throws ServiceLinkRecordNotFoundException {

		ServiceLinkRecordDoubleSigned result = slrRepo
				.findByPayload_SurrogateIdAndPayload_ServiceId(surrogateId, serviceId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException("No Service Link Record with surrogateId: "
						+ surrogateId + " and serviceId: " + serviceId + " was found"));

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Return the Last Service Link Record Status by Slr Id.", tags = {
			"Service Link Record" }, responses = {
					@ApiResponse(description = "Returns the Last Service Link Record Status.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
	@Override
	@GetMapping(value = "/slr/{slrId}/statuses/last")
	public ResponseEntity<ServiceLinkStatusRecordSigned> getLastServiceLinkStatusRecord(@PathVariable String slrId)
			throws ServiceLinkRecordNotFoundException {

		ServiceLinkStatusRecordSigned result = slrRepo.getLastServiceLinkStatusRecordBySlrId(slrId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException(
						"No Service Link Record with slrId: " + slrId + " was found"));

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Insert a new link between userId, surrogateId, serviceId and operatorId.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 CREATED and the created UserSurrogateIdLink.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserSurrogateIdLink.class))) })
	@Override
	@PostMapping(value = "/userSurrogateIdLink", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserSurrogateIdLink> linkUserToSurrogateId(@RequestBody @Valid UserSurrogateIdLink entity) {

		entity.setCreated(ZonedDateTime.now(ZoneId.of("UTC")));
		UserSurrogateIdLink result = userSurrogateIdRepo.insert(entity);

		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@Operation(summary = "Return the Last Service Link Record Status by Slr Id.", tags = {
			"Service Link Record" }, responses = {
					@ApiResponse(description = "Returns the Last Service Link Record Status.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
	@Override
	@GetMapping(value = "/userSurrogateIdLink")
	public ResponseEntity<UserSurrogateIdLink> getUserSurrogateLinkByUserIdAndServiceIdAndOperatorId(
			@RequestParam String userId, @RequestParam String serviceId, @RequestParam String operatorId)
			throws UserSurrogateIdLinkNotFoundException {

		UserSurrogateIdLink result = userSurrogateIdRepo
				.findTopByUserIdAndServiceIdAndOperatorIdOrderByCreatedDesc(userId, serviceId, operatorId).orElseThrow(
						() -> new UserSurrogateIdLinkNotFoundException("No user - surrogate link found for userId: "
								+ userId + " , serviceId: " + serviceId + " and operatorId: " + operatorId));

		return ResponseEntity.ok(result);
	}

	/**
	 * Return changed Service Link Status Record, due to a status change request
	 * started from Operator
	 * 
	 * @throws ParseException
	 * @throws JOSEException
	 * @throws JsonProcessingException
	 * @throws ServiceLinkStatusRecordNotValid
	 **/
	@Operation(summary = "Notify to the Service a status change in a Service Link Record.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 Created.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
	@Override
	@PostMapping(value = "/slr/{slrId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> notifyServiceLinkStatusChanged(@PathVariable String slrId,
			@RequestBody @Valid ServiceLinkStatusRecordSigned ssr) throws ServiceLinkRecordNotFoundException,
			JsonProcessingException, JOSEException, ParseException, ServiceLinkStatusRecordNotValid {

		/*
		 * Verify ServiceLink Status Record signature with the Account public key
		 */
		if (!sdkManager.verifyServiceLinkStatusRecord(ssr))
			throw new ServiceLinkStatusRecordNotValid("The input ServiceLink Status Record signature is not valid");

		if (slrId.equals(ssr.getPayload().getServiceLinkRecordId())) {

			/*
			 * Get existing Service Link Record associated to the input Service Link Status
			 * Record
			 */
			ServiceLinkRecordDoubleSigned existingSlr = slrRepo.findByPayload_SlrId(slrId).orElseThrow(
					() -> new ServiceLinkRecordNotFoundException("No Service Link Record found with slrId: " + slrId));

			slrRepo.addStatusToSlr(slrId, ssr);
		} else
			throw new ServiceLinkRecordNotFoundException(
					"The slrId in input does not match with the one contained in the SSR");

		return ResponseEntity.ok().body(null);
	}

	/**
	 * Enable an existing disabled Service Link, due to a status change request
	 * started from Service
	 * 
	 * @throws ServiceLinkRecordNotFoundException
	 **/
	@Operation(summary = "Notify to the Service a status change in a Service Link Record.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 Created.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
	@Override
	@PutMapping(value = "/slr/{slrId}/surrogate/{surrogateId}/services/{serviceId}")
	public ResponseEntity<ServiceLinkStatusRecordSigned> enableServiceLink(@PathVariable String slrId,
			@PathVariable String surrogateId, @PathVariable String serviceId)
			throws ServiceLinkRecordNotFoundException {

		ServiceLinkStatusRecordSigned createdSsr = clientService.callEnableServiceLink(surrogateId, serviceId, slrId);

		if (slrId.equals(createdSsr.getPayload().getServiceLinkRecordId()))
			slrRepo.addStatusToSlr(slrId, createdSsr);
		else
			throw new ServiceLinkRecordNotFoundException(
					"The slrId in input does not match with the one contained in the SSR");

		return ResponseEntity.ok(createdSsr);
	}

	/**
	 * Disable an existing disabled Service Link, due to a status change request
	 * started from Service
	 * 
	 * @throws ServiceLinkRecordNotFoundException
	 **/
	@Operation(summary = "Notify to the Service a status change in a Service Link Record.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 Created.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
	@Override
	@DeleteMapping(value = "/slr/{slrId}/surrogate/{surrogateId}/services/{serviceId}")

	public ResponseEntity<ServiceLinkStatusRecordSigned> disableServiceLink(@PathVariable String slrId,
			@PathVariable String surrogateId, @PathVariable String serviceId)
			throws ServiceLinkRecordNotFoundException {

		ServiceLinkStatusRecordSigned createdSsr = clientService.callDisableServiceLink(surrogateId, serviceId, slrId);

		if (slrId.equals(createdSsr.getPayload().getServiceLinkRecordId()))
			slrRepo.addStatusToSlr(slrId, createdSsr);
		else
			throw new ServiceLinkRecordNotFoundException(
					"The slrId in input does not match with the one contained in the SSR");

		return ResponseEntity.ok(createdSsr);
	}

	/**
	 * Fetch Consent Form from Consent Manager
	 */
	@Operation(summary = "Call Consent Manager to generate Consent Form for surrogateId, sinkId and sourceId (if any) in input.", tags = {
			"Consenting" }, responses = {
					@ApiResponse(description = "Returns the generated Consent Form containing the Resource Set generated either by matching Sink and Source datasets' mappings (3-party reuse) or from the datasets of the service (Within Service)", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsentForm.class))) })
	@GetMapping(value = "/users/surrogates/{surrogateId}/service/{serviceId}/purpose/{purposeId}/consentForm", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ConsentForm> fetchConsentForm(@PathVariable String surrogateId,
			@PathVariable String serviceId, @PathVariable String purposeId,
			@RequestParam(required = false, name = "sourceDatasetId") String sourceDatasetId,
			@RequestParam(required = false, name = "sourceServiceId") String sourceServiceId)
			throws ServiceManagerException, CapeSdkManagerException {

		return clientService.callFetchConsentForm(surrogateId, serviceId, purposeId, sourceDatasetId, sourceServiceId);

	}

	/**
	 * Verify and store a new Consent Record issued by the Consent Manager
	 * 
	 * @throws ServiceLinkRecordNotFoundException
	 * @throws JOSEException
	 * @throws ParseException
	 * @throws JsonProcessingException
	 * @throws ConsentRecordNotValid
	 * @throws ConsentStatusRecordNotValid
	 **/
	@Operation(summary = "Verify and store a new Consent Record issued by the Operator.", tags = {
			"Consenting" }, responses = {
					@ApiResponse(description = "Returns 201 Created.", responseCode = "201", content = @Content(mediaType = "text/plain")) })
	@Override
	@PostMapping(value = "/cr/cr_management", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> storeNewConsentRecord(@RequestBody @Valid ConsentRecordSigned consentRecord)
			throws ServiceLinkRecordNotFoundException, JsonProcessingException, ParseException, JOSEException,
			ConsentRecordNotValid, ConsentStatusRecordNotValid {

		/*
		 * Get Service Link Record associated to the input Consent Record
		 */
		String slrId = consentRecord.getPayload().getCommonPart().getSlrId();
		ServiceLinkRecordDoubleSigned associatedSlr = serviceLinkRecordRepo.findByPayload_SlrId(slrId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException(
						"No Service Link Record with slrId: " + slrId + " was found"));

		/*
		 * Check if associated SLR is Active
		 */
		ServiceLinkStatusRecordPayload associatedSlrStatus = associatedSlr.getServiceLinkStatuses()
				.get(associatedSlr.getServiceLinkStatuses().size() - 1).getPayload();

		if (!associatedSlrStatus.getServiceLinkStatus().equals(ServiceLinkStatusEnum.ACTIVE))
			throw new ServiceLinkRecordNotFoundException("No active Service Link Record found for SlrId: " + slrId);

		/*
		 * Verify input Consent Record and Consent Status Record signatures with Account
		 * public key contained in the associated SLR
		 */
		RSAKey accountPublicKey = associatedSlr.getPayload().getCrKeys().get(0);
		if (!sdkManager.verifyConsentRecordSigned(consentRecord, accountPublicKey))
			throw new ConsentRecordNotValid("The signature of the input Consent Record is not valid");

		ConsentStatusRecordSigned consentStatusRecord = consentRecord.getConsentStatusList()
				.get(consentRecord.getConsentStatusList().size() - 1);
		if (!sdkManager.verifyConsentStatusRecordSigned(consentStatusRecord, accountPublicKey))
			throw new ConsentStatusRecordNotValid("The signature of the input Consent Status Record is not valid");

		/*
		 * Verify CSR chain is intact
		 */
		if (!sdkManager.verifyConsentStatusChain(consentRecord.getConsentStatusList().stream()
				.map(ConsentStatusRecordSigned::getPayload).toArray(ConsentStatusRecordPayload[]::new)))
			throw new ConsentStatusRecordNotValid(
					"The input Consent Record does not have a valid Consent Statuses chain");

		/*
		 * Store Signed CR and CSR
		 */
		consentRecord.set_id(new ObjectId(consentRecord.getPayload().getCommonPart().getCrId()));
		consentRecordRepo.insert(consentRecord);

		CommonPart crCommonPart = consentRecord.getPayload().getCommonPart();
		return ResponseEntity.created(UriComponentsBuilder
				.fromHttpUrl(
						appProperty.getCape().getServiceSdk().getHost() + "/api/v2/users/{surrogateId}/consents/{crId}")
				.build(crCommonPart.getSurrogateId(), crCommonPart.getCrId())).body(crCommonPart.getCrId());
	}

	/**
	 * Verify and store a new Consent Status Record issued by the Consent Manager
	 * 
	 * @throws ConsentStatusRecordNotValid
	 * @throws ConsentRecordNotFoundException
	 * @throws ServiceLinkRecordNotFoundException
	 * @throws JOSEException
	 * @throws ParseException
	 * @throws JsonProcessingException
	 * @throws ConsentRecordNotValid
	 **/
	@Operation(summary = "Verify and update the Consent Record (along with new Csr as last one in the csr list) issued by the Consent Manager.", tags = {
			"Consenting" }, responses = {
					@ApiResponse(description = "Returns 201 Created.", responseCode = "201", content = @Content(mediaType = "text/plain")) })
	@Override
	@PatchMapping(value = "/cr/cr_management", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> updateConsentRecordWithNewStatus(@RequestBody @Valid ConsentRecordSigned updatedCr)
			throws ConsentStatusRecordNotValid, ConsentRecordNotFoundException, ServiceLinkRecordNotFoundException,
			JsonProcessingException, ParseException, JOSEException, ConsentRecordNotValid {

		String inputCrId = updatedCr.getPayload().getCommonPart().getCrId();
		ConsentStatusRecordSigned updatedCsr = updatedCr.getConsentStatusList()
				.get(updatedCr.getConsentStatusList().size() - 1);
		/*
		 * Get existing Consent Record matching to the input Consent Record id
		 */
		ConsentRecordSigned existingCr = consentRecordRepo.findByPayload_commonPart_crId(inputCrId).orElseThrow(
				() -> new ConsentRecordNotFoundException("The Consent Record with id: " + inputCrId + "was not found"));
		List<ConsentStatusRecordSigned> existingCsrList = existingCr.getConsentStatusList();

		/*
		 * Get Service Link Record associated to the input Consent Record
		 */
		String slrId = existingCr.getPayload().getCommonPart().getSlrId();
		ServiceLinkRecordDoubleSigned associatedSlr = serviceLinkRecordRepo.findByPayload_SlrId(slrId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException(
						"No Service Link Record with slrId: " + slrId + " was found"));

		/*
		 * Verify input Consent Record signatures (Cr, Csr list and last Csr) with
		 * Account public key contained in the associated SLR
		 */
		RSAKey accountPublicKey = associatedSlr.getPayload().getCrKeys().get(0);
		if (!sdkManager.verifyConsentStatusRecordSigned(updatedCsr, accountPublicKey))
			throw new ConsentStatusRecordNotValid("The signature of the input Consent Status Record is not valid");

		if (!sdkManager.verifyConsentRecordSigned(updatedCr, accountPublicKey))
			throw new ConsentRecordNotValid("The signature of the input Consent Record is not valid");

		/*
		 * Add input CSR to the existing CSR list and Verify CSR chain is intact
		 */
		existingCsrList.add(updatedCsr);

		if (!sdkManager.verifyConsentStatusChain(existingCsrList.stream().map(ConsentStatusRecordSigned::getPayload)
				.toArray(ConsentStatusRecordPayload[]::new)))
			throw new ConsentStatusRecordNotValid(
					"The input Consent Record does not have a valid Consent Statuses chain");

		// Store the Cr updated with the new Csr
		updatedCr.set_id(new ObjectId(updatedCr.getPayload().getCommonPart().getCrId()));
		consentRecordRepo.save(updatedCr);

		return ResponseEntity.ok().body(null);
	}

	/**
	 * Give Consent from Consent Manager
	 */
	@Operation(summary = "Give the Consent for the input ConsentForm. Return Consent Record signed with the Acccount private key.", tags = {
			"Consenting" }, responses = {
					@ApiResponse(description = "Returns 201 Created with the created Consent Record Signed.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsentRecordSigned.class))) })
	@PostMapping(value = "/users/surrogates/{surrogateId}/consents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ConsentRecordSigned> giveConsent(@PathVariable String surrogateId,
			@RequestBody @Valid ConsentForm consentForm) {

		ResponseEntity<ConsentRecordSigned> giveConsentResponse = clientService.callGiveConsent(surrogateId,
				consentForm);
		ConsentRecordSigned responseCr = giveConsentResponse.getBody();

		String crId = responseCr.getPayload().getCommonPart().getCrId();

		/*
		 * If the Response Status from Consent Manager is 201 Created (is a new
		 * ConsentRecord)
		 */
		if (giveConsentResponse.getStatusCode().equals(HttpStatus.CREATED)) {
			/*
			 * 1. Store Signed CR and CSR
			 */
			responseCr.set_id(new ObjectId(responseCr.getPayload().getCommonPart().getCrId()));
			consentRecordRepo.insert(responseCr);

			/*
			 * 2. If both sourceId and sourceName are not blank, we are in 3rd party
			 * consenting case Get and save the (serialized) authorisation Token from
			 * Consent Manager
			 */
			if (!StringUtils.isAnyBlank(consentForm.getSourceId(), consentForm.getSourceName())) {
				AuthorisationTokenResponse tokenResponse = clientService.callGetAuthorisationToken(crId);
				authTokenRepo.save(tokenResponse);
			}

		}

		CommonPart crCommonPart = responseCr.getPayload().getCommonPart();
		return ResponseEntity.created(UriComponentsBuilder
				.fromHttpUrl(
						appProperty.getCape().getServiceSdk().getHost() + "/api/v2/users/{surrogateId}/consents/{crId}")
				.build(crCommonPart.getSurrogateId(), crCommonPart.getCrId())).body(responseCr);

	}

	@Operation(summary = "Get the list of signed Consent Records for the input Surrogate Id. Optionally can be filtered by ", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records belonging to the User linked with the input Surrogate Id", responseCode = "200") })
	@GetMapping(value = "/users/surrogates/{surrogateId}/consents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsBySurrogateIdAndQuery(
			@PathVariable String surrogateId, @RequestParam(required = false) String serviceId,
			@RequestParam(required = false) String sourceServiceId, @RequestParam(required = false) String datasetId,
			ConsentRecordStatusEnum status, @RequestParam(required = false) String purposeId,
			@RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) ProcessingCategory processingCategory,
			@RequestParam(defaultValue = "false") Boolean checkConsentAtOperator) {

		if (checkConsentAtOperator)
			return ResponseEntity.ok(Arrays.asList(clientService.callGetConsentRecordsByBusinessIdAndQuery(businessId,
					surrogateId, serviceId, sourceServiceId, datasetId, status, purposeCategory, processingCategory)));
		else

			return ResponseEntity.ok(consentRecordRepo.findBySurrogateIdAndQuery(surrogateId, serviceId,
					sourceServiceId, datasetId, status, purposeId, purposeName, purposeCategory, processingCategory));

	}

	@Operation(summary = "Get the list of signed Consent Records for the input User Id. Optionally can be filtered by Source Service Id, Dataset Id, Consent Status, Purpose Category and Processing Category.", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records belonging to the User linked with the input Surrogate Id", responseCode = "200") })
	@GetMapping(value = "/users/{userId}/consents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByUserIdAndQuery(@PathVariable String userId,
			@RequestParam(required = false) String serviceId, @RequestParam(required = false) String sourceServiceId,
			@RequestParam(required = false) String datasetId, ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) ProcessingCategory processingCategory,
			@RequestParam(defaultValue = "false") Boolean checkConsentAtOperator) {

		/*
		 * Retrieve SurrogateIds corresponding to the input UserId, in order to query
		 * ConsentRecord by SurrogateId. In case either the serviceId or the
		 * sourceServiceId is specified, there will be only one matching surrogateId
		 */
		List<UserSurrogateIdLink> matchingSurrogateIds = null;
		List<ConsentRecordSigned> result = new ArrayList<ConsentRecordSigned>(0);

		if (StringUtils.isBlank(serviceId))
			matchingSurrogateIds = userSurrogateIdRepo.findByUserIdOrderByCreatedDesc(userId);
		else {

			Optional<UserSurrogateIdLink> matchingLink = userSurrogateIdRepo
					.findTopByUserIdAndServiceIdOrderByCreatedDesc(userId, serviceId);

			if (matchingLink.isPresent())
				matchingSurrogateIds = Arrays.asList(matchingLink.get());
		}

		if (matchingSurrogateIds != null)
			for (UserSurrogateIdLink matchingSurrogateId : matchingSurrogateIds) {

				result.addAll(getConsentRecordsBySurrogateIdAndQuery(matchingSurrogateId.getSurrogateId(), serviceId,
						sourceServiceId, datasetId, status, purposeId, purposeName, purposeCategory, processingCategory,
						checkConsentAtOperator).getBody());
			}

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Get the list of signed Consent Records for the input Service Id. Optionally can be filtered by Source Service Id, Dataset Id, Consent Status, Purpose Category and Processing Category.", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records for the input ServiceId", responseCode = "200") })
	@GetMapping(value = "/services/{serviceId}/consents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByServiceIdAndQuery(
			@PathVariable String serviceId, @RequestParam(required = false) String sourceServiceId,
			@RequestParam(required = false) String datasetId,
			@RequestParam(required = false) ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) ProcessingCategory processingCategory,
			@RequestParam(defaultValue = "false") Boolean checkConsentAtOperator) {

		if (checkConsentAtOperator)

			return ResponseEntity.ok(Arrays.asList(clientService.callGetConsentRecordsByBusinessIdAndQuery(businessId,
					null, serviceId, sourceServiceId, datasetId, status, purposeCategory, processingCategory)));
		else
			return ResponseEntity.ok(consentRecordRepo.findByServiceIdAndQuery(serviceId, sourceServiceId, datasetId,
					status, purposeId, purposeName, purposeCategory, processingCategory));

	}

	@Operation(summary = "Get the list of all signed Consent Records for this Service Provider by using its assigned Business Id.", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records for all the Users for services provided by this Service Provieder (SDK instance).", responseCode = "200") })
	@GetMapping(value = "/consents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByBusinessIdAndQuery(
			@RequestParam(required = false) String surrogateId, @RequestParam(required = false) String serviceId,
			@RequestParam(required = false) String sourceServiceId, @RequestParam(required = false) String datasetId,
			@RequestParam(required = false) ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) ProcessingCategory processingCategory,
			@RequestParam(defaultValue = "false") Boolean checkConsentAtOperator) {

		if (checkConsentAtOperator)
			return ResponseEntity.ok(Arrays.asList(clientService.callGetConsentRecordsByBusinessIdAndQuery(businessId,
					surrogateId, serviceId, sourceServiceId, datasetId, status, purposeCategory, processingCategory)));
		else
			return ResponseEntity.ok(consentRecordRepo.findByBusinessIdAndQuery(businessId, surrogateId, serviceId,
					sourceServiceId, datasetId, status, purposeId, purposeName, purposeCategory, processingCategory));
	}

	@Operation(summary = "Call the Consent Manager to change status (from Service) of a Consent for the input SurrogateId, Slr Id and Cr Id.", tags = {
			"Consenting" }, responses = {
					@ApiResponse(description = "Returns 201 Created with the new Consent Status Record Signed.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsentStatusRecordSigned.class))) })
	@PostMapping(value = "/users/{surrogateId}/servicelinks/{slrId}/consents/{crId}/statuses", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ConsentRecordSigned> changeConsentStatusFromService(@PathVariable String surrogateId,
			@PathVariable String slrId, @PathVariable String crId, @RequestBody ChangeConsentStatusRequest request)
			throws ConsentRecordNotFoundException, ConsentStatusNotValidException, ServiceLinkRecordNotFoundException,
			ServiceLinkStatusNotValidException, ServiceDescriptionNotFoundException {

		return clientService.callChangeConsentStatus(surrogateId, slrId, crId, request);
	}

	@Operation(summary = "End point that initialises Data Transfer flow from Sink.", tags = {
			"Data Request" }, responses = {
					@ApiResponse(description = "Returns 200 OK with the Data requested matching input Rs id.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataTransferResponse.class))) })
	@PostMapping(value = "/dc", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<DataTransferResponse> startDataTransfer(@RequestBody @Valid DataTransferRequest dataRequest,
			@RequestParam Boolean checkConsentAtOperator, @RequestParam(value = "dataset_id") String datasetId)
			throws ConsentRecordNotFoundException, JsonMappingException, JsonProcessingException, ParseException,
			ConsentStatusNotValidException, CapeSdkManagerException, JOSEException, ServiceManagerException,
			ServiceDescriptionNotFoundException {

		/*
		 * Consent Record introspection (sink)
		 */
		ConsentRecordSigned consentRecord;
		consentRecord = checkConsentAtOperator ? clientService
				.callGetConsentRecordBySurrogateIdAndCrId(dataRequest.getSurrogateId(), dataRequest.getCrId()).getBody()
				: consentRecordRepo
						.findByPayload_commonPart_surrogateIdAndPayload_commonPart_crId(dataRequest.getSurrogateId(),
								dataRequest.getCrId())
						.orElseThrow(() -> new ConsentRecordNotFoundException(
								"The Consent Record with Cr Id: " + dataRequest.getCrId() + " was not found"));

		/*
		 * Check Consent Status (if Active)
		 */
		if (!consentRecord.getConsentStatusList().get(consentRecord.getConsentStatusList().size() - 1).getPayload()
				.getConsentStatus().equals(ConsentRecordStatusEnum.Active))
			throw new ConsentStatusNotValidException(
					"The Consent Record with Cr Id: " + dataRequest.getCrId() + " is not Active");

		/*
		 * Get the Authorisation Token locally, if any and it is not expired, otherwise
		 * call the Consent Manager to get a new one
		 */

		Optional<AuthorisationTokenResponse> optionalLocalToken = authTokenRepo.findById(dataRequest.getCrId());
		AuthorisationTokenResponse tokenResponse = null;
		AuthorisationTokenPayload authToken = null;
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

		if (optionalLocalToken.isPresent()) {
			authToken = sdkManager.extractTokenPayloadFromSerializedToken(optionalLocalToken.get().getAuthToken());
			if (now.isAfter(authToken.getExp())) {
				tokenResponse = clientService.callGetAuthorisationToken(dataRequest.getCrId());
				authTokenRepo.delete(optionalLocalToken.get());
				authTokenRepo.save(tokenResponse);
			} else {
				tokenResponse = optionalLocalToken.get();
			}
		} else {
			tokenResponse = clientService.callGetAuthorisationToken(dataRequest.getCrId());
			authTokenRepo.save(tokenResponse);
		}

		/*
		 * Construct the Data Request
		 */
		DataRequestAuthorizationPayload authPayload = new DataRequestAuthorizationPayload(tokenResponse.getAuthToken(),
				now.toInstant().toEpochMilli());
		dataRequest.setDatasetId(datasetId);

		DataTransferResponse dataTransferResponse = clientService
				.sendDataRequest(sdkManager.prepareDataRequest(authPayload, dataRequest));

		return ResponseEntity.ok(dataTransferResponse);
	}

	@Operation(summary = "Final End point to get Data Transfer at the Source.", tags = { "Data Request" }, responses = {
			@ApiResponse(description = "Returns 200 OK with the Data requested matching input Rs id.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataTransferResponse.class))) })
	@PostMapping(value = "/dc/send", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<DataTransferResponse> postDataTransfer(@RequestBody @Valid DataTransferRequest dataRequest,
			@RequestParam(value = "dataset_id") String datasetId,
			@RequestHeader("Authorization") String[] authorizationHeader)
			throws ConsentRecordNotFoundException, JsonMappingException, JsonProcessingException, ParseException,
			ServiceLinkRecordNotFoundException, ConsentStatusRecordNotValid, DatasetIdNotFoundException, JOSEException,
			DataRequestNotValid, CapeSdkManagerException {

		/*
		 * Check if exists active paired Consent Record for input Cr Id
		 */
		ConsentRecordSignedPair consentPair = clientService
				.callGetConsentRecordPairBySurrogateIdAndCrId(dataRequest.getSurrogateId(), dataRequest.getCrId())
				.getBody();
		@NonNull
		List<ConsentStatusRecordSigned> consentRecordStatusList = consentPair.getSource().getConsentStatusList();
		if (!consentRecordStatusList.get(consentRecordStatusList.size() - 1).getPayload().getConsentStatus()
				.equals(ConsentRecordStatusEnum.Active))
			throw new ConsentStatusRecordNotValid(
					"The paired Source Consent Status Record for Cr Id: " + dataRequest.getCrId() + " is not active");

		// Get Service Link by input Surrogate Id, to get the ServiceId
		ServiceLinkRecordDoubleSigned serviceLink = slrRepo.findByPayload_SurrogateId(dataRequest.getSurrogateId())
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException(
						"No Service Link Record with surrogateId: " + dataRequest.getSurrogateId() + " was found"));

		// Check if consent dataset matches with the Dataset id in input
		List<Dataset> consentDatasets = consentRecordStatusList.size() > 1
				? consentRecordStatusList.get(consentRecordStatusList.size() - 1).getPayload().getConsentResourceSet()
						.getDatasets()
				: consentPair.getSource().getPayload().getCommonPart().getRsDescription().getResourceSet()
						.getDatasets();
		if (!consentDatasets.get(consentDatasets.size() - 1).getId().equals(datasetId))
			throw new DatasetIdNotFoundException(
					"The dataset with id in input: " + datasetId + " was not found in the existing Consent Record");

		/*
		 * Check AuthorisationToken and Data Request signature
		 */
		String popHeader = null;
		for (String header : authorizationHeader) {
			if (header.startsWith("PoP "))
				popHeader = header.split("PoP ")[1];
		}
		if (StringUtils.isBlank(popHeader))
			throw new CapeSdkManagerException("The PoP Authorization of Data request is not present");

		if (!sdkManager.verifyTokenAndDataRequest(popHeader, consentPair, dataRequest))
			throw new DataRequestNotValid("The Data request signature is not valid");

		// TODO Check ​that​ ​the​ ​potential​ ​constraints​ ​set​ ​in​ ​the​ ​Consent​
		// ​Record​ ​are​ ​not​ ​violated

		// TODO ​MUST​ ​include​ ​at​ ​least​ ​verification​ ​that​ ​token’s
		// audience​ ​includes​ ​the​ ​URL​ ​to​ ​which​ ​the​ ​data​ ​request​ ​was​
		// ​made.

		// Depending on serviceId, call the relative API to get the data matching with
		// dataset
		String serviceId = serviceLink.getPayload().getServiceId();

		// Mapping concepts with data????

		// TODO Get Data depending on Specific Service implementation (use aud from
		// token?)
		return null;
	}

	@Operation(summary = "Create a new CaPe Account.", tags = { "Account" }, responses = {
			@ApiResponse(description = "Returns 201 Created with the created Account.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class))) })
	@Override
	@PostMapping(value = "/accounts")
	public ResponseEntity<Account> createAccount(@RequestBody @Valid Account account) {

		return clientService.createCapeAccount(account);

	}

	@Operation(summary = "Enforce Consent Usage Rule by filtering input body. Use the Active Consent Record matched (if any) by input UserId, Sink Service Id and Source Service Id. Optionally the Consent Record to match can be filtered by Dataset Id, Purpose Category and Processing Category.", tags = {
			"Data Request" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records belonging to the User linked with the input Surrogate Id", responseCode = "200") })
	@PostMapping(value = "/services/consents/enforceUsageRules")
	@Override
	public ResponseEntity<Object> enforceUsageRulesToPayload(@RequestParam(required = true) String userId,
			@RequestParam(required = true) String sinkServiceId, @RequestParam(required = false) String sourceServiceId,
			@RequestParam(required = false) String datasetId, @RequestParam(required = false) String purposeId,
			@RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) ProcessingCategory processingCategory,
			@RequestParam(defaultValue = "false") Boolean checkConsentAtOperator,
			@RequestBody Map<String, Object> dataObject) throws ConsentRecordNotFoundException {

		/*
		 * Get active Consent Records that match with UserId and Sink-Source Service
		 * pair
		 */
		List<ConsentRecordSigned> matchingConsents = getConsentRecordsByUserIdAndQuery(userId, sinkServiceId,
				sourceServiceId, datasetId, ConsentRecordStatusEnum.Active, purposeId, purposeName, purposeCategory,
				processingCategory, checkConsentAtOperator).getBody();

		if (matchingConsents == null || matchingConsents.isEmpty())
			throw new ConsentRecordNotFoundException(
					"No Usage Rules or Consent Record found for the input UserId and Sink-Source Service Ids.");

		// If any, there will be only one Active Matching Consent for input UserId and
		// Sink-Source
		ConsentRecordSigned consent = matchingConsents.get(0);
		List<Dataset> conceptDatasets = consent.getPayload().getCommonPart().getRsDescription().getResourceSet()
				.getDatasets();

		for (Dataset dataset : conceptDatasets) {

			List<String> properties = dataset.getDataMappings().stream().map(concept -> concept.getProperty())
					.collect(Collectors.toList());

			dataObject = dataObject.entrySet().stream().filter(entry -> properties.contains(entry.getKey()))
					.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));

		}

		return ResponseEntity.ok(dataObject);
	}

}
