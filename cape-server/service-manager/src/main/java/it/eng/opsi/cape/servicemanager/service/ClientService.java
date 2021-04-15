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
package it.eng.opsi.cape.servicemanager.service;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.nimbusds.jose.util.Base64;

import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.exception.ServiceManagerException;
import it.eng.opsi.cape.servicemanager.ApplicationProperties;
import it.eng.opsi.cape.servicemanager.model.ServicePopKey;
import it.eng.opsi.cape.servicemanager.model.audit.EventLog;
import it.eng.opsi.cape.servicemanager.model.consenting.ChangeConsentStatusRequest;
import it.eng.opsi.cape.servicemanager.model.consenting.ChangeConsentStatusRequest.ChangeConsentStatusRequestFrom;
import it.eng.opsi.cape.servicemanager.model.consenting.ConsentRecordPayload;
import it.eng.opsi.cape.servicemanager.model.consenting.ConsentRecordRoleEnum;
import it.eng.opsi.cape.servicemanager.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.servicemanager.model.consenting.ConsentRecordSignedPair;
import it.eng.opsi.cape.servicemanager.model.consenting.ConsentRecordSinkRoleSpecificPart;
import it.eng.opsi.cape.servicemanager.model.consenting.ConsentRecordStatusEnum;
import it.eng.opsi.cape.servicemanager.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkRecordPayload;
import it.eng.opsi.cape.servicemanager.model.linking.ChangeSlrStatusRequestFrom;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkRecordAccountSigned;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkStatusRecordPayload;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.servicemanager.model.linking.account.AccountSignSlrRequest;
import it.eng.opsi.cape.servicemanager.model.linking.account.AccountSignSlrResponse;
import it.eng.opsi.cape.servicemanager.model.linking.account.ServiceLinkInitResponse;
import it.eng.opsi.cape.servicemanager.model.linking.account.SinkServiceLinkInitRequest;
import it.eng.opsi.cape.servicemanager.model.linking.account.SourceServiceLinkInitRequest;
import it.eng.opsi.cape.servicemanager.model.linking.account.FinalStoreSlrRequest;
import it.eng.opsi.cape.servicemanager.model.linking.account.FinalStoreSlrResponse;
import it.eng.opsi.cape.servicemanager.model.linking.service.ServiceSignSlrRequest;
import it.eng.opsi.cape.servicemanager.model.linking.service.ServiceSignSlrResponse;
import it.eng.opsi.cape.servicemanager.model.linking.service.StartLinkingRequest;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;

@Service
public class ClientService {

	private final ApplicationProperties appProperty;

	@Autowired
	private ApplicationContext applicationContext;

	private final String serviceRegistryHost;
	private final String auditLogManagerHost;
	private final String accountManagerHost;
	private final String consentManagerHost;
	private final String operatorId;

	@Autowired
	public ClientService(ApplicationProperties appProperty) {

		this.appProperty = appProperty;
		serviceRegistryHost = this.appProperty.getCape().getServiceRegistry().getHost();
		auditLogManagerHost = this.appProperty.getCape().getAuditLogManager().getHost();
		accountManagerHost = this.appProperty.getCape().getAccountManager().getHost();
		consentManagerHost = this.appProperty.getCape().getConsentManager().getHost();
		operatorId = this.appProperty.getCape().getOperatorId();

	}

	public ServiceEntry getServiceDescriptionFromRegistry(String serviceId)
			throws ServiceManagerException, ServiceDescriptionNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ServiceEntry> response = restTemplate.getForEntity(serviceRegistryHost + "/api/v2/services/{id}",
				ServiceEntry.class, serviceId);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.is2xxSuccessful())
			return response.getBody();

		else if (responseStatus == HttpStatus.NOT_FOUND) {
			throw new ServiceDescriptionNotFoundException("The service with Id: " + serviceId + "was not found");
		} else
			throw new ServiceManagerException(
					"There was an error while retrieving Service Description from Service Manager");

	}

	/*
	 * Start Service Linking (Linking started from Service and then redirected to
	 * Operator Login) Service Manager -> Service SDK
	 */
	public FinalStoreSlrResponse callStartServiceLinking(String code, String surrogateId, String operatorId,
			String serviceId, String returnUrl, String serviceLinkingUri) throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<FinalStoreSlrResponse> linkingResponse = restTemplate
				.exchange(
						RequestEntity.post(URI.create(serviceLinkingUri))
								.body(StartLinkingRequest.builder().code(code).surrogateId(surrogateId)
										.operatorId(operatorId).serviceId(serviceId).returnUrl(returnUrl).build()),
						FinalStoreSlrResponse.class);

		HttpStatus status = linkingResponse.getStatusCode();

		if (!status.is2xxSuccessful())
			throw new ServiceManagerException("There was an error while starting Service Linking at Service");

		return linkingResponse.getBody();

	}

	@Async
	public CompletableFuture<Void> callAddEventLog(EventLog event) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		try {
			restTemplate.exchange(RequestEntity.post(URI.create(auditLogManagerHost + "/api/v2/eventLogs")).body(event),
					String.class);
			return CompletableFuture.completedFuture(null);
		} catch (Exception e) {
			e.printStackTrace();
			return CompletableFuture.completedFuture(null);
		}
	}

	/*
	 * callStoreSinkSlrId Service Manager ---> Account Manager
	 */
	public ServiceLinkInitResponse callStoreSinkSlrId(String accountId, String code, String slrId, String serviceId,
			String surrogateId, ServicePopKey popKey) throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<ServiceLinkInitResponse> response = restTemplate
				.exchange(
						RequestEntity
								.post(UriComponentsBuilder
										.fromHttpUrl(accountManagerHost
												+ "/api/v2/accounts/{account_id}/servicelinks/init/sink")
										.build(accountId))
								.body(SinkServiceLinkInitRequest.builder().code(code).slrId(slrId).serviceId(serviceId)
										.surrogateId(surrogateId).popKey(popKey).build()),
						ServiceLinkInitResponse.class);

		HttpStatus status = response.getStatusCode();
		if (!status.is2xxSuccessful())
			throw new ServiceManagerException("There was an error while storing Sink SLR ID");

		return response.getBody();
	}

	/*
	 * callSourceSinkSlrId Service Manager ---> Account Manager
	 */
	public ServiceLinkInitResponse callStoreSourceSlrId(String accountId, String code, String slrId, String serviceId,
			String surrogateId) throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<ServiceLinkInitResponse> response = restTemplate
				.exchange(
						RequestEntity
								.post(UriComponentsBuilder
										.fromHttpUrl(accountManagerHost
												+ "/api/v2/accounts/{account_id}/servicelinks/init/source")
										.build(accountId))
								.body(SourceServiceLinkInitRequest.builder().code(code).slrId(slrId)
										.serviceId(serviceId).surrogateId(surrogateId).build()),
						ServiceLinkInitResponse.class);

		HttpStatus status = response.getStatusCode();
		if (!status.is2xxSuccessful())
			throw new ServiceManagerException("There was an error while storing Source SLR ID");

		return response.getBody();
	}

	/*
	 * callAccountSignSlr Service Manager ---> Account Manager
	 */
	public AccountSignSlrResponse callAccountSignSlr(String accountId, String slrId,
			ServiceLinkRecordPayload partialSlrPayload, String code) throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<AccountSignSlrResponse> response = restTemplate
				.exchange(
						RequestEntity
								.patch(UriComponentsBuilder
										.fromHttpUrl(accountManagerHost
												+ "/api/v2/accounts/{account_id}/servicelinks/{link_id}")
										.build(accountId, slrId))
								.body(AccountSignSlrRequest.builder().code(code).partialSlrPayload(partialSlrPayload)
										.build()),
						AccountSignSlrResponse.class);

		HttpStatus status = response.getStatusCode();
		if (!status.is2xxSuccessful())
			throw new ServiceManagerException("There was an error while signing partial SLR");

		return response.getBody();
	}

	/*
	 * callServiceSignSlr Service Manager ---> Service SDK
	 */
	public ServiceSignSlrResponse callServiceSignSlr(String serviceSdkHost,
			ServiceLinkRecordAccountSigned accountSignedSlr, String surrogateId, String operatorId, String code)
			throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<ServiceSignSlrResponse> response = restTemplate.exchange(
				RequestEntity.post(URI.create(serviceSdkHost + "/api/v2/slr/slr"))
						.body(ServiceSignSlrRequest.builder().accountSignedSlr(accountSignedSlr).code(code)
								.surrogateId(surrogateId).operatorId(operatorId).build()),
				ServiceSignSlrResponse.class);

		HttpStatus status = response.getStatusCode();
		if (!status.is2xxSuccessful())
			throw new ServiceManagerException("There was an error while storing Source SLR ID");

		return response.getBody();
	}

	/*
	 * Get X509 Certificate as Base64 String from the input URI
	 */
	public Base64 getCertificateFromUri(String certificateUri) throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		String certificateString = restTemplate.getForObject(certificateUri, String.class);

		if (certificateString != null)
			return new Base64(certificateString);
		else
			throw new ServiceManagerException("There was an error while retrieving X509 certificate");

	}

	/*
	 * callStoreServiceLink Service Manager ---> Account Manager
	 */
	public ResponseEntity<FinalStoreSlrResponse> callStoreFinalSlr(String accountId, String code, String slrId,
			ServiceLinkRecordDoubleSigned slr, ServiceLinkStatusRecordPayload ssrPayload)
			throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<FinalStoreSlrResponse> response = restTemplate.exchange(
				RequestEntity
						.post(UriComponentsBuilder
								.fromHttpUrl(accountManagerHost
										+ "/api/v2/accounts/{account_id}/servicelinks/{link_id}/store/")
								.build(accountId, slrId))
						.body(FinalStoreSlrRequest.builder().slr(slr).ssr(ssrPayload).code(code).build()),
				FinalStoreSlrResponse.class);

		HttpStatus status = response.getStatusCode();
		if (!status.is2xxSuccessful())
			throw new ServiceManagerException("There was an error while storing Final Service Link Record");

		return response;
	}

	/*
	 * callSignAndStoreServiceLinkStatus Service Manager ---> Account Manager
	 */
	public ResponseEntity<ServiceLinkStatusRecordSigned> callSignAndStoreServiceLinkStatus(String accountId,
			String slrId, ServiceLinkStatusRecordPayload ssr) throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<ServiceLinkStatusRecordSigned> response = restTemplate
				.exchange(RequestEntity.post(UriComponentsBuilder
						.fromHttpUrl(
								accountManagerHost + "/api/v2/accounts/{account_id}/servicelinks/{link_id}/statuses")
						.build(accountId, slrId)).body(ssr), ServiceLinkStatusRecordSigned.class);

		HttpStatus status = response.getStatusCode();
		if (!status.is2xxSuccessful())
			throw new ServiceManagerException("There was an error while storing Service Link Status Record");

		return response;
	}

	/*
	 * callStoreSignedServiceLinkStatus Service Manager ---> Account Manager
	 */
	public ResponseEntity<ServiceLinkStatusRecordSigned> callStoreSignedServiceLinkStatus(String surrogateId,
			String slrId, ServiceLinkStatusRecordSigned ssr) throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<ServiceLinkStatusRecordSigned> response = restTemplate
				.exchange(RequestEntity.post(UriComponentsBuilder
						.fromHttpUrl(accountManagerHost
								+ "/api/v2/users/{surrogate_id}/servicelinks/{link_id}/statuses/storeOnly")
						.build(surrogateId, slrId)).body(ssr), ServiceLinkStatusRecordSigned.class);

		HttpStatus status = response.getStatusCode();
		if (!status.is2xxSuccessful())
			throw new ServiceManagerException("There was an error while storing Service Link Status Record");

		return response;
	}

	/*
	 * callchangeServiceLinkStatus Service Manager ---> Service SDK
	 */
	public ResponseEntity<String> callNotifyServiceLinkStatusChanged(String serviceSdkHost, String slrId,
			ServiceLinkStatusRecordSigned ssr) throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<String> response = restTemplate.exchange(RequestEntity
				.post(UriComponentsBuilder.fromHttpUrl(serviceSdkHost + "/api/v2/slr/{slrId}/status").build(slrId))
				.body(ssr), String.class);

		HttpStatus status = response.getStatusCode();
		if (!status.is2xxSuccessful())
			throw new ServiceManagerException(
					"There was an error while notifying Service Link Status Record to Service SDK");

		return response;
	}

	/*
	 * callGetServiceLinkRecord Service Manager ---> Account Manager
	 */
	public ResponseEntity<ServiceLinkRecordDoubleSigned> callGetServiceLinkRecord(String accountOrSurrogateId,
			String serviceId, String slrId, ChangeSlrStatusRequestFrom requestFrom)
			throws ServiceManagerException, ServiceLinkRecordNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ServiceLinkRecordDoubleSigned> response = null;

		if (requestFrom.equals(ChangeSlrStatusRequestFrom.OPERATOR))
			response = restTemplate.getForEntity(UriComponentsBuilder
					.fromHttpUrl(accountManagerHost + "/api/v2/accounts/{account_id}/servicelinks/{link_id}")
					.build(accountOrSurrogateId, slrId), ServiceLinkRecordDoubleSigned.class);

		else if (requestFrom.equals(ChangeSlrStatusRequestFrom.SERVICE))
			response = restTemplate.getForEntity(UriComponentsBuilder
					.fromHttpUrl(accountManagerHost + "/api/v2/users/{surrogateId}/services/{serviceId}/servicelink")
					.build(accountOrSurrogateId, serviceId), ServiceLinkRecordDoubleSigned.class);

		HttpStatus status = response.getStatusCode();

		if (status.equals(HttpStatus.NOT_FOUND))
			throw new ServiceLinkRecordNotFoundException("The Service Link Record with id:" + slrId
					+ " for account/surrogate Id: " + accountOrSurrogateId + " was not found");
		else if (!status.is2xxSuccessful())
			throw new ServiceManagerException("There was an error while getting the Service Link Record");

		return response;
	}

	/*
	 * callGetConsentPairsByAccountId Service Manager ---> Consent Manager
	 */
	public ResponseEntity<ConsentRecordSignedPair[]> callGetConsentPairsByAccountId(String accountId)
			throws AccountNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ConsentRecordSignedPair[]> response = null;

		response = restTemplate.getForEntity(UriComponentsBuilder
				.fromHttpUrl(consentManagerHost + "/api/v2/accounts/{accountId}/consents/pair").build(accountId),
				ConsentRecordSignedPair[].class);

		HttpStatus status = response.getStatusCode();

		if (status.equals(HttpStatus.NOT_FOUND))
			throw new AccountNotFoundException("No account found with Id: " + accountId);

		return response;
	}

	/*
	 * callGetConsentPairsByAccountId Service Manager ---> Consent Manager
	 */
	public ResponseEntity<ConsentRecordSignedPair[]> callGetConsentPairsByAccountIdAndSlrId(String accountId,
			String slrId) throws AccountNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ConsentRecordSignedPair[]> response = null;

		response = restTemplate.getForEntity(UriComponentsBuilder
				.fromHttpUrl(consentManagerHost + "/api/v2/accounts/{accountId}/servicelinks/{slrId}/consents/pair")
				.build(accountId, slrId), ConsentRecordSignedPair[].class);

		HttpStatus status = response.getStatusCode();

		if (status.equals(HttpStatus.NOT_FOUND))
			throw new AccountNotFoundException("No account found with Id: " + accountId);

		return response;
	}

	/*
	 * callGetConsentPairsByAccountId Service Manager ---> Consent Manager
	 */
	public ResponseEntity<ConsentRecordSignedPair[]> callGetConsentPairsBySurrogateIdAndSlrId(String surrogateId,
			String slrId) throws AccountNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ConsentRecordSignedPair[]> response = null;

		response = restTemplate.getForEntity(UriComponentsBuilder
				.fromHttpUrl(consentManagerHost + "/api/v2/users/{surrogateId}/servicelinks/{slrId}/consents/pair")
				.build(surrogateId, slrId), ConsentRecordSignedPair[].class);

		HttpStatus status = response.getStatusCode();

		if (status.equals(HttpStatus.NOT_FOUND))
			throw new AccountNotFoundException("No account found with Id: " + surrogateId);

		return response;
	}

	/*
	 * callNotifyConsentStatusRecordChanged Service Manager ---> Service SDK
	 */
	public ResponseEntity<String> callNotifyConsentStatusRecordChanged(String serviceSdkHost, String crId,
			ConsentStatusRecordSigned csr) throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<String> response = restTemplate.exchange(RequestEntity
				.put(UriComponentsBuilder.fromHttpUrl(serviceSdkHost + "/api/v2/cr/{crId}/status").build(crId))
				.body(csr), String.class);

		HttpStatus status = response.getStatusCode();
		if (!status.is2xxSuccessful())
			throw new ServiceManagerException(
					"There was an error while notifying Consent Status Record to Service SDK");

		return response;
	}

	/*
	 * callChangeConsentStatus Service Manager ---> Consent Manager
	 */
	public ResponseEntity<ConsentRecordSigned> callChangeConsentStatus(String accountId, String slrId,
			String crId, ConsentRecordPayload existingCrPayload, ConsentRecordStatusEnum newStatus,
			ChangeConsentStatusRequestFrom requestFrom) throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<ConsentRecordSigned> response = restTemplate
				.exchange(
						RequestEntity.post(UriComponentsBuilder
								.fromHttpUrl(consentManagerHost
										+ "/api/v2/accounts/{accountId}/servicelinks/{slrId}/consents/{crId}/statuses")
								.build(accountId, slrId, crId))
								.body(ChangeConsentStatusRequest.builder()
										.resourceSet(
												existingCrPayload.getCommonPart().getRsDescription().getResourceSet())
										.status(newStatus)
										.usageRules(existingCrPayload.getCommonPart().getRole()
												.equals(ConsentRecordRoleEnum.SINK)
														? ((ConsentRecordSinkRoleSpecificPart) existingCrPayload
																.getRoleSpecificPart()).getUsageRules()
														: null)
										.requestFrom(requestFrom).build()),
						ConsentRecordSigned.class);

		HttpStatus status = response.getStatusCode();
		if (!status.is2xxSuccessful())
			throw new ServiceManagerException(
					"There was an error while notifying Consent Status Record to Service SDK");

		return response;
	}

}
