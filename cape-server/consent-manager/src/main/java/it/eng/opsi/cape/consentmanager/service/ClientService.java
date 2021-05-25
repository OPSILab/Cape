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
package it.eng.opsi.cape.consentmanager.service;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.nimbusds.jose.util.Base64URL;

import it.eng.opsi.cape.consentmanager.ApplicationProperties;
import it.eng.opsi.cape.consentmanager.model.AuthorisationTokenPayload;
import it.eng.opsi.cape.consentmanager.model.AuthorisationTokenResponse;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordPayload;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordSigned;
import it.eng.opsi.cape.consentmanager.model.ConsentStatusRecordPayload;
import it.eng.opsi.cape.consentmanager.model.ConsentStatusRecordSigned;
import it.eng.opsi.cape.consentmanager.model.ThirdPartyReuseConsentSignRequest;
import it.eng.opsi.cape.consentmanager.model.ThirdPartyReuseConsentSignResponse;
import it.eng.opsi.cape.consentmanager.model.WithinServiceConsentSignRequest;
import it.eng.opsi.cape.consentmanager.model.WithinServiceConsentSignResponse;
import it.eng.opsi.cape.consentmanager.model.audit.EventLog;
import it.eng.opsi.cape.consentmanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.ConsentManagerException;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;

@Service
public class ClientService {

	private final ApplicationProperties appProperty;

	private final String serviceRegistryHost;
	private final String serviceManagerHost;
	private final String accountManagerHost;
	private final String auditLogManagerHost;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	public ClientService(ApplicationProperties appProperty) {

		this.appProperty = appProperty;
		serviceRegistryHost = this.appProperty.getCape().getServiceRegistry().getHost();
		auditLogManagerHost = this.appProperty.getCape().getAuditLogManager().getHost();
		accountManagerHost = this.appProperty.getCape().getAccountManager().getHost();
		serviceManagerHost = this.appProperty.getCape().getServiceManager().getHost();
	}

//	public Object getIdmUserDetail(String token) {
//
//		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
//		return restTemplate.getForObject(UriComponentsBuilder
//				.fromHttpUrl(idm.getHost() + "/user")
//				.queryParam("access_token", token).toUriString(), Object.class);
//
//	}

	public ServiceEntry getServiceDescriptionFromRegistry(String serviceId)
			throws ConsentManagerException, ServiceDescriptionNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ServiceEntry> response = restTemplate.getForEntity(serviceRegistryHost + "/api/v2/services/{id}",
				ServiceEntry.class, serviceId);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.is2xxSuccessful())
			return response.getBody();

		else if (responseStatus == HttpStatus.NOT_FOUND) {
			throw new ServiceDescriptionNotFoundException("The service with Id: " + serviceId + "was not found");
		} else
			throw new ConsentManagerException(
					"There was an error while retrieving Service Description from Service Registry");

	}

	public void callAddEventLog(EventLog event) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		try {
			restTemplate.exchange(RequestEntity.post(URI.create(auditLogManagerHost + "/api/v2/eventLogs")).body(event),
					String.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String callGetAccountIdFromSlrIdAndSurrogateId(String slrId, String surrogateId)
			throws AccountNotFoundException, ConsentManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<String> response = restTemplate.getForEntity(
				accountManagerHost + "/api/v2/users/{surrogateId}/servicelink/{slrId}", String.class, surrogateId,
				slrId);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.is2xxSuccessful())
			return response.getBody();
		else if (responseStatus == HttpStatus.NOT_FOUND) {
			throw new AccountNotFoundException(
					"No account found associated to Slr Id: " + slrId + " and surrogate Id: " + surrogateId);
		} else
			throw new ConsentManagerException("There was an error while retrieving Account id from Account Manager");
	}

	public ServiceLinkRecordDoubleSigned callGetServiceLinkRecordBySurrogateIdAndServiceId(String surrogateId,
			String serviceId) throws ConsentManagerException, ServiceLinkRecordNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ServiceLinkRecordDoubleSigned> response = null;

		response = restTemplate.getForEntity(UriComponentsBuilder
				.fromHttpUrl(accountManagerHost + "/api/v2/users/{surrogateId}/services/{serviceId}/servicelink")
				.build(surrogateId, serviceId), ServiceLinkRecordDoubleSigned.class);

		HttpStatus status = response.getStatusCode();

		if (status.equals(HttpStatus.NOT_FOUND))
			throw new ServiceLinkRecordNotFoundException("No Service Link Record was found for surrogateId:"
					+ surrogateId + " and Service Id: " + serviceId);
		else if (!status.is2xxSuccessful())
			throw new ConsentManagerException("There was an error while getting the Service Link Record");

		return response.getBody();
	}

	public ServiceLinkRecordDoubleSigned callGetServiceLinkRecordBySurrogateId(String surrogateId)
			throws ConsentManagerException, ServiceLinkRecordNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ServiceLinkRecordDoubleSigned> response = null;

		response = restTemplate.getForEntity(UriComponentsBuilder
				.fromHttpUrl(accountManagerHost + "/api/v2/users/{surrogateId}/servicelink").build(surrogateId),
				ServiceLinkRecordDoubleSigned.class);

		HttpStatus status = response.getStatusCode();

		if (status.equals(HttpStatus.NOT_FOUND))
			throw new ServiceLinkRecordNotFoundException(
					"No Service Link Record was found for surrogateId:" + surrogateId);
		else if (!status.is2xxSuccessful())
			throw new ConsentManagerException("There was an error while getting the Service Link Record");

		return response.getBody();
	}

	public ServiceLinkRecordDoubleSigned callGetServiceLinkRecordByAccountIdAndServiceId(String accountId,
			String serviceId) throws ConsentManagerException, ServiceLinkRecordNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ServiceLinkRecordDoubleSigned> response = null;

		response = restTemplate.getForEntity(UriComponentsBuilder
				.fromHttpUrl(accountManagerHost + "/api/v2/accounts/{accountId}/services/{serviceId}/servicelink")
				.build(accountId, serviceId), ServiceLinkRecordDoubleSigned.class);

		HttpStatus status = response.getStatusCode();

		if (status.equals(HttpStatus.NOT_FOUND))
			throw new ServiceLinkRecordNotFoundException(
					"No Service Link Record was found for Account Id:" + accountId + " and Service Id: " + serviceId);
		else if (!status.is2xxSuccessful())
			throw new ConsentManagerException("There was an error while getting the Service Link Record");

		return response.getBody();
	}

	public ServiceLinkRecordDoubleSigned callGetServiceLinkRecordByAccountIdAndSlrId(String accountId, String slrId)
			throws ConsentManagerException, ServiceLinkRecordNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ServiceLinkRecordDoubleSigned> response = null;

		response = restTemplate.getForEntity(UriComponentsBuilder
				.fromHttpUrl(accountManagerHost + "/api/v2//accounts/{accountId}/servicelinks/{slrId}")
				.build(accountId, slrId), ServiceLinkRecordDoubleSigned.class);

		HttpStatus status = response.getStatusCode();

		if (status.equals(HttpStatus.NOT_FOUND))
			throw new ServiceLinkRecordNotFoundException(
					"No Service Link Record was found for Account Id:" + accountId + " and Slr Id: " + slrId);
		else if (!status.is2xxSuccessful())
			throw new ConsentManagerException("There was an error while getting the Service Link Record");

		return response.getBody();
	}

	public WithinServiceConsentSignResponse callSignWithinServiceConsent(String surrogateId, String slrId,
			ConsentRecordPayload crPayload, ConsentStatusRecordPayload csrPayload,
			List<ConsentStatusRecordSigned> csrList) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<WithinServiceConsentSignResponse> response = restTemplate
				.postForEntity(
						UriComponentsBuilder
								.fromHttpUrl(accountManagerHost
										+ "/api/v2/users/{surrogate_id}/servicelinks/{link_id}/consents")
								.build(surrogateId, slrId),
						WithinServiceConsentSignRequest.builder().crPayload(crPayload).csrPayload(csrPayload)
								.csrList(csrList).build(),
						WithinServiceConsentSignResponse.class);

		return response.getBody();

	}

	public ThirdPartyReuseConsentSignResponse callSignThirdPartyReuseConsent(String surrogateId, String sinkSlrId,
			String sourceSlrId, ConsentRecordPayload sinkCrPayload, ConsentStatusRecordPayload sinkCsrPayload,
			ConsentRecordPayload sourceCrPayload, ConsentStatusRecordPayload sourceCsrPayload,
			List<ConsentStatusRecordSigned> sinkCsrList, List<ConsentStatusRecordSigned> sourceCsrList) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<ThirdPartyReuseConsentSignResponse> response = restTemplate.postForEntity(
				UriComponentsBuilder
						.fromHttpUrl(accountManagerHost
								+ "/api/v2/users/{surrogate_id}/servicelinks/{sink_link_id}/{source_link_id}/consents")
						.build(surrogateId, sinkSlrId, sourceSlrId),
				ThirdPartyReuseConsentSignRequest.builder()
						.sink(WithinServiceConsentSignRequest.builder().crPayload(sinkCrPayload)
								.csrPayload(sinkCsrPayload).csrList(sinkCsrList).build())
						.source(WithinServiceConsentSignRequest.builder().crPayload(sourceCrPayload)
								.csrPayload(sourceCsrPayload).csrList(sourceCsrList).build())
						.build(),
				ThirdPartyReuseConsentSignResponse.class);

		return response.getBody();

	}

	public ConsentStatusRecordSigned callSignConsentStatusRecord(String accountId, String slrId,
			ConsentStatusRecordPayload csrPayload) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<ConsentStatusRecordSigned> response = restTemplate.postForEntity(UriComponentsBuilder
				.fromHttpUrl(
						accountManagerHost + "/api/v2/accounts/{account_id}/servicelinks/{slrId}/consents/statuses")
				.build(accountId, slrId), csrPayload, ConsentStatusRecordSigned.class);

		return response.getBody();

	}

	public Base64URL callSignConsentStatusRecordsList(String accountId, String slrId,
			List<ConsentStatusRecordSigned> csrList) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<Base64URL> response = restTemplate.postForEntity(UriComponentsBuilder
				.fromHttpUrl(accountManagerHost
						+ "/api/v2/accounts/{account_id}/servicelinks/{slrId}/consents/statuses/list")
				.build(accountId, slrId), csrList, Base64URL.class);

		return response.getBody();

	}

	/*
	 * Use the notification to SDK successful boolean to discriminate whether to
	 * force storing CR at the Service
	 */
	public String sendConsentRecordToService(String serviceDomainUrl, ConsentRecordSigned signedCr) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<String> response = restTemplate.exchange(RequestEntity
				.post(UriComponentsBuilder.fromHttpUrl(serviceDomainUrl + "/api/v2/consents").build().toUri())
				.body(signedCr), String.class);
		return response.getBody();
	}

	/*
	 * Use the notification to SDK successful boolean to discriminate whether to
	 * force storing CR and CSR at the Service -> SDK will not check CSR chain
	 */
	public String sendUpdatedConsentRecordToService(String serviceDomainUrl, ConsentRecordSigned signedCr) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<String> response = restTemplate
				.exchange(
						RequestEntity
								.patch(UriComponentsBuilder.fromHttpUrl(serviceDomainUrl + "/api/v2/consents/{crId}")
										.build(signedCr.getPayload().getCommonPart().getCrId()))
								.body(signedCr),
						String.class);
		return response.getBody();
	}

	public AuthorisationTokenResponse callSignAuthorisationToken(String operatorId,
			AuthorisationTokenPayload tokenPayload) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		return restTemplate.exchange(RequestEntity.post(UriComponentsBuilder
				.fromHttpUrl(serviceManagerHost + "/api/v2/operatorDescriptions/{operatorId}/signToken")
				.build(operatorId)).body(tokenPayload), AuthorisationTokenResponse.class).getBody();

	}

}
