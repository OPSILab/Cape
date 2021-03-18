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
package it.eng.opsi.cape.auditlogmanager.controller;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.security.auth.login.AccountNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.eng.opsi.cape.auditlogmanager.ApplicationProperties;
import it.eng.opsi.cape.auditlogmanager.EventAuditLogManager;
import it.eng.opsi.cape.auditlogmanager.model.event.EventLog;
import it.eng.opsi.cape.auditlogmanager.model.event.EventType;
import it.eng.opsi.cape.auditlogmanager.repository.EventLogRepository;
import it.eng.opsi.cape.exception.AccountAuditAlreadyPresentException;
import it.eng.opsi.cape.exception.AuditLogException;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import lombok.extern.slf4j.Slf4j;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;

@RestController
@RequestMapping("/api/v2")
@Slf4j
public class EventLogController implements IEventLogController {

	private final ApplicationProperties appProperty;
	private final String auditLogPublicUrl;

	@Autowired
	EventLogRepository eventLogsRepo;

	@Autowired
	EventAuditLogManager eventAuditManager;

	@Autowired
	public EventLogController(ApplicationProperties appProperty) {
		this.appProperty = appProperty;
		this.auditLogPublicUrl = this.appProperty.getCape().getAccountManager().getHost();
	}

	@Operation(summary = "Create a new Event Log for the Account.", tags = { "Event Log" }, responses = {
			@ApiResponse(description = "Returns 201 Created with the created Event Log.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventLog.class))) })
	@PostMapping(value = "/eventLogs")
	@Override
	public ResponseEntity<EventLog> addEventLog(@RequestBody @Valid EventLog eventLog) {

		EventLog storedEventLog = eventLogsRepo.save(eventLog);

//		new Thread(() -> {
//			try {
		try {
			eventAuditManager.auditEventLog(eventLog, eventLog.getAccountId());
		} catch (AccountNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuditLogException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}).start();

		return ResponseEntity.created(URI.create(auditLogPublicUrl + "/eventLogs/" + storedEventLog.get_id()))
				.body(storedEventLog);
	}

	@Operation(summary = "Return All Event Logs.", tags = { "Event Log" }, responses = {
			@ApiResponse(description = "Returns the Event Logs.", responseCode = "200") })
	@GetMapping(value = "/eventLogs", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<EventLog>> getAllEventLogs() {

		List<EventLog> result = eventLogsRepo.findAll();

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Delete all Event Logs for an Account Id.", tags = { "Event Log" }, responses = {
			@ApiResponse(description = "Returns No Content.", responseCode = "204") })
	@Override
	@DeleteMapping(value = "/eventLogs/accounts/{accountId}")
	public ResponseEntity<Object> deleteEventLogsByAccountId(@PathVariable String accountId) {

		eventLogsRepo.deleteEventLogByAccountId(accountId);
		// throw new AccountNotFoundException("No Account with Id: " + accountId + "
		// found");

		return ResponseEntity.noContent().build();

	}

	@Operation(summary = "Get all Event Logs for an Account Id.", tags = { "Event Log" }, responses = {
			@ApiResponse(description = "Returns the Event Logs.", responseCode = "200") })
	@GetMapping(value = "/eventLogs/accounts/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<EventLog>> getEventLogsByAccountId(@PathVariable String accountId,
			@RequestParam(required = false) EventType type, @RequestParam(required = false) List<LegalBasis> legalBasis,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate,
			@RequestParam(required = false) List<ProcessingCategory> processingCategories) {

		Optional<List<EventLog>> events = null;

//		if (ServiceUtils.callExistsAccount(accountId)) {

		if (legalBasis != null || startDate != null || endDate != null || processingCategories != null)
			events = eventLogsRepo.findByAccountIdAndQuery(accountId, legalBasis, startDate, endDate,
					processingCategories);
		else if (type != null)
			events = eventLogsRepo.findByAccountIdAndType(accountId, type);
		else
			events = eventLogsRepo.findByAccountId(accountId);

		return ResponseEntity.ok(events.get());

	}

}
