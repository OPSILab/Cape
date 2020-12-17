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
