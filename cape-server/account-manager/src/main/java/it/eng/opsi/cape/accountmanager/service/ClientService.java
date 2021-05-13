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
package it.eng.opsi.cape.accountmanager.service;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import it.eng.opsi.cape.accountmanager.ApplicationProperties;
import it.eng.opsi.cape.accountmanager.model.audit.EventLog;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.accountmanager.model.linking.LinkingSession;
import it.eng.opsi.cape.exception.AccountManagerException;
import it.eng.opsi.cape.exception.AuditLogNotFoundException;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.SessionNotFoundException;
import it.eng.opsi.cape.accountmanager.model.audit.AuditDataMapping;
import it.eng.opsi.cape.accountmanager.model.audit.AuditLog;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;

@Service
public class ClientService {

	private final ApplicationProperties appProperty;

	private final String serviceRegistryHost;
	private final String serviceManagerHost;
	private final String auditLogManagerHost;
	private final String consentManagerHost;
	private final String operatorId;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	public ClientService(ApplicationProperties appProperty) {

		this.appProperty = appProperty;
		serviceRegistryHost = this.appProperty.getCape().getServiceRegistry().getHost();
		serviceManagerHost = this.appProperty.getCape().getServiceManager().getHost();
		auditLogManagerHost = this.appProperty.getCape().getAuditLogManager().getHost();
		consentManagerHost = this.appProperty.getCape().getConsentManager().getHost();
		operatorId = this.appProperty.getCape().getOperatorId();
	}

	public ServiceEntry getServiceDescriptionFromRegistry(String serviceId)
			throws AccountManagerException, ServiceDescriptionNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ServiceEntry> response = restTemplate.getForEntity(serviceRegistryHost + "/api/v1/services/{id}",
				ServiceEntry.class, serviceId);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.is2xxSuccessful())
			return response.getBody();

		else if (responseStatus == HttpStatus.NOT_FOUND) {
			throw new ServiceDescriptionNotFoundException("The service with Id: " + serviceId + "was not found");
		} else
			throw new AccountManagerException(
					"There was an error while retrieving Service Description from Service Registry");

	}

	public LinkingSession callGetLinkingSession(String sessionCode)
			throws SessionNotFoundException, AccountManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<LinkingSession> response = restTemplate.getForEntity(
				serviceManagerHost + "/api/v2/slr/linkingSession/{sessionCode}", LinkingSession.class, sessionCode);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.is2xxSuccessful())
			return response.getBody();
		else if (responseStatus == HttpStatus.NOT_FOUND) {
			throw new SessionNotFoundException("The session with sessionCode: " + sessionCode + " was not found");
		} else
			throw new AccountManagerException(
					"There was an error while retrieving Linking Session from Service Manager");
	}

	@Async
	public CompletableFuture<Void> callAddEventLog(EventLog event) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
					RequestEntity.post(URI.create(auditLogManagerHost + "/api/v2/eventLogs")).body(event),
					String.class);
			return CompletableFuture.completedFuture(null);
		} catch (Exception e) {
			e.printStackTrace();
			return CompletableFuture.completedFuture(null);
		}
	}

	public void callCreateAuditLog(String accountId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<String> response = restTemplate.exchange(RequestEntity
				.post(URI.create(auditLogManagerHost + "/api/v2/auditLogs"))
				.body(new AuditLog(accountId, 0, 0, 0, new HashMap<String, AuditDataMapping>(),
						new HashMap<String, HashMap<String, AuditDataMapping>>(), new HashMap<String, Integer>(),
						new HashMap<String, Integer>(), new HashMap<String, HashMap<String, AuditDataMapping>>())),
				String.class);

	}

	public ConsentRecordSigned[] callGetConsentRecords(String accountId)
			throws AccountManagerException, ServiceDescriptionNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ConsentRecordSigned[]> response = restTemplate.getForEntity(
				consentManagerHost + "/api/v2/accounts/{accountId}/consents", ConsentRecordSigned[].class, accountId);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.is2xxSuccessful())
			return response.getBody();

		else
			throw new AccountManagerException(
					"There was an error while retrieving Consent Records from Consent Manager");

	}

	public AuditLog callGetAuditLog(String accountId) throws AccountManagerException, AuditLogNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<AuditLog> response = restTemplate.getForEntity(
				auditLogManagerHost + "/api/v2/auditLogs/accounts/{accountId}", AuditLog.class, accountId);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.is2xxSuccessful())
			return response.getBody();

		else if (responseStatus == HttpStatus.NOT_FOUND) {
			throw new AuditLogNotFoundException("No Audit Log found for Account Id: " + accountId);
		} else
			throw new AccountManagerException("There was an error while retrieving AuditLog from AuditLog Manager");

	}

	public void callDeleteAuditLog(String accountId) {

		try {
			RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
			restTemplate.delete(auditLogManagerHost + "/api/v2/auditLogs/accounts/{accountId}", accountId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EventLog[] callGetEventLogs(String accountId)
			throws AccountManagerException, ServiceDescriptionNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<EventLog[]> response = restTemplate.getForEntity(
				auditLogManagerHost + "/api/v2/eventLogs/accounts/{accountId}", EventLog[].class, accountId);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.is2xxSuccessful())
			return response.getBody();

		else
			throw new AccountManagerException("There was an error while retrieving EventLogs from AuditLog Manager");

	}

	public void callDeleteLinkingSessions(String accountId) {

		try {
			RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
			restTemplate.delete(serviceManagerHost + "/api/v2/slr/accounts/{accountId}/linkingSessions", accountId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void callDeleteConsentRecords(String accountId) {

		try {
			RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
			restTemplate.delete(consentManagerHost + "/api/v2/accounts/{accountId}/consents", accountId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
