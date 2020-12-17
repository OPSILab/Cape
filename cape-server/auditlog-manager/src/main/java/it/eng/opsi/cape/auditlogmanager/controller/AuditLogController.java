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
package it.eng.opsi.cape.auditlogmanager.controller;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.eng.opsi.cape.auditlogmanager.ApplicationProperties;
import it.eng.opsi.cape.auditlogmanager.model.audit.AuditDataMapping;
import it.eng.opsi.cape.auditlogmanager.model.audit.AuditLog;
import it.eng.opsi.cape.auditlogmanager.model.event.EventLog;
import it.eng.opsi.cape.auditlogmanager.repository.AuditLogRepository;
import it.eng.opsi.cape.auditlogmanager.repository.EventLogRepository;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.AuditLogNotFoundException;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.Storage.Location;
import lombok.extern.slf4j.Slf4j;

@OpenAPIDefinition(/* security = { @SecurityRequirement(name = "bearer-key") }, */ tags = {
		@Tag(name = "Audit Log", description = "Audit Log, reporting aggregated auditing statistics, triggered by incoming Event Logs (ServiceLink, Consent or Data Processing) regarding a specific Account"),
		@Tag(name = "Event Log", description = "Log of an Event (ServiceLink, Consent or Data Processing) regarding a specific Account") }, info = @Info(title = "CaPe API - AuditLog Manager", description = "CaPe APIs used to manage Events and Auditing operations", version = "2.0"))
@RestController
@RequestMapping("/api/v2")
@Slf4j
public class AuditLogController implements IAuditLogController {

	private final ApplicationProperties appProperty;
	private final String auditLogPublicUrl;

	@Autowired
	AuditLogRepository auditLogsRepo;

	@Autowired
	EventLogRepository eventLogsRepo;

	@Autowired
	public AuditLogController(ApplicationProperties appProperty) {
		this.appProperty = appProperty;
		this.auditLogPublicUrl = this.appProperty.getCape().getAccountManager().getHost();
	}

	@Operation(summary = "Create a new Audit Log for the Account.", tags = { "Audit Log" }, responses = {
			@ApiResponse(description = "Returns 201 Created with the created Audit Log.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PostMapping(value = "/auditLogs")
	@Override
	public ResponseEntity<AuditLog> createAuditLog(@RequestBody @Valid AuditLog auditLog) {

		AuditLog storedAuditLog = auditLogsRepo.save(auditLog);

		return ResponseEntity.created(URI.create(auditLogPublicUrl + "/eventLogs/" + storedAuditLog.get_id()))
				.body(storedAuditLog);

//		if (ServiceUtils.callExistsAccount(accountId)) 

	}

	@Operation(summary = "Delete the Audit Log for the Account Id in input.", tags = { "Audit Log" }, responses = {
			@ApiResponse(description = "Returns No Content.", responseCode = "204") })
	@DeleteMapping(value = "/auditLogs/accounts/{accountId}")
	@Override
	public ResponseEntity<AuditLog> deleteAuditLog(@PathVariable String accountId,
			@RequestParam(defaultValue = "true") Boolean deleteEventLogs) throws AuditLogNotFoundException {

		if (auditLogsRepo.deleteAuditLogByAccountId(accountId) == 0L)
			throw new AuditLogNotFoundException("No Audit Log found for account Id: " + accountId);

		if (deleteEventLogs)
			eventLogsRepo.deleteEventLogByAccountId(accountId);

		return ResponseEntity.noContent().build();

	}

	@Operation(summary = "Return Audit Logs for Account Id.", tags = { "Audit Log" }, responses = {
			@ApiResponse(description = "Returns the Audit Logs.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@GetMapping(value = "/auditLogs/accounts/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> getAuditByAccountId(@PathVariable String accountId)
			throws AuditLogNotFoundException {

		AuditLog result = auditLogsRepo.findByAccountId(accountId)
				.orElseThrow(() -> new AuditLogNotFoundException("No Audit Log found for Account Id: " + accountId));

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Increment by one the Audit Log - linked Services count of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/linkedServices/inc", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> incLinkedServiceToAuditLog(String accountId) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.incLinkedService(accountId);

		return ResponseEntity.ok(updatedAuditLog.get());

	}

	@Operation(summary = "Decrement by one the Audit Log - linked Services count of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/linkedServices/dec", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> decLinkedServiceToAuditLog(String accountId) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.decLinkedService(accountId);

		return ResponseEntity.ok(updatedAuditLog.get());
	}

	@Operation(summary = "Increment by one the Audit Log - Given Consents count of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/givenConsents/inc", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> incGivenConsentsToAuditLog(String accountId) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.incGivenConsent(accountId);

		return ResponseEntity.ok(updatedAuditLog.get());
	}

	@Operation(summary = "Decrement by one the Audit Log - Given Consents count of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/givenConsents/dec", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> decGivenConsentsToAuditLog(String accountId) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.decGivenConsent(accountId);

		return ResponseEntity.ok(updatedAuditLog.get());
	}

	@Operation(summary = "Increment by one the Audit Log - Processed Personal Data count of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/processedPersonalData/inc", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> incProcessedPersonalDataToAuditLog(String accountId) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.incProcessedPersonalData(accountId);

		return ResponseEntity.ok(updatedAuditLog.get());
	}

	@Operation(summary = "Decrement by one the Audit Log - Processed Personal Data count of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/processedPersonalData/dec", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> decProcessedPersonalDataToAuditLog(String accountId) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.decProcessedPersonalData(accountId);

		return ResponseEntity.ok(updatedAuditLog.get());
	}

	@Operation(summary = "Increment by one the Audit Log count of a specific Personal Data Concept, of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/processedPersonalData/{conceptId}/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> addProcessedPersonalDataToAuditLog(String accountId, String conceptId) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.addProcessedPersonalData(accountId,
				new AuditDataMapping(conceptId));

		return ResponseEntity.ok(updatedAuditLog.get());
	}

	@Operation(summary = "Decrement by one the Audit Log count of a specific Personal Data Concept, of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/processedPersonalData/{conceptId}/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> subtractProcessedPersonalDataToAuditLog(String accountId, String conceptId) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.subtractProcessedPersonalData(accountId,
				new AuditDataMapping(conceptId));

		return ResponseEntity.ok(updatedAuditLog.get());
	}

	@Operation(summary = "Increment by one the Audit Log - Processing Category of a specific Personal Data Concept, of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/processingCategoryPersonalData/{processingCategory}/{conceptId}/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> addProcessingCategoryPersonalDataToAuditLog(String accountId,
			ProcessingCategory processingCategory, String conceptId) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.addProcessingPersonalData(accountId, processingCategory,
				new AuditDataMapping(conceptId));

		return ResponseEntity.ok(updatedAuditLog.get());

	}

	@Operation(summary = "Decrement by one the Audit Log - Processing Category of a specific Personal Data Concept, of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/processingCategoryPersonalData/{processingCategory}/{conceptId}/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> subtractProcessingCategoryPersonalDataToAuditLog(String accountId,
			ProcessingCategory processingCategory, String conceptId) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.subtractProcessingPersonalData(accountId, processingCategory,
				new AuditDataMapping(conceptId));

		return ResponseEntity.ok(updatedAuditLog.get());
	}

	@Operation(summary = "Increment by one the Audit Log count of a specific Purpose Category, of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/purposeCategoryPersonalData/{purposeCategory}/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> addPurposeCategoryPersonalDataToAuditLog(String accountId, String purposeCategory) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.addPurposeCategory(accountId, purposeCategory);

		return ResponseEntity.ok(updatedAuditLog.get());

	}

	@Operation(summary = "Decrement by one the Audit Log count of a specific Purpose Category, of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/purposeCategoryPersonalData/{purposeCategory}/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> subtractPurposeCategoryPersonalDataToAuditLog(String accountId,
			String purposeCategory) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.subtractPurposeCategory(accountId, purposeCategory);

		return ResponseEntity.ok(updatedAuditLog.get());
	}

	@Operation(summary = "Increment by one the Audit Log count of a specific Legal Basis, of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/legalBasis/{legalBasis}/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> incLegalBasisToAuditLog(String accountId, LegalBasis legalBasis) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.addLegalBasis(accountId, legalBasis);

		return ResponseEntity.ok(updatedAuditLog.get());
	}

	@Operation(summary = "Decrement by one the Audit Log count of a specific Legal Basis, of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/legalBasis/{legalBasis}/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> decLegalBasisToAuditLog(String accountId, LegalBasis legalBasis) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.subtractLegalBasis(accountId, legalBasis);

		return ResponseEntity.ok(updatedAuditLog.get());
	}

	@Operation(summary = "Increment by one the Audit Log Storage count of a specific Personal Data Concept, of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/storageLocationPersonalData/{location}/{conceptId}/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> addStoragePersonalDataToAuditLog(String accountId, Location storage,
			String conceptId) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.addStoragePersonalData(accountId, storage,
				new AuditDataMapping(conceptId));

		return ResponseEntity.ok(updatedAuditLog.get());
	}

	@Operation(summary = "Decrement by one the Audit Log Storage count of a specific Personal Data Concept, of specified Account Id", tags = {
			"Audit Log" }, responses = {
					@ApiResponse(description = "Returns the updated Audit Log.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLog.class))) })
	@PutMapping(value = "/auditLogs/accounts/{accountId}/storageLocationPersonalData/{location}/{conceptId}/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<AuditLog> substactStoragePersonalDataToAuditLog(String accountId, Location storage,
			String conceptId) {

		Optional<AuditLog> updatedAuditLog = auditLogsRepo.subtractStoragePersonalData(accountId, storage,
				new AuditDataMapping(conceptId));

		return ResponseEntity.ok(updatedAuditLog.get());
	}

}
