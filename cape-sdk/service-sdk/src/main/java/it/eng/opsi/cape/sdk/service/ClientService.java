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
package it.eng.opsi.cape.sdk.service;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import it.eng.opsi.cape.exception.CapeSdkManagerException;
import it.eng.opsi.cape.exception.OperatorDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceManagerException;
import it.eng.opsi.cape.exception.SessionNotFoundException;
import it.eng.opsi.cape.sdk.ApplicationProperties;
import it.eng.opsi.cape.sdk.model.OperatorDescription;
import it.eng.opsi.cape.sdk.model.ServicePopKey;
import it.eng.opsi.cape.sdk.model.consenting.ChangeConsentStatusRequest;
import it.eng.opsi.cape.sdk.model.consenting.ConsentForm;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSignedPair;
import it.eng.opsi.cape.sdk.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.sdk.model.datatransfer.AuthorisationTokenResponse;
import it.eng.opsi.cape.sdk.model.datatransfer.DataTransferRequest;
import it.eng.opsi.cape.sdk.model.datatransfer.DataTransferResponse;
import it.eng.opsi.cape.sdk.model.linking.ContinueLinkingRequest;
import it.eng.opsi.cape.sdk.model.linking.ContinueSinkLinkingRequest;
import it.eng.opsi.cape.sdk.model.linking.FinalLinkingResponse;
import it.eng.opsi.cape.sdk.model.linking.LinkingSession;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;

@Service
public class ClientService {

	private ApplicationProperties appProperty;
	private final String serviceRegistryHost;
	private final String serviceManagerHost;
	private final String consentManagerHost;
	private final ApplicationProperties.Idm idm;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	public ClientService(ApplicationProperties appProperty) {

		this.appProperty = appProperty;
		serviceRegistryHost = this.appProperty.getCape().getServiceRegistry().getHost();
		serviceManagerHost = this.appProperty.getCape().getServiceManager().getHost();
		consentManagerHost = this.appProperty.getCape().getConsentManager().getHost();
		idm = this.appProperty.getIdm();
	}

	public Object getIdmUserDetail(String token) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate.getForObject(UriComponentsBuilder.fromHttpUrl(idm.getHost() + "/user")
				.queryParam("access_token", token).toUriString(), Object.class);

	}

	public Object postCodeForToken(String grantType, String redirectUri, String code) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("grant_type", grantType);
		body.add("redirect_uri", redirectUri);
		body.add("code", code);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBasicAuth(idm.getClientId(), idm.getClientSecret());

		return restTemplate.exchange(
				RequestEntity.post(UriComponentsBuilder.fromHttpUrl(idm.getHost() + "/oauth2/token").build().toUri())
						.headers(headers).body(body),
				Object.class);

	}

	public Object externalLogout(String clientId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate
				.exchange(RequestEntity.delete(UriComponentsBuilder.fromHttpUrl(idm.getHost() + "/auth/external_logout")
						.queryParam("clientId", clientId).build().toUri()).build(), Object.class);

	}

	public OperatorDescription fetchOperatorDescription(String operatorId)
			throws ServiceManagerException, OperatorDescriptionNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<OperatorDescription> response = restTemplate.getForEntity(
				serviceManagerHost + "/api/v2/operatorDescriptions/{operatorId}", OperatorDescription.class,
				operatorId);
		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.is2xxSuccessful())
			return response.getBody();

		else if (responseStatus == HttpStatus.NOT_FOUND) {
			throw new OperatorDescriptionNotFoundException("The operator with Id: " + operatorId + "was not found");
		} else
			throw new ServiceManagerException(
					"There was an error while retrieving Service Description from Service Manager");

	}

	public ServiceEntry getServiceDescriptionFromRegistry(String serviceId, Boolean onlyRegistered)
			throws ServiceManagerException, ServiceDescriptionNotFoundException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ServiceEntry> response = restTemplate.getForEntity(
				serviceRegistryHost + "/api/v2/services/{id}?onlyRegistered=" + onlyRegistered, ServiceEntry.class,
				serviceId);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.is2xxSuccessful())
			return response.getBody();

		else if (responseStatus == HttpStatus.NOT_FOUND) {
			throw new ServiceDescriptionNotFoundException("The service with Id: " + serviceId + "was not found");
		} else
			throw new ServiceManagerException(
					"There was an error while retrieving Service Description from Service Registry: " + responseStatus);

	}

	public List<ServiceEntry> getServicesDescriptionsFromRegistry(Boolean onlyRegistered, String businessId)
			throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ServiceEntry[]> response = restTemplate.getForEntity(
				serviceRegistryHost + "/api/v2/services?onlyRegistered=" + onlyRegistered + "&businessId=" + businessId,
				ServiceEntry[].class);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.is2xxSuccessful())
			return Arrays.asList(response.getBody());
		else
			throw new ServiceManagerException(
					"There was an error while retrieving Service Descriptions from Service Registry: "
							+ responseStatus);

	}

	public ServiceEntry createServiceDescriptionAtRegistry(ServiceEntry serviceDescription)
			throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ServiceEntry> response = restTemplate.exchange(
				RequestEntity.post(URI.create(serviceRegistryHost + "/api/v2/services")).body(serviceDescription),
				ServiceEntry.class);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.equals(HttpStatus.CREATED))
			return response.getBody();

		else
			throw new ServiceManagerException(
					"There was an error while registering new Service Description to Service Registry: "
							+ responseStatus);

	}

	public ServiceEntry updateServiceDescriptionAtRegistry(ServiceEntry serviceDescription)
			throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<ServiceEntry> response = restTemplate
				.exchange(
						RequestEntity.put(
								UriComponentsBuilder.fromHttpUrl(serviceRegistryHost + "/api/v2/services/{serviceId}")
										.build(serviceDescription.getServiceId()))
								.body(serviceDescription),
						ServiceEntry.class);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.equals(HttpStatus.OK))
			return response.getBody();

		else
			throw new ServiceManagerException(
					"There was an error while updating Service Description at Service Registry: " + responseStatus);

	}

	public void unregisterServiceFromRegistry(String serviceId, Boolean deleteServiceDescription)
			throws ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		restTemplate.delete(UriComponentsBuilder.fromHttpUrl(
				serviceRegistryHost + "/api/v2/services/{serviceId}?deleteOnlyCertificate=" + !deleteServiceDescription)
				.build(serviceId));

	}

	/*
	 * Call Link Sink Service SDK -> Service Manager
	 */
	public FinalLinkingResponse callLinkSinkService(String code, String serviceId, String surrogateId,
			ServicePopKey popKey) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<FinalLinkingResponse> response = restTemplate.exchange(
				RequestEntity.post(URI.create(serviceManagerHost + "/api/v2/slr/link")).body(ContinueSinkLinkingRequest
						.sinkBuilder().code(code).serviceId(serviceId).surrogateId(surrogateId).popKey(popKey).build()),
				FinalLinkingResponse.class);

		return response.getBody();
	}

	/*
	 * Call Link Source Service SDK -> Service Manager
	 */
	public FinalLinkingResponse callLinkSourceService(String code, String serviceId, String surrogateId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<FinalLinkingResponse> response = restTemplate.exchange(
				RequestEntity.post(URI.create(serviceManagerHost + "/api/v2/slr/link")).body(ContinueLinkingRequest
						.builder().code(code).serviceId(serviceId).surrogateId(surrogateId).build()),
				FinalLinkingResponse.class);

		return response.getBody();
	}

	/*
	 * Call Get Linking Session by Code Service SDK -> Service Manager
	 */
	public LinkingSession callGetLinkingSession(String code) throws SessionNotFoundException, ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<LinkingSession> response = restTemplate
				.getForEntity(serviceManagerHost + "/api/v2/slr/linkingSession/{code}", LinkingSession.class, code);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.is2xxSuccessful())
			return response.getBody();
		else if (responseStatus == HttpStatus.NOT_FOUND) {
			throw new SessionNotFoundException("The session with code: " + code + "was not found");
		} else
			throw new ServiceManagerException(
					"There was an error while retrieving Linking Session from Service Manager");
	}

	/*
	 * Call Get Linking Code to start service link session from service in automatic
	 * mode-> Service Manager
	 */
	public String callGetLinkingCode(String serviceId, String userId, String surrogateId, String returnUrl,
			Boolean forceLinking) throws SessionNotFoundException, ServiceManagerException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<String> response = restTemplate
				.getForEntity(
						serviceManagerHost + "/api/v2/slr/service/{serviceId}/account/{userId}?surrogateId="
								+ surrogateId + "&returnUrl=" + returnUrl
								+ "&linkingFrom=Service&forceLinkCode=true&forceLinking=" + forceLinking,
						String.class, serviceId, userId);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.is2xxSuccessful())
			return response.getBody();
		else
			throw new ServiceManagerException("There was an error while retrieving Linking Code from Service Manager");
	}

	/*
	 * Call Enable service Link. Service SDK -> Service Manager
	 */
	public ServiceLinkStatusRecordSigned callEnableServiceLink(String surrogateId, String serviceId, String slrId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<ServiceLinkStatusRecordSigned> response = restTemplate
				.exchange(RequestEntity.put(UriComponentsBuilder.fromHttpUrl(serviceManagerHost
						+ "/api/v2/slr/account/{surrogateId}/services/{serviceId}/slr/{slrId}?requestFrom=SERVICE")
						.build(surrogateId, serviceId, slrId)).build(), ServiceLinkStatusRecordSigned.class);

		return response.getBody();
	}

	/*
	 * Call Disable service Link. Service SDK -> Service Manager
	 */
	public ServiceLinkStatusRecordSigned callDisableServiceLink(String surrogateId, String serviceId, String slrId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

		ResponseEntity<ServiceLinkStatusRecordSigned> response = restTemplate
				.exchange(RequestEntity.delete(UriComponentsBuilder.fromHttpUrl(serviceManagerHost
						+ "/api/v2/slr/account/{surrogateId}/services/{serviceId}/slr/{slrId}?requestFrom=SERVICE")
						.build(surrogateId, serviceId, slrId)).build(), ServiceLinkStatusRecordSigned.class);

		return response.getBody();
	}

	public ResponseEntity<ConsentForm> callFetchConsentForm(String surrogateId, String serviceId, String purposeId,
			String sourceDatasetId, String sourceServiceId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate.getForEntity(consentManagerHost
				+ "/api/v2/users/{surrogateId}/service/{serviceId}/purpose/{purposeId}/consentForm?sourceDatasetId="
				+ sourceDatasetId + "&sourceServiceId=" + sourceServiceId, ConsentForm.class, surrogateId, serviceId,
				purposeId);
	}

	public ConsentRecordSigned callGiveConsent(String surrogateId, ConsentForm consentForm) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate.postForEntity(URI.create(consentManagerHost + "/api/v2/users/" + surrogateId + "/consents"),
				consentForm, ConsentRecordSigned.class).getBody();
	}

	public ResponseEntity<ConsentStatusRecordSigned> callChangeConsentStatus(String surrogateId, String slrId,
			String crId, ChangeConsentStatusRequest request) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate.postForEntity(UriComponentsBuilder
				.fromHttpUrl(consentManagerHost
						+ "/api/v2/accounts/{surrogateId}/servicelinks/{slrId}/consents/{crId}/statuses")
				.build(surrogateId, slrId, crId), request, ConsentStatusRecordSigned.class);
	}

	public ResponseEntity<ConsentRecordSigned[]> callGetConsentRecordsBySurrogateId(String surrogateId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate.getForEntity(consentManagerHost + "/api/v2/users/{surrogateId}/consents",
				ConsentRecordSigned[].class, surrogateId);
	}

	public ResponseEntity<ConsentRecordSigned> callGetConsentRecordBySurrogateIdAndCrId(String surrogateId,
			String crId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate.getForEntity(consentManagerHost + "/api/v2/users/{surrogateId}/consents/{crId}",
				ConsentRecordSigned.class, surrogateId, crId);
	}

	public ResponseEntity<ConsentRecordSignedPair> callGetConsentRecordPairBySurrogateIdAndCrId(String surrogateId,
			String crId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate.getForEntity(consentManagerHost + "/api/v2/users/{surrogateId}/consents/{crId}/pair",
				ConsentRecordSignedPair.class, surrogateId, crId);
	}

	public ResponseEntity<ConsentRecordSigned[]> callGetConsentRecordsBySurrogateId(String surrogateId, String crId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate.getForEntity(consentManagerHost + "/api/v2/users/{surrogateId}/consents/{crId}",
				ConsentRecordSigned[].class, surrogateId, crId);
	}

	public ResponseEntity<ConsentRecordSigned[]> callGetConsentRecordsBySurrogateIdAndPurposeId(String surrogateId,
			String purposeId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate.getForEntity(
				consentManagerHost + "/api/v2/users/{surrogateId}/purpose/{purposeId}/consents",
				ConsentRecordSigned[].class, surrogateId, purposeId);
	}

	public ResponseEntity<ConsentRecordSigned[]> callGetConsentRecordsByServiceId(String serviceId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate.getForEntity(consentManagerHost + "/api/v2/services/{serviceId}/consents",
				ConsentRecordSigned[].class, serviceId);
	}

	public ResponseEntity<ConsentRecordSigned[]> callGetConsentRecordsByBusinessId(String businessId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate.getForEntity(consentManagerHost + "/api/v2/services/consents?businessId=" + businessId,
				ConsentRecordSigned[].class);
	}

	public ResponseEntity<ConsentRecordSigned[]> callGetConsentRecordBySurrogateIdAndResourceSetId(String surrogateId,
			String rsId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate.getForEntity(consentManagerHost + "/api/v2/users/{surrogateId}/consents/resourceSet/{rsId}",
				ConsentRecordSigned[].class, surrogateId, rsId);
	}

	public AuthorisationTokenResponse callGetAuthorisationToken(String crId) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate
				.getForEntity(consentManagerHost + "/api/v2/auth_token/{crId}", AuthorisationTokenResponse.class, crId)
				.getBody();
	}

	public DataTransferResponse sendDataRequest(RequestEntity<DataTransferRequest> dataRequest) {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		return restTemplate.exchange(dataRequest, DataTransferResponse.class).getBody();
	}

}
