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

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.accountmanager.repository.AccountInfoOnly;
import it.eng.opsi.cape.accountmanager.repository.AccountRepository;
import it.eng.opsi.cape.accountmanager.service.ClientService;
import it.eng.opsi.cape.accountmanager.service.CryptoService;
import it.eng.opsi.cape.exception.AccountManagerException;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.AuditLogNotFoundException;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkStatusRecordNotFoundException;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import it.eng.opsi.cape.accountmanager.ApplicationProperties;
import it.eng.opsi.cape.accountmanager.model.Account;
import it.eng.opsi.cape.accountmanager.model.AccountExport;
import it.eng.opsi.cape.accountmanager.model.AccountInfo;
import it.eng.opsi.cape.accountmanager.model.SurrogateIdServiceIdRecord;
import it.eng.opsi.cape.accountmanager.model.audit.AccountActionType;
import it.eng.opsi.cape.accountmanager.model.audit.AccountEventLog;
import it.eng.opsi.cape.accountmanager.model.audit.AuditLog;
import it.eng.opsi.cape.accountmanager.model.audit.EventLog;
import it.eng.opsi.cape.accountmanager.model.audit.EventType;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentRecordSigned;
import lombok.extern.slf4j.Slf4j;

@OpenAPIDefinition(info = @Info(title = "CaPe API - Account Manager", description = "CaPe APIs used to manage CRUD on CaPe Account and related Service Link Records and for internal operations during the overall Consent and Service Linking processes.", version = "2.0"), tags = {
		@Tag(name = "Account", description = "Account Manager APIs to manage CaPe Account."),
		@Tag(name = "Service Link Record", description = "Account Manager APIs to manage Service Link Records."),
		@Tag(name = "(Internal) Service Linking", description = "Account Manager APIs to handle Service Linking internal operations."),
		@Tag(name = "(Internal) Consenting", description = "Account Manager APIs to handle Consenting internal operations.") })
@RestController
@RequestMapping("/api/v2")
@Slf4j
//@CrossOrigin(origins= {"*"})
public class AccountController implements IAccountController {

	private final ApplicationProperties appProperty;
	private final String operatorId;
	private final String accountPublicUrl;

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private ClientService clientService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	public AccountController(ApplicationProperties appProperty) {
		this.appProperty = appProperty;
		this.operatorId = this.appProperty.getCape().getOperatorId();
		this.accountPublicUrl = this.appProperty.getCape().getAccountManager().getHost();
	}

	@Operation(summary = "Get running Cape version")
	@GetMapping(value = "/version", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getCapeVersion() throws JsonProcessingException {

		Map<String, String> result = new HashMap<String, String>(1);
		result.put("version", this.appProperty.getCape().getVersion());

		return ResponseEntity.ok(objectMapper.writeValueAsString(result));
	}

	@Operation(summary = "Create a new CaPe Account.", tags = { "Account" }, responses = {
			@ApiResponse(description = "Returns 201 Created with the created Account.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class))) })
	@Override
	@PostMapping(value = "/accounts")
	public ResponseEntity<Account> createAccount(@RequestBody @Valid Account account) throws JOSEException {

		/*
		 * Create a new RSA KeyPair for Account
		 */

		account.set_id(ObjectId.get());
		cryptoService.createAccountKeyPair(account);

		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
		account.setActivated(true);
		account.setCreated(now);
		account.setModified(now);
		account.setServiceLinkRecords(new ArrayList<ServiceLinkRecordDoubleSigned>(0));
		Account storedAccount = accountRepo.insert(account);

		try {
			clientService.callCreateAuditLog(storedAccount.getUsername());
			clientService.callAddEventLog(new AccountEventLog(ZonedDateTime.now(), EventType.ACCOUNT,
					storedAccount.getUsername(), "Account with username: " + storedAccount.getUsername() + " created",
					AccountActionType.CREATE));
		} catch (Exception e) {
			accountRepo.delete(storedAccount);
			throw e;
		}

		return ResponseEntity.created(URI.create(accountPublicUrl + "/accounts/" + storedAccount.getUsername()))
				.body(storedAccount);

	}

	@Operation(summary = "Get CaPe Account by Account Id or username.", tags = { "Account" }, responses = {
			@ApiResponse(description = "Returns the requested Account.", responseCode = "200") })
	@Override
	@GetMapping(value = "/accounts/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Account> getAccount(@PathVariable String accountId) throws AccountNotFoundException {

		Account account = accountRepo.findBy_idOrUsername(accountId, accountId)
				.orElseThrow(() -> new AccountNotFoundException("No Account found with Id: " + accountId));

		return ResponseEntity.ok(account);

	}

	@Operation(summary = "Delete CaPe Account by Id or Username.", tags = { "Account" }, responses = {
			@ApiResponse(description = "Returns No Content.", responseCode = "204") })
	@Override
	@DeleteMapping(value = "/accounts/{accountId}")
	public ResponseEntity<Account> deleteAccount(@PathVariable String accountId)
			throws AccountNotFoundException, AccountManagerException, ServiceDescriptionNotFoundException {

		Account existingAccount = this.getAccount(accountId).getBody();

		List<SurrogateIdServiceIdRecord> accountSurrogateIdServiceIdRecords = existingAccount.getServiceLinkRecords()
				.stream().map(slr -> new SurrogateIdServiceIdRecord(slr.getPayload().getSurrogateId(),
						slr.getPayload().getServiceId()))
				.collect(Collectors.toList());

		clientService.callDeleteUserSurrogateLinkFromSDKs(accountSurrogateIdServiceIdRecords);

		if (accountRepo.deleteAccountBy_idOrUsername(accountId, accountId) == 0L)
			throw new AccountManagerException("There was an error while deleting Account: " + accountId);

		clientService.callDeleteLinkingSessions(accountId);
		clientService.callDeleteAuditLog(accountId);
		clientService.callDeleteConsentRecords(accountId);

		return ResponseEntity.noContent().build();

	}

	@Operation(summary = "Update CaPe Account basic fields (accountInfo, language, notification) by Account Id or username", tags = {
			"Account" }, responses = {
					@ApiResponse(description = "Returns the updated Account.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountInfo.class))) })
	@Override
	@PutMapping(value = "/accounts/{accountId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Account> updateAccount(@PathVariable String accountId, @RequestBody @Valid Account account)
			throws AccountNotFoundException {

		Account result = accountRepo.updateAccount(accountId, account)
				.orElseThrow(() -> new AccountNotFoundException("No Account found with Id: " + accountId));

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Export CaPe Account by Account Id or username.", tags = { "Account" }, responses = {
			@ApiResponse(description = "Returns the exported Account.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountExport.class))) })
	@Override
	@GetMapping(value = "/accounts/{accountId}/export", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountExport> exportAccount(@PathVariable String accountId) throws AccountNotFoundException,
			AccountManagerException, ServiceDescriptionNotFoundException, AuditLogNotFoundException {

		Account account = accountRepo.findBy_idOrUsername(accountId, accountId)
				.orElseThrow(() -> new AccountNotFoundException("No Account found with Id: " + accountId));

		ConsentRecordSigned[] consentRecords = clientService.callGetConsentRecords(accountId);

		AuditLog auditLog = clientService.callGetAuditLog(accountId);

		EventLog[] eventLogs = clientService.callGetEventLogs(accountId);

		AccountExport result = new AccountExport(account.getAccountInfo(),
				account.getServiceLinkRecords().stream().toArray(ServiceLinkRecordDoubleSigned[]::new), consentRecords,
				eventLogs, auditLog);

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Export and download as JSON file the CaPe Account by Account Id or username.", tags = {
			"Account" }, responses = {
					@ApiResponse(description = "Returns the file with exported Account.", responseCode = "200") })
	@Override
	@GetMapping(value = "/accounts/{accountId}/export/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] exportAndDownloadAccount(@PathVariable String accountId)
			throws AccountNotFoundException, AccountManagerException, ServiceDescriptionNotFoundException,
			AuditLogNotFoundException, JsonProcessingException {

		Account account = accountRepo.findBy_idOrUsername(accountId, accountId)
				.orElseThrow(() -> new AccountNotFoundException("No Account found with Id: " + accountId));

		ConsentRecordSigned[] consentRecords = clientService.callGetConsentRecords(accountId);

		AuditLog auditLog = clientService.callGetAuditLog(accountId);

		EventLog[] eventLogs = clientService.callGetEventLogs(accountId);

		AccountExport result = new AccountExport(account.getAccountInfo(),
				account.getServiceLinkRecords().stream().toArray(ServiceLinkRecordDoubleSigned[]::new), consentRecords,
				eventLogs, auditLog);

		return objectMapper.writeValueAsBytes(result);
	}

	@Operation(summary = "Return CaPe Account info by Account Id or username.", tags = { "Account" }, responses = {
			@ApiResponse(description = "Returns the CaPe Account info.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountInfo.class))) })
	@Override
	@GetMapping(value = "/accounts/{accountId}/info", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountInfo> getAccountInfo(@PathVariable String accountId) throws AccountNotFoundException {

		AccountInfoOnly accountInfoProxy = accountRepo.getAccountInfoBy_idOrUsername(accountId, accountId)
				.orElseThrow(() -> new AccountNotFoundException("No Account found with Id: " + accountId));

		return ResponseEntity.ok(accountInfoProxy.getAccountInfo());

	}

	@Operation(summary = "Update CaPe Account Info by Account Id or username", tags = { "Account" }, responses = {
			@ApiResponse(description = "Returns the updated Account info.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountInfo.class))) })
	@Override
	@PutMapping(value = "/accounts/{accountId}/info", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountInfo> updateAccountInfo(@PathVariable String accountId,
			@RequestBody @Valid AccountInfo accountInfo) throws AccountNotFoundException {

		AccountInfo result = accountRepo.updateAccountInfo(accountId, accountInfo)
				.orElseThrow(() -> new AccountNotFoundException("No Account found with Id: " + accountId));

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Return Service Link Records by Account Id or username.", tags = {
			"Service Link Record" }, responses = {
					@ApiResponse(description = "Returns Service Link Records.", responseCode = "200") })
	@Override
	@GetMapping(value = "/accounts/{accountId}/servicelinks", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ServiceLinkRecordDoubleSigned>> getServiceLinkRecords(@PathVariable String accountId)
			throws AccountNotFoundException {

		List<ServiceLinkRecordDoubleSigned> result = accountRepo.getServiceLinksRecordsBy_idOrUsername(accountId)
				.orElseThrow(() -> new AccountNotFoundException("No Account found with Id: " + accountId));

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Return Service Link Record by Account Id or username and Slr Id.", tags = {
			"Service Link Record" }, responses = {
					@ApiResponse(description = "Returns the Service Link Record.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkRecordDoubleSigned.class))) })
	@Override
	@GetMapping(value = "/accounts/{accountId}/servicelinks/{slrId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceLinkRecordDoubleSigned> getServiceLinkRecord(@PathVariable String accountId,
			@PathVariable String slrId) throws ServiceLinkRecordNotFoundException {

		ServiceLinkRecordDoubleSigned result = accountRepo
				.getServiceLinkRecordByAccountIdOrUsernameAndSlrId(accountId, slrId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException("The Service Link Record with id: " + slrId
						+ " for account Id: " + accountId + " was not found"));

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Return Service Link Record by Account Id or username and Service Id.", tags = {
			"Service Link Record" }, responses = {
					@ApiResponse(description = "Returns the Service Link Record.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkRecordDoubleSigned.class))) })
	@GetMapping(value = "/accounts/{accountId}/services/{serviceId}/servicelink", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ServiceLinkRecordDoubleSigned> getServiceLinkByAccountIdOrUsernameAndServiceId(
			@PathVariable String accountId, @PathVariable String serviceId) throws ServiceLinkRecordNotFoundException {

		ServiceLinkRecordDoubleSigned result = accountRepo
				.getServiceLinkRecordByAccountIdOrUsernameAndServiceId(accountId, serviceId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException("The Service Link Record with Service id: "
						+ serviceId + " for account Id: " + accountId + " was not found"));

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Return Service Link Record by SurrogateId Id.", tags = {
			"Service Link Record" }, responses = {
					@ApiResponse(description = "Returns the Service Link Record.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkRecordDoubleSigned.class))) })
	@GetMapping(value = "/users/{surrogateId}/servicelink", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ServiceLinkRecordDoubleSigned> getServiceLinkRecordBySurrogateId(
			@PathVariable String surrogateId) throws ServiceLinkRecordNotFoundException {

		ServiceLinkRecordDoubleSigned result = accountRepo.getServiceLinkRecordBySurrogateId(surrogateId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException(
						"The Service Link Record with surrogateId: " + surrogateId + " was not found"));

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Return Service Link Record by SurrogateId and Service Id.", tags = {
			"Service Link Record" }, responses = {
					@ApiResponse(description = "Returns the Service Link Record.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkRecordDoubleSigned.class))) })
	@GetMapping(value = "/users/{surrogateId}/services/{serviceId}/servicelink", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ServiceLinkRecordDoubleSigned> getServiceLinkRecordBySurrogateIdAndServiceId(
			@PathVariable String surrogateId, @PathVariable String serviceId)
			throws ServiceLinkRecordNotFoundException {

		ServiceLinkRecordDoubleSigned result = accountRepo
				.getServiceLinkRecordBySurrogateIdAndServiceId(surrogateId, serviceId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException("The Service Link Record with surrogateId: "
						+ surrogateId + " and serviceId: " + serviceId + " was not found"));

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Return Service Link Record Statuses by Account Id or username and Slr Id.", tags = {
			"Service Link Record" }, responses = {
					@ApiResponse(description = "Returns the CaPe Account info.", responseCode = "200") })
	@Override
	@GetMapping(value = "/accounts/{accountId}/serviceLinks/{slrId}/statuses", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ServiceLinkStatusRecordSigned>> getServiceLinkStatusRecords(
			@PathVariable String accountId, @PathVariable String slrId) throws ServiceLinkRecordNotFoundException {

		List<ServiceLinkStatusRecordSigned> result = accountRepo
				.getServiceLinkStatusRecordsByAccountOrUsernameAndSlrId(accountId, slrId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException("The Service Link Status Record with slrId: "
						+ slrId + " and AccountId: " + accountId + " was not found"));

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Return Service Link Record Status by Account Id or username, Slr Id and Ssr Id.", tags = {
			"Service Link Record" }, responses = {
					@ApiResponse(description = "Returns the Service Link Record Status.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
	@Override
	@GetMapping(value = "/accounts/{accountId}/servicelinks/{slrId}/statuses/{ssrId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceLinkStatusRecordSigned> getServiceLinkStatusRecord(@PathVariable String accountId,
			@PathVariable String slrId, @PathVariable String ssrId) throws ServiceLinkStatusRecordNotFoundException {

		ServiceLinkStatusRecordSigned result = accountRepo
				.getServiceLinkStatusRecordByAccountIdOrUsernameAndSlrIdAndSsrId(accountId, slrId, ssrId).orElseThrow(
						() -> new ServiceLinkStatusRecordNotFoundException("The Service Link Status Record with ssrId: "
								+ ssrId + " for slrId: " + slrId + " and AccountId: " + accountId + " was not found"));

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Return the Last Service Link Record Status by Account Id or username and Slr Id.", tags = {
			"Service Link Record" }, responses = {
					@ApiResponse(description = "Returns the Last Service Link Record Status.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
	@Override
	@GetMapping(value = "/accounts/{accountId}/servicelinks/{slrId}/statuses/last", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceLinkStatusRecordSigned> getLastServiceLinkStatusRecord(@PathVariable String accountId,
			@PathVariable String slrId) throws ServiceLinkStatusRecordNotFoundException {

		ServiceLinkStatusRecordSigned result = accountRepo
				.getLastServiceLinkStatusRecordByAccountOrUsernameAndSlrId(accountId, slrId).orElseThrow(
						() -> new ServiceLinkStatusRecordNotFoundException("The Service Link Status Record for slrId: "
								+ slrId + " and AccountId: " + accountId + " was not found"));

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Return the AccountId (username) associated to the input Service Link Record Id and Surrogate Id.", tags = {
			"Service Link Record" }, responses = {
					@ApiResponse(description = "Returns the Last Service Link Record Status.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
	@Override
	@GetMapping(value = "/users/{surrogateId}/servicelink/{slrId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAccountIdFromSlrIdAndSurrogateId(@PathVariable String surrogateId,
			@PathVariable String slrId)
			throws ServiceLinkStatusRecordNotFoundException, ServiceLinkRecordNotFoundException {

		Account result = accountRepo
				.findByServiceLinkRecords_Payload_slrIdAndServiceLinkRecords_Payload_SurrogateId(slrId, surrogateId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException("The Service Link Record with id: " + slrId
						+ " and Surrogate Id: " + surrogateId + " was not found"));

		return ResponseEntity.ok(result.getUsername());

	}

}
