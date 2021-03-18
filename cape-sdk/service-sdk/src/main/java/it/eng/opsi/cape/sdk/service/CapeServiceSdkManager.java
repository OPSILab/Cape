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
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;

import it.eng.opsi.cape.exception.CapeSdkManagerException;
import it.eng.opsi.cape.exception.DataRequestNotValid;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceManagerException;
import it.eng.opsi.cape.exception.ServiceSignKeyNotFoundException;
import it.eng.opsi.cape.sdk.model.ServicePopKey;
import it.eng.opsi.cape.sdk.model.ServiceSignKey;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSignedPair;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSourceRoleSpecificPart;
import it.eng.opsi.cape.sdk.model.consenting.ConsentStatusRecordPayload;
import it.eng.opsi.cape.sdk.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.sdk.model.datatransfer.AuthorisationTokenPayload;
import it.eng.opsi.cape.sdk.model.datatransfer.AuthorisationTokenResponse;
import it.eng.opsi.cape.sdk.model.datatransfer.DataRequestAuthorizationPayload;
import it.eng.opsi.cape.sdk.model.datatransfer.DataTransferRequest;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordAccountSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.sdk.repository.ServicePopKeyRepository;
import it.eng.opsi.cape.sdk.repository.ServiceSignKeyRepository;
import it.eng.opsi.cape.serviceregistry.data.Cert;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;

@Service
public class CapeServiceSdkManager {

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private ClientService clientService;

	@Autowired
	private ServicePopKeyRepository popKeyRepo;

	@Autowired
	private ServiceSignKeyRepository signKeyRepo;

	public String generateSurrogateId(String operatorId, String userId) {
		return RandomStringUtils.randomAlphanumeric(15);
	}

	public ServicePopKey getPopKey(String operatorId, String serviceId, String surrogateId) throws JOSEException {

		Optional<ServicePopKey> optServicePopKey = popKeyRepo.findFirstBySurrogateId(surrogateId);

		if (!optServicePopKey.isPresent()) {
			ServicePopKey newPopKey = new ServicePopKey(operatorId, serviceId, surrogateId,
					cryptoService.generateRSAKey());
			popKeyRepo.insert(newPopKey);
			return newPopKey;
		} else {
			return optServicePopKey.get();
		}
	}

	public ServiceSignKey generateSignKey(String serviceId) throws JOSEException {

		Optional<ServiceSignKey> optServiceSignKey = signKeyRepo.findFirstByServiceId(serviceId);

		if (!optServiceSignKey.isPresent()) {
			RSAKey newRSAKey = cryptoService.generateRSAKey();
			return new ServiceSignKey(newRSAKey.getKeyID(), serviceId, newRSAKey);
		} else
			return optServiceSignKey.get();
	}

	public ServiceSignKey storeSignKey(ServiceSignKey serviceSignKeyPair) {

		return signKeyRepo.insert(serviceSignKeyPair);
	}

	/*
	 * Create a new X509 Public certificate, with Service Provider Name as Issuer
	 * and Service Name as Subject
	 * 
	 * TODO The certificate could be issued by an external CA in behalf of Service
	 * Provider
	 */
	private String createPemEncodedX509PublicSignKeyCertificate(ServiceEntry serviceDescription,
			ServiceSignKey serviceSignKey) throws JOSEException {

		String providerNameAsIssuer = serviceDescription.getServiceInstance().getServiceProvider().getName();

		String serviceNameAsSubject = serviceDescription.getName();

		X509Certificate serviceX509Certificate = cryptoService.buildX509Certificate(providerNameAsIssuer,
				serviceNameAsSubject, serviceSignKey.getJwk());

		return cryptoService.pemEncodeX509Certificate(serviceX509Certificate);

	}

	public ServiceSignKey getSignKey(String serviceId, String kid)
			throws JOSEException, ServiceSignKeyNotFoundException {

		Optional<ServiceSignKey> storedKey = signKeyRepo.findFirstByServiceIdAndKid(serviceId, kid);

		try {
			return storedKey.get();
		} catch (NoSuchElementException ex) {
			throw new ServiceSignKeyNotFoundException(
					"Sign key for Service Id: " + serviceId + " and kid: " + kid + " was not found");
		}
	}

	public ServiceEntry registerServiceToCape(String serviceId) throws JOSEException, ServiceManagerException,
			JsonProcessingException, ServiceDescriptionNotFoundException {

		ServiceEntry serviceDescription = clientService.getServiceDescriptionFromRegistry(serviceId, false);

		ServiceSignKey serviceSignKey = generateSignKey(serviceId);

		String pemEncodedX509PublicKeyCertificate = createPemEncodedX509PublicSignKeyCertificate(serviceDescription,
				serviceSignKey);

		serviceDescription.getServiceInstance()
				.setCert(new Cert("", Arrays.asList(pemEncodedX509PublicKeyCertificate)));

		/*
		 * Sign the Service Description with the generated Service Sign key
		 */
		serviceDescription.setServiceDescriptionSignature(
				cryptoService.signServiceDescription(serviceSignKey, serviceDescription).toString());

		clientService.updateServiceDescriptionAtRegistry(serviceDescription);

		/*
		 * If updating Service Description was successful, we can store the generated
		 * Service Sign key pair
		 */
		storeSignKey(serviceSignKey);

		/*
		 * TODO SET x5u with serviceregistry endpoint providing Service Public X509
		 * Certificate
		 * 
		 * serviceDescription.getServiceInstance().getCert().setX5u(x5u);
		 */

		return serviceDescription;

	}

	public void unregisterService(String serviceId, Boolean deleteServiceDescription)
			throws JOSEException, ServiceManagerException, ServiceSignKeyNotFoundException {

		/*
		 * Delete Service Sign key pair
		 */
		Long deletedItems = signKeyRepo.deleteServiceSignKeyByServiceId(serviceId);
		if (deletedItems != 1)
			throw new ServiceSignKeyNotFoundException("The Sign Key for Service Id: " + serviceId + " was not found");

		/*
		 * Delete Service Description at Service
		 */
		clientService.unregisterServiceFromRegistry(serviceId, deleteServiceDescription);

	}

	public List<ServiceSignKey> getRegisteredServicesKeys() {

		return signKeyRepo.findAll();

	}

	public List<ServiceEntry> getServices(Boolean onlyRegistered, String businessId) throws ServiceManagerException {

		return clientService.getServicesDescriptionsFromRegistry(onlyRegistered, businessId);
	}

	public ServiceEntry getService(String serviceId, Boolean onlyRegistered)
			throws ServiceManagerException, ServiceDescriptionNotFoundException {

		return clientService.getServiceDescriptionFromRegistry(serviceId, onlyRegistered);
	}

	public ServiceLinkRecordDoubleSigned verifyAndSignServiceLinkRecordPayload(
			ServiceLinkRecordAccountSigned accountSignedSlr)
			throws JOSEException, JsonProcessingException, ParseException, ServiceSignKeyNotFoundException {

		/*
		 * Verify Account Signed Service Link Record
		 */
		if (!cryptoService.verifyAccountSignedSLR(accountSignedSlr))
			throw new JOSEException("The Account Signed Service Link Record is not valid");

		/*
		 * Get Service RSA Key Pair by Service Id contained in the Account Signed SLR
		 */
		String slrServiceId = accountSignedSlr.getPayload().getServiceId();
		Optional<ServiceSignKey> serviceKeyPair = signKeyRepo.findFirstByServiceId(slrServiceId);

		if (!serviceKeyPair.isPresent())
			throw new ServiceSignKeyNotFoundException(
					"No service key found for Service Id: " + slrServiceId + " contained in the Account Signed SLR");
		/*
		 * Sign the Account Signed SLR with Service Key to produce the Double Signed SLR
		 */
		return cryptoService.signSLRPayload(serviceKeyPair.get(), accountSignedSlr);

	}

	public Boolean verifyConsentRecordSigned(ConsentRecordSigned signedConsentRecord, RSAKey accountPublicKey)
			throws JsonProcessingException, ParseException, JOSEException {

		return cryptoService.verifyConsentRecordSigned(signedConsentRecord, accountPublicKey);
	}

	public Boolean verifyConsentStatusRecordSigned(ConsentStatusRecordSigned signedConsentStatusRecord,
			RSAKey accountPublicKey) throws JsonProcessingException, ParseException, JOSEException {

		return cryptoService.verifyConsentStatusRecordSigned(signedConsentStatusRecord, accountPublicKey);
	}

	public Boolean verifyConsentStatusChain(ConsentStatusRecordPayload[] csrArray) {

		if (csrArray.length == 1 && csrArray[0].getPrevRecordId().equals("none"))
			return true;
		else if (csrArray.length == 0)
			return true;
		else if (csrArray.length > 1
				&& csrArray[csrArray.length - 1].getPrevRecordId().equals(csrArray[csrArray.length - 2].getRecordId()))
			return verifyConsentStatusChain(Arrays.copyOfRange(csrArray, 0, csrArray.length - 2));

		return false;

	}

	public Boolean verifyServiceLinkStatusRecord(ServiceLinkStatusRecordSigned signedSsr)
			throws JsonProcessingException, JOSEException, ParseException {
		return cryptoService.verifyServiceLinkStatusRecordSigned(signedSsr);
	}

	public AuthorisationTokenPayload extractTokenPayloadFromSerializedToken(String serializedToken)
			throws JsonMappingException, JsonProcessingException, ParseException {

		return cryptoService.extractPayloadFromSerializedAuthToken(serializedToken);

	}

	public RequestEntity<DataTransferRequest> prepareDataRequest(DataRequestAuthorizationPayload authPayload,
			DataTransferRequest dataRequest) throws CapeSdkManagerException, JsonProcessingException, JOSEException,
			ServiceManagerException, ServiceDescriptionNotFoundException {

		ServicePopKey popKey = popKeyRepo.findFirstBySurrogateId(dataRequest.getSurrogateId()).orElseThrow(
				() -> new CapeSdkManagerException("No Pop key found for surrogateId: " + dataRequest.getSurrogateId()));

		String dataRequestSignature = cryptoService.signAndSerializeDataRequestAuthorization(popKey, authPayload);

		// Get Consent Pair starting from CrId
		ConsentRecordSignedPair consentPair = clientService
				.callGetConsentRecordPairBySurrogateIdAndCrId(dataRequest.getSurrogateId(), dataRequest.getCrId())
				.getBody();

		// Get Source Service Id from subjectId of Source's Consent
		String sourceServiceId = consentPair.getSource().getPayload().getCommonPart().getSubjectId();

		// Get service description to get source library domain
		ServiceEntry sourceService = clientService.getServiceDescriptionFromRegistry(sourceServiceId, true);

		// Construct data request url
		String sourceLibraryDomain = sourceService.getServiceInstance().getServiceUrls().getLibraryDomain();

		return RequestEntity
				.post(URI.create(sourceLibraryDomain + "/api/v2/dc/send?dataset_id=" + dataRequest.getDatasetId()))
				.header("Authorization", "PoP " + dataRequestSignature).body(dataRequest);

	}

	public Boolean verifyTokenAndDataRequest(String popHeader, ConsentRecordSignedPair consentPair,
			DataTransferRequest dataRequest)
			throws JsonProcessingException, JOSEException, ParseException, DataRequestNotValid {

		/*
		 * Verify Data Request signature -> Pop Authorization header signature with the
		 * Pop key contained in the Consent Record
		 */
		/*
		 * Verify Authorization Token -> Contained in the payload of Pop Authorization
		 * header -> Verify signature with Operator Key contained in the token issuer
		 * field of consent record
		 */

		return cryptoService.verifyDataRequest(popHeader, consentPair);

	}

}
