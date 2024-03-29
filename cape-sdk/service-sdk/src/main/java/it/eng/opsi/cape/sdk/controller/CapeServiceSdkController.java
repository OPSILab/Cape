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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import it.eng.opsi.cape.exception.AccountNotFoundException;
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
import it.eng.opsi.cape.sdk.ApplicationProperties;
import it.eng.opsi.cape.sdk.model.ServicePopKey;
import it.eng.opsi.cape.sdk.model.ServiceSignKey;
import it.eng.opsi.cape.sdk.model.DataOperatorDescription;
import it.eng.opsi.cape.sdk.model.SurrogateIdResponse;
import it.eng.opsi.cape.sdk.model.account.Account;
import it.eng.opsi.cape.sdk.model.consenting.ChangeConsentStatusRequest;
import it.eng.opsi.cape.sdk.model.consenting.CommonPart;
import it.eng.opsi.cape.sdk.model.consenting.ConsentForm;
import it.eng.opsi.cape.sdk.model.consenting.ConsentFormRequest;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSignedPair;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordStatusEnum;
import it.eng.opsi.cape.sdk.model.consenting.ConsentStatusRecordPayload;
import it.eng.opsi.cape.sdk.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.sdk.model.consenting.Dataset;
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
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry.Role;
import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@OpenAPIDefinition(info = @Info(title = "SDK Service API", description = "SDK Service API for integration with cape", version = "2.0"), tags = {
		@Tag(name = "Service Linking", description = "Cape SDK APIs to start and manage Service Linking starting from Service."),
		@Tag(name = "(Internal) Service Linking", description = "Cape SDK APIs used internally by Cape Server to handle operations during Service Linking. These endpoints are part of Service Linking transaction and should not be called independently."),
		@Tag(name = "Service Entry", description = "Service Description APIs to get and manage service entry descriptions."),
		@Tag(name = "Service Management", description = "Cape SDK APIs to handle Service Registrations managed by this SDK and already present in the Cape Service Registry."),
		@Tag(name = "Service Link Record", description = "Cape SDK APIs to manage Service Link Records."),
		@Tag(name = "Consent Record", description = "Cape SDK APIs to query CaPe Consent Records."),
		@Tag(name = "Consenting", description = "Cape SDK APIs to perform CaPe Consenting operations."),
		@Tag(name = "(Internal) Consenting", description = "Consent Manager APIs to perform internal communications between SDK and Cape Server during Consenting operations. These endpoints are part of Consenting transaction and should not be called independently.") })
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

	@Operation(summary = "Get running Cape version")
	@GetMapping(value = "/version", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getCapeVersion() throws JsonProcessingException {

		Map<String, String> result = new HashMap<String, String>(1);
		result.put("version", this.appProperty.getCape().getVersion());

		return ResponseEntity.ok(objectMapper.writeValueAsString(result));
	}

	@Operation(summary = "Get Operator Description for CaPe by Operator Id", tags = {
			"Data Operator Description" }, responses = {
					@ApiResponse(description = "Returns the requested Operator Description.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataOperatorDescription.class))) })
	@Override
	@GetMapping(value = "/dataOperatorDescriptions/{operatorId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DataOperatorDescription> getDataOperatorDescription(
			@PathVariable("operatorId") String operatorId)
			throws DataOperatorDescriptionNotFoundException, ServiceManagerException {

		/*
		 * Check if operator exists TODO user its linkingRedirectUri to return new SLR
		 * to the appopriate URL (asynch push service?)
		 */

		DataOperatorDescription dataOperatorDescription = clientService.fetchOperatorDescription(operatorId);

		return ResponseEntity.ok().body(dataOperatorDescription);
	}

	@Operation(summary = "Get Service Linking sessionCode from Service Manager to start Automatic Service Linking", tags = {
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

	@Operation(summary = "Link the Service User's Account to CaPe Account.", description = "Initiate linking process from service. It needs the generated sessionCode and surrogateId taken using the respective APIs ( /slr/linking/sessionCode and /slr/linking/surrogateId .", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 CREATED and the created Service Link Record and Service Link Status Record", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FinalLinkingResponse.class))) })
	@Override
	@PostMapping(value = "/slr/linking", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FinalLinkingResponse> startServiceLinking(@RequestBody @Valid StartLinkingRequest request)
			throws ServiceManagerException, DataOperatorDescriptionNotFoundException, JOSEException,
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

		DataOperatorDescription dataOperatorDescription = clientService.fetchOperatorDescription(operatorId);

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

	@Operation(summary = "Generate surrogateId for given Service User's Account.", description = "Generate surrogateId to be used in the /slr/linking to start Service Linking. This will represent the same User in both sides (Service and Cape).", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 CREATED and the created surrogate_id ", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurrogateIdResponse.class))) })
	@Override
	@GetMapping(value = "/slr/linking/surrogateId")
	public ResponseEntity<SurrogateIdResponse> generateSurrogateId(@RequestParam(required = true) String operatorId,
			@RequestParam(required = true) String userId) {

		String surrogateId = sdkManager.generateSurrogateId(operatorId, userId);

		return ResponseEntity.status(HttpStatus.CREATED).body(new SurrogateIdResponse(surrogateId));

	}

	@Operation(summary = "Register a Service existing in the Service Registry as managed by CaPe.", description = "Register the Service as managed by this SDK and create Service key pair and x509 Certificate. Once registered, the Service will be available to the User for service linking and will be in Completed status.", tags = {
			"Service Management" }, responses = {
					@ApiResponse(description = "Returns 201 CREATED and the Service Description containing generated Public X509 Certificate.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceEntry.class))) })
	@Override
	@PostMapping(value = "/services/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceEntry> registerServiceToCape(@PathVariable String serviceId) throws JOSEException,
			ServiceManagerException, JsonProcessingException, ServiceDescriptionNotFoundException {

		ServiceEntry certificateFilledAndSignedDescription = sdkManager.registerServiceToCape(serviceId);

		return ResponseEntity.status(HttpStatus.CREATED).body(certificateFilledAndSignedDescription);
	}

	@Operation(summary = "Unregister a Service managed by CaPe. Optionally delete also Service Description from Service Registry.", description = "Delete Service Sign key pair (and PoP key if Sink) and X509 Certificate from Service Description at Registry. Until is unregistered, the Service will not be visible/available to the User.", tags = {
			"Service Management" }, responses = {
					@ApiResponse(description = "Returns 204 No Content", responseCode = "204"),
					@ApiResponse(description = "Returns 404, no Service or Sign Key was found", responseCode = "404") })
	@Override
	@DeleteMapping(value = "/services/{serviceId}")
	public ResponseEntity<Object> unregisterOrDeleteService(@PathVariable String serviceId,
			@RequestParam(defaultValue = "false") Boolean deleteServiceDescription) throws JOSEException,
			ServiceManagerException, ServiceSignKeyNotFoundException, ServiceDescriptionNotFoundException {

		sdkManager.unregisterService(serviceId, deleteServiceDescription);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Get sign keys of services registered to Cape by this SDK.", description = "Get registered services sign keys managed by this SDK (associated to its Service Provider Business Id).", tags = {
			"Service Management" }, responses = {
					@ApiResponse(description = "Returns 200 OK and the list of requested services", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceSignKey.class))) })
	@Override
	@GetMapping(value = "/services/signingKeys")
	public ResponseEntity<List<ServiceSignKey>> getRegisteredServicesKeys() {

		return ResponseEntity.status(HttpStatus.OK).body(sdkManager.getRegisteredServicesKeys());

	}

	@Operation(summary = "Get services descriptions (optionally only the ones registered to CaPe) managed by this SDK. Optionally can be filtered by Service Identifier (URI).", description = "Get services managed by this SDK (associated to its Service Provider Business Id).", tags = {
			"Service Entry" }, responses = {
					@ApiResponse(description = "Returns 200 OK and the list of requested services", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceEntry.class))) })
	@Override
	@GetMapping(value = "/services")
	public ResponseEntity<List<ServiceEntry>> getServices(@RequestParam(defaultValue = "false") Boolean onlyRegistered,
			@RequestParam(required = false) String serviceUrl) throws ServiceManagerException {

		return ResponseEntity.status(HttpStatus.OK)
				.body(sdkManager.getServices(onlyRegistered, serviceUrl, businessId));
	}

	@Operation(summary = "Get Service Description by ServiceId (optionally only the ones registered to CaPe) managed by this SDK.", description = "Get service managed by this SDK (associated to the Service Provider Business Id).", tags = {
			"Service Entry" }, responses = {
					@ApiResponse(description = "Returns 200 OK and the requested service", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceEntry.class))) })
	@Override
	@GetMapping(value = "/services/{serviceId}")
	public ResponseEntity<ServiceEntry> getService(@PathVariable String serviceId,
			@RequestParam(defaultValue = "false") Boolean onlyRegistered)
			throws ServiceManagerException, ServiceDescriptionNotFoundException {

		return ResponseEntity.status(HttpStatus.OK).body(sdkManager.getService(serviceId, onlyRegistered));
	}

	@Operation(summary = "Sign the Service Link Record payload with Service key.", description = "Sign the Service Link Record payload with Service key after verifying SLR signature made by Account Manager.", tags = {
			"(Internal) Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 CREATED and the SLR Payload signed with Service private key.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceSignSlrResponse.class))) })
	@Override
	@PostMapping(value = "/slr/sign", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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

	@Operation(summary = "Return all the Service Link Records managed by this SDK instance (associated to its Service Provider Business Id)", tags = {
			"Service Link Record" }, responses = {
					@ApiResponse(description = "Returns Service Link Records.", responseCode = "200") })
	@Override
	@GetMapping(value = "/slr")
	public ResponseEntity<List<ServiceLinkRecordDoubleSigned>> getAllServiceLinkRecords() {

		List<ServiceLinkRecordDoubleSigned> result = slrRepo.findAll();

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Return Service Link Records associated to a specific Service Id.", tags = {
			"Service Link Record" }, responses = {
					@ApiResponse(description = "Returns Service Link Records.", responseCode = "200") })
	@Override
	@GetMapping(value = "/services/{serviceId}/slr")
	public ResponseEntity<List<ServiceLinkRecordDoubleSigned>> getServiceLinkRecordsByServiceId(
			@PathVariable String serviceId) {

		List<ServiceLinkRecordDoubleSigned> result = slrRepo.findByPayload_ServiceId(serviceId);

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Return Service Link Record by Slr Id", tags = { "Service Link Record" }, responses = {
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

	@Operation(summary = "Return Service Link Record by Surrogate Id.", tags = { "Service Link Record" }, responses = {
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

	@Operation(summary = "Return Service Link Record by Surrogate Id and Service Id.", tags = {
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

	@Operation(summary = "Insert a new link between Service userId, surrogateId, serviceId and operatorId.", description = "It is used by SDK once the Service Linking has been completed at Cape. This link holds the matching between Service User Id (known only on Service side) and Surrogate Id (generated By Cape SDK).", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 CREATED and the created UserSurrogateIdLink.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserSurrogateIdLink.class))) })
	@Override
	@PostMapping(value = "/userSurrogateIdLink", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserSurrogateIdLink> linkUserToSurrogateId(@RequestBody @Valid UserSurrogateIdLink entity) {

		entity.setCreated(ZonedDateTime.now(ZoneId.of("UTC")));
		UserSurrogateIdLink result = userSurrogateIdRepo.insert(entity);

		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@Operation(summary = "Get the link between Service userId, surrogateId, serviceId and operatorId.", description = "It is used to easily discover from the Service side if a Service User has a link to Cape for the specific Service managed by this SDK.", tags = {
			"Service Linking" }, responses = {
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
	 * Notify a changed Service Link Status Record to be saved by SDK, due to a
	 * Service Link status changed starting from Operator
	 * 
	 * @throws ParseException
	 * @throws JOSEException
	 * @throws JsonProcessingException
	 * @throws ServiceLinkStatusRecordNotValid
	 **/
	@Operation(summary = "Notify a changed Service Link Status Record to be saved by SDK, due to a Service Link status change coming from Operator (Cape Server/User Dashboard).", description = "This API is used internally by Cape when a Service Link Status is changed by the User. SDK will append the Service Link Status Record to the status list of affected Service Link Record.", tags = {
			"(Internal) Service Linking" }, responses = {
					@ApiResponse(description = "Returns 200 OK.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
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
			slrRepo.findByPayload_SlrId(slrId).orElseThrow(
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
	@Operation(summary = "Enable an existing disabled ('Removed' status) Service Link Record, due to a request started from Service.", description = "SDK will call Cape Server to change the Service Link status and will append the new Service Link Status Record to the status list of affected Service Link Record.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 Created and the new Service Link Status Record.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
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
	 * Disable an existing enabled Service Link, due to a status change request
	 * started from Service
	 * 
	 * @throws ServiceLinkRecordNotFoundException
	 **/
	@Operation(summary = "Disable an existing enabled ('Enabled' status) Service Link Record, due to a request started from Service.", description = "SDK will call Cape Server to change the Service Link status and will append the new Service Link Status Record to the status list of affected Service Link Record.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 Created and the new Service Link Status Record.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
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
	@Operation(summary = "Fetch generated Consent Form for input Surrogate Id (depends on requesting party), (Sink) ServiceId , Purpose Id and optionally sourceDatasetId and sourceServiceId (in 3rd party consenting case)", description = "PurposeId must match with one of the purposes present in the (Sink) Service Description. In case of 3rd party consenting, sourceDatasetId and sourceServiceId parameters must be both present. The fetched Consent Form will be used in the Give Consent API.", tags = {
			"Consenting" }, responses = {
					@ApiResponse(description = "Returns the generated Consent Form containing the Resource Set generated either by matching Sink and Source datasets' data mappings/concepts (3-party consenting case) or directly from the datasets required by the selected Purpose of the service (Within Service case).", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsentForm.class))) })
	@PostMapping(value = "/consents/consentForm", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ConsentForm> fetchConsentForm(@RequestBody @Valid ConsentFormRequest request)
			throws ServiceManagerException, CapeSdkManagerException {

		return clientService.callFetchConsentForm(request);

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
	@Operation(summary = "Verify and store a new Consent Record issued by the Operator (Cape Consent Manager).", tags = {
			"(Internal) Consenting" }, responses = {
					@ApiResponse(description = "Returns 201 Created and the URI of the stored Consent Record.", responseCode = "201", content = @Content(mediaType = "text/plain")) })
	@Override
	@PostMapping(value = "/consents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
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

		if (!sdkManager.verifyConsentRecordSigned(consentRecord, associatedSlr))
			throw new ConsentRecordNotValid("The signature of the input Consent Record is not valid");

		ConsentStatusRecordSigned consentStatusRecord = consentRecord.getConsentStatusList()
				.get(consentRecord.getConsentStatusList().size() - 1);
		if (!sdkManager.verifyConsentStatusRecordSigned(consentStatusRecord, associatedSlr))
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
	 * Verify and update the existing Consent Record issued by the Consent Manager
	 * 
	 * @throws ConsentStatusRecordNotValid
	 * @throws ConsentRecordNotFoundException
	 * @throws ServiceLinkRecordNotFoundException
	 * @throws JOSEException
	 * @throws ParseException
	 * @throws JsonProcessingException
	 * @throws ConsentRecordNotValid
	 **/
	@Operation(summary = "Verify and update the Consent Record (along with new Csr to be appended to the Csr list) issued by the Operator (Cape Consent Manager).", tags = {
			"(Internal) Consenting" }, responses = {
					@ApiResponse(description = "Returns 201 Created.", responseCode = "201", content = @Content(mediaType = "text/plain")) })
	@Override
	@PatchMapping(value = "/consents/{crId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> updateConsentRecordWithNewStatus(@PathVariable String crId,
			@RequestBody @Valid ConsentRecordSigned updatedCr)
			throws ConsentStatusRecordNotValid, ConsentRecordNotFoundException, ServiceLinkRecordNotFoundException,
			JsonProcessingException, ParseException, JOSEException, ConsentRecordNotValid {

		String bodyCrId = updatedCr.getPayload().getCommonPart().getCrId();

		if (!bodyCrId.equals(crId))
			throw new ConsentStatusRecordNotValid("The crId in the path and the crId in the body does not match");

		ConsentStatusRecordSigned updatedCsr = updatedCr.getConsentStatusList()
				.get(updatedCr.getConsentStatusList().size() - 1);

		/*
		 * Get existing Consent Record matching to the input Consent Record Id
		 */
		ConsentRecordSigned existingCr = consentRecordRepo.findByPayload_commonPart_crId(bodyCrId).orElseThrow(
				() -> new ConsentRecordNotFoundException("The Consent Record with id: " + bodyCrId + "was not found"));
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
		 * Account public key contained in the associated SLR (jwk field in protected
		 * header of first signature)
		 */
		if (!sdkManager.verifyConsentStatusRecordSigned(updatedCsr, associatedSlr))
			throw new ConsentStatusRecordNotValid("The signature of the input Consent Status Record is not valid");

		if (!sdkManager.verifyConsentRecordSigned(updatedCr, associatedSlr))
			throw new ConsentRecordNotValid("The signature of the input Consent Record is not valid");

		/*
		 * If Cape Server is sending forceUpdate=true, it means that a previous Cr
		 * status change notification failed
		 */
//		if (!forceUpdate) {
		/*
		 * Add the new last CSR to the existing CSR list and Verify CSR chain is intact
		 */
		existingCsrList.add(updatedCsr);
		if (!sdkManager.verifyConsentStatusChain(existingCsrList.stream().map(ConsentStatusRecordSigned::getPayload)
				.toArray(ConsentStatusRecordPayload[]::new)))
			throw new ConsentStatusRecordNotValid(
					"The input Consent Record does not have a valid Consent Statuses chain");

//		} 

		// Store the Cr updated with the new Csr
		updatedCr.set_id(new ObjectId(updatedCr.getPayload().getCommonPart().getCrId()));
		consentRecordRepo.save(updatedCr);

		return ResponseEntity.ok().body(null);
	}

	/**
	 * Give Consent from Consent Manager
	 */
	@Operation(summary = "Give the Consent for the input ConsentForm. Generated Cr(s) will be saved and notified by Consent Manager.", description = "Return the newly generated Consent Record signed with the Acccount private key. (In case of 3rd party consenting, return the signed Cr copy related to which party has started consenting (Sink or Source).", tags = {
			"Consenting" }, responses = {
					@ApiResponse(description = "Returns 201 Created with the created Consent Record Signed.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsentRecordSigned.class))) })
	@PostMapping(value = "/consents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ConsentRecordSigned> giveConsent(@RequestBody @Valid ConsentForm consentForm) {

		ResponseEntity<ConsentRecordSigned> giveConsentResponse = clientService.callGiveConsent(consentForm);
		ConsentRecordSigned responseCr = giveConsentResponse.getBody();

		String crId = responseCr.getPayload().getCommonPart().getCrId();

		/*
		 * If the Response Status from Consent Manager is 201 Created (is a new
		 * ConsentRecord)
		 */
		if (giveConsentResponse.getStatusCode().equals(HttpStatus.CREATED)) {

			/*
			 * If both sourceId and sourceName are not blank, we are in 3rd party consenting
			 * case Get and save the (serialized) authorisation Token from Consent Manager
			 */
			if (!StringUtils.isAnyBlank(consentForm.getSourceServiceId(), consentForm.getSourceName())) {
				AuthorisationTokenResponse tokenResponse = clientService.callGetAuthorisationToken(crId);
				authTokenRepo.save(tokenResponse);
			}

		}

		CommonPart crCommonPart = responseCr.getPayload().getCommonPart();
		return ResponseEntity.created(UriComponentsBuilder
				.fromHttpUrl(
						appProperty.getCape().getServiceSdk().getHost() + "/api/v2/users/{surrogateId}/consents/{crId}")
				.build(consentForm.getRequesterSurrogateRole().equals(Role.SINK) ? crCommonPart.getSurrogateId()
						: crCommonPart.getSourceSurrogateId(), crCommonPart.getCrId()))
				.body(responseCr);

	}

	@Operation(summary = "Delete Consent Record Signed due to a rollback from Consent Manager.", tags = {
			"(Internal) Consenting" }, responses = {
					@ApiResponse(description = "Returns No Content.", responseCode = "204") })
	@DeleteMapping(value = "/consents/{crId}")
	@Override
	public ResponseEntity<?> deleteConsentRecord(@PathVariable String crId) throws ConsentRecordNotFoundException {

		if (consentRecordRepo.deleteConsentRecordSignedByPayload_commonPart_crId(crId) == 0L)
			throw new ConsentRecordNotFoundException("No Consent Record with Cr Id: " + crId + " was found");

		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Get the list of signed Consent Records for the input Surrogate Id.", description = "In 3rd party consenting case the Surrogate Id can be relative to a linked service acting in the consent either as sink or source. Optionally can be filtered by Source Service Id, Dataset Id, Consent Status, Purpose Id, Name or Category and Processing Category. Results can be sorted by the value of the iat timestamp of Consent Record (DESC by default). The query can be performed against the SDK local storage (default) or by calling Cape Consent Manager (checkConsentAtOperator=true).", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records belonging to the User linked with the input Surrogate Id.", responseCode = "200") })
	@GetMapping(value = "/users/surrogates/{surrogateId}/consents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsBySurrogateIdAndQuery(
			@PathVariable String surrogateId, @RequestParam(required = false) String serviceId,
			@RequestParam(required = false) String sourceServiceId, @RequestParam(required = false) String datasetId,
			@RequestParam(required = false) ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) ProcessingCategory processingCategory,
			@RequestParam(defaultValue = "false") Boolean checkConsentAtOperator,
			@RequestParam(defaultValue = "DESC") Sort.Direction iatSort) {

		if (checkConsentAtOperator)
			return ResponseEntity.ok(Arrays.asList(clientService.callGetConsentRecordsByBusinessIdAndQuery(businessId,
					surrogateId, serviceId, sourceServiceId, datasetId, status, purposeId, purposeName, purposeCategory,
					processingCategory, iatSort)));
		else

			return ResponseEntity
					.ok(consentRecordRepo.findBySurrogateIdAndQuery(surrogateId, serviceId, sourceServiceId, datasetId,
							status, purposeId, purposeName, purposeCategory, processingCategory, iatSort));

	}

	@Operation(summary = "Get the list of signed Consent Records for the input Service User Id.", description = "Optionally can be filtered by Service Id, Source Service Id, Dataset Id, Consent Status, Purpose Id, Name or Category and Processing Category. Results can be sorted by the value of the iat timestamp of Consent Record (DESC by default). The query can be performed against the SDK local storage (default) or by calling Cape Consent Manager (checkConsentAtOperator=true).", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records belonging to the input User Id.", responseCode = "200") })
	@GetMapping(value = "/users/{userId}/consents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByUserIdAndQuery(@PathVariable String userId,
			@RequestParam(required = false) String serviceId, @RequestParam(required = false) String sourceServiceId,
			@RequestParam(required = false) String datasetId,
			@RequestParam(required = false) ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) ProcessingCategory processingCategory,
			@RequestParam(defaultValue = "false") Boolean checkConsentAtOperator,
			@RequestParam(defaultValue = "DESC") Sort.Direction iatSort) {

		/*
		 * Retrieve SurrogateIds corresponding to the input UserId, in order to query
		 * ConsentRecord by SurrogateId. In case either the serviceId or the
		 * sourceServiceId is specified, there will be only one matching surrogateId
		 */
		List<UserSurrogateIdLink> matchingSurrogateIds = null;
		List<ConsentRecordSigned> result = new ArrayList<ConsentRecordSigned>(0);

		if (StringUtils.isBlank(serviceId))
			matchingSurrogateIds = userSurrogateIdRepo.findByUserIdOrderByCreatedAsc(userId);
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
						checkConsentAtOperator, iatSort).getBody());
			}

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Get the list of signed Consent Records for the input Service Id", description = "Optionally can be filtered by Service User Id, Source Service Id, Dataset Id, Consent Status, Purpose Id, Name or Category and Processing Category. Results can be sorted by the value of the iat timestamp of Consent Record (DESC by default). The query can be performed against the SDK local storage (default) or by calling Cape Consent Manager (checkConsentAtOperator=true).", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records for the input ServiceId", responseCode = "200") })
	@GetMapping(value = "/services/{serviceId}/consents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByServiceIdAndQuery(
			@PathVariable String serviceId, @RequestParam(required = false) String userId,
			@RequestParam(required = false) String sourceServiceId, @RequestParam(required = false) String datasetId,
			@RequestParam(required = false) ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) ProcessingCategory processingCategory,
			@RequestParam(defaultValue = "false") Boolean checkConsentAtOperator,
			@RequestParam(defaultValue = "DESC") Sort.Direction iatSort) {

		if (StringUtils.isNotBlank(userId)) {
			return this.getConsentRecordsByUserIdAndQuery(userId, serviceId, sourceServiceId, datasetId, status,
					purposeId, purposeName, purposeCategory, processingCategory, checkConsentAtOperator, iatSort);
		} else {

			if (checkConsentAtOperator)

				return ResponseEntity
						.ok(Arrays.asList(clientService.callGetConsentRecordsByBusinessIdAndQuery(businessId, null,
								serviceId, sourceServiceId, datasetId, status, purposeId, purposeName, purposeCategory,
								processingCategory, iatSort)));
			else
				return ResponseEntity.ok(consentRecordRepo.findByServiceIdAndQuery(serviceId, sourceServiceId,
						datasetId, status, purposeId, purposeName, purposeCategory, processingCategory, iatSort));

		}
	}

	@Operation(summary = "Check if there is an Active Consent Record for the input Service Id and UserId (optional parameters are supported)", description = "Optionally can be filtered by Source Service Id, Dataset Id, Consent Status, Purpose Id, Name or Category and Processing Category. The query can be performed against the SDK local storage (default) or by calling Cape Consent Manager (checkConsentAtOperator=true).", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the existing Active Consent Record (with 200 as Http Status code), if any, otherwise 404.", responseCode = "200"),
					@ApiResponse(description = "Returns the existing Active Consent Record (with 200 as Http Status code), if any, otherwise 404.", responseCode = "404") })
	@GetMapping(value = "/services/{serviceId}/consents/check", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ConsentRecordSigned> checkConsentRecordByServiceIdAndUserIdAndQuery(@PathVariable String serviceId,
			@RequestParam(required = true) String userId, @RequestParam(required = false) String sourceServiceId,
			@RequestParam(required = false) String datasetId,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) ProcessingCategory processingCategory,
			@RequestParam(defaultValue = "false") Boolean checkConsentAtOperator,
			@RequestParam(defaultValue = "DESC") Sort.Direction iatSort) throws ConsentRecordNotFoundException {

		List<ConsentRecordSigned> result = this.getConsentRecordsByServiceIdAndQuery(serviceId, userId.toLowerCase(), sourceServiceId,
				datasetId, ConsentRecordStatusEnum.Active, purposeId, purposeName, purposeCategory, processingCategory,
				checkConsentAtOperator, iatSort).getBody();
		if (result.isEmpty())
			throw new ConsentRecordNotFoundException(
					"No Active Consent Record found for the input ServiceId and UserId.");
		else
			return ResponseEntity.ok(result.get(0));

	}

	@Operation(summary = "Get the list of all signed Consent Records for this Service Provider by using its assigned Business Id.", description = "Optionally can be filtered by SurrogateId, Service Id, Source Service Id, Dataset Id, Consent Status, Purpose Id, Name or Category and Processing Category. Results can be sorted by the value of the iat timestamp of Consent Record(DESC by default). The query can be performed against the SDK local storage (default) or by calling Cape Consent Manager (checkConsentAtOperator=true).", tags = {
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
			@RequestParam(defaultValue = "false") Boolean checkConsentAtOperator,
			@RequestParam(defaultValue = "DESC") Sort.Direction iatSort) {

		if (checkConsentAtOperator)
			return ResponseEntity.ok(Arrays.asList(clientService.callGetConsentRecordsByBusinessIdAndQuery(businessId,
					surrogateId, serviceId, sourceServiceId, datasetId, status, purposeId, purposeName, purposeCategory,
					processingCategory, iatSort)));
		else
			return ResponseEntity
					.ok(consentRecordRepo.findByBusinessIdAndQuery(businessId, surrogateId, serviceId, sourceServiceId,
							datasetId, status, purposeId, purposeName, purposeCategory, processingCategory, iatSort));
	}

	@Operation(summary = "Get the list of the pairs of signed Consent Records (sink, source) managed by this SDK (associated to its Service Provider Business Id)", description = "Optionally can be filtered by SurrogateId, Service Id, Source Service Id, Dataset Id, Consent Status, Purpose Id, Name or Category and Processing Category. In 3rd party consenting case, the optional Surrogate Id can be relative to a linked service acting in the consent either as sink or source. Results can be sorted by the value of the iat timestamp of Consent Record(DESC by default). The query can be performed against the SDK local storage (default) or by calling Cape Consent Manager (checkConsentAtOperator=true).", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records for all the Users for all services managed by this SDK (associated to its Service Provided Business Id.", responseCode = "200") })
	@GetMapping(value = "/consents/pair", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsByBusinessIdAndQuery(
			@RequestParam(required = false) String surrogateId, @RequestParam(required = false) String serviceId,
			@RequestParam(required = false) String sourceServiceId, @RequestParam(required = false) String datasetId,
			@RequestParam(required = false) ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) ProcessingCategory processingCategory,
			@RequestParam(defaultValue = "DESC") Sort.Direction iatSort) {

		return ResponseEntity.ok(Arrays.asList(clientService.callGetConsentRecordPairsByBusinessIdAndQuery(businessId,
				surrogateId, serviceId, sourceServiceId, datasetId, status, purposeId, purposeName, purposeCategory,
				processingCategory, iatSort)));

	}

	@Operation(summary = "Change the status (starting from Service) of an existing Consent Record associated to the input CrId, SlrId and SurrogateId.", description = "The new Status can contain not only the new state (Active, Disabled, Withdrawn), but also a new Resource Set or Sink Usage Rules. Call the Cape Consent Manager to change status (from Service) the existing Consent Record. This will trigger the notification from Cape to the SDK of the newly generated Consent Status Record.", tags = {
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

	@Operation(summary = "Final End point to perform Data Transfer from the Source Service.", tags = {
			"(Internal) Data Request" }, responses = {
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

	@Operation(summary = "Create a new CaPe Account starting from Service.", tags = {
			"(Internal) Service Linking" }, responses = {
					@ApiResponse(description = "Returns 201 Created with the created Account.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class))) })
	@Override
	@PostMapping(value = "/accounts")
	public ResponseEntity<Account> createCapeAccount(@RequestBody @Valid Account account) {

		return clientService.createCapeAccount(account);

	}

	@Operation(summary = "Delete User - Surrogate Link.", description = "Used by Account Manager due to an account deletion.", tags = {
			"Account" }, responses = {
					@ApiResponse(description = "Returns 201 Created with the created Account.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class))) })
	@Override
	@DeleteMapping(value = "/userSurrogateIdLink/{surrogateId}")
	public ResponseEntity<?> deleteUserToSurrogateId(@PathVariable String surrogateId) throws CapeSdkManagerException {

		/*
		 * For each input SurrogateId:
		 * 
		 * - Set matching Service Link Record to "Removed"? - Set related Consent
		 * Records to Withdrawn?
		 * 
		 * For now delete only userSurrogateIdLink
		 */
		Long deletedCount = 0L;
		deletedCount += userSurrogateIdRepo.deleteUserSurrogateIdLinkBySurrogateId(surrogateId);

		if (deletedCount != 1)
			throw new CapeSdkManagerException("There was an error while deleting UserSurrogateIdLink");

		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Enforce Usage Rules associated to a User Consent to the input body to filter fields disallowed by the matching Consent Record. Use the Active Consent Record matched (if any) by input UserId, Sink Service Id and Source Service Id. Optionally the Consent Record to match can be filtered by Dataset Id, Purpose Category and Processing Category.", tags = {
			"Data Request" }, responses = {
					@ApiResponse(description = "Returns the input body with fields filtered according to the Consent Record associated to the input UserId", responseCode = "200") })
	@PostMapping(value = "/services/consents/enforceUsageRules")
	@Override
	public ResponseEntity<Object> enforceUsageRulesToPayload(@RequestParam(required = true) String userId,
			@RequestParam(required = false) String sinkServiceId,
			@RequestParam(required = false) String sourceServiceId,
			@RequestParam(required = false) String sinkServiceUrl,
			@RequestParam(required = false) String sourceServiceUrl, @RequestParam(required = false) String datasetId,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) ProcessingCategory processingCategory,
			@RequestParam(defaultValue = "false") Boolean checkConsentAtOperator,
			@RequestBody Map<String, Object> dataObject)
			throws ConsentRecordNotFoundException, ServiceManagerException, ServiceDescriptionNotFoundException {

		List<ConsentRecordSigned> matchingConsents = null;
		/*
		 * Get active Consent Records that match with UserId and Sink-Source Service
		 * pair
		 */

		//Robcalla: Updated removing the check to the sourceServiceId 
		//if (StringUtils.isNotBlank(sinkServiceId) && StringUtils.isNotBlank(sourceServiceId))
		if (StringUtils.isNotBlank(sinkServiceId))
			matchingConsents = getConsentRecordsByUserIdAndQuery(userId, sinkServiceId, sourceServiceId, datasetId,
					ConsentRecordStatusEnum.Active, purposeId, purposeName, purposeCategory, processingCategory,
					checkConsentAtOperator, Sort.Direction.ASC).getBody();
		else if (StringUtils.isNotBlank(sinkServiceUrl) && StringUtils.isNotBlank(sourceServiceUrl)) {
			/*
			 * Get Service descrcriptions from Service Urls in input (identifier) to get
			 * corresponding IDs
			 */
			ServiceEntry sinkService = sdkManager.getServices(true, sinkServiceUrl, businessId).get(0);
			ServiceEntry sourceService = sdkManager.getServices(true, sourceServiceUrl, businessId).get(0);
			sinkServiceId = sinkService.getServiceId();
			sourceServiceId = sourceService.getServiceId();

			matchingConsents = getConsentRecordsByUserIdAndQuery(userId, sinkServiceId, sourceServiceId, datasetId,
					ConsentRecordStatusEnum.Active, purposeId, purposeName, purposeCategory, processingCategory,
					checkConsentAtOperator, Sort.Direction.ASC).getBody();

		}

		if (matchingConsents == null || matchingConsents.isEmpty())
			throw new ConsentRecordNotFoundException(
					"No Usage Rules or Active Consent Record found for the input UserId, Sink-Source Service Ids/Urls and optional query paramaeters.");

		// If any, there will be only one Active Matching Consent for input UserId and
		// Sink-Source
		ConsentRecordSigned consent = matchingConsents.get(0);
		List<Dataset> consentDatasets = consent.getPayload().getCommonPart().getRsDescription().getResourceSet()
				.getDatasets();

		/*
		 * Filter out only fields that are in the Service / Purpose dataset but not in
		 * the Consent Dataset because Data subject deselected the optional concepts
		 * from available ones from Service Dataset
		 */

		for (Dataset dataset : consentDatasets) {

			List<String> consentProperties = dataset.getDataMappings().stream().map(concept -> concept.getProperty())
					.collect(Collectors.toList());

			DataMapping[] serviceConcepts = clientService.getMatchingDatasets(sinkServiceId, sourceServiceId,
					dataset.getPurposeId(), dataset.getId());
			List<String> serviceProperties = Arrays.asList(serviceConcepts).stream()
					.map(concept -> concept.getProperty()).collect(Collectors.toList());

			/*
			 * Keep only fields that are neither in serviceConcepts (Concepts matching
			 * between datasets required by sink and ones provided by source) or not are
			 * concepts contained in serviceConcepts but disallowed by Data Subject Consent
			 */
			dataObject = dataObject.entrySet().stream().filter(
					entry -> !serviceProperties.contains(entry.getKey()) || consentProperties.contains(entry.getKey()))
					.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));

		}

		return ResponseEntity.ok(dataObject);
	}

}
