package it.eng.opsi.cape.accountmanager.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.Base64;

import it.eng.opsi.cape.accountmanager.model.consenting.ConsentStatusRecordPayload;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.accountmanager.model.consenting.ThirdPartyReuseConsentSignRequest;
import it.eng.opsi.cape.accountmanager.model.consenting.ThirdPartyReuseConsentSignResponse;
import it.eng.opsi.cape.accountmanager.model.consenting.WithinServiceConsentSignRequest;
import it.eng.opsi.cape.accountmanager.model.consenting.WithinServiceConsentSignResponse;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;

public interface IConsentingController {

	public abstract ResponseEntity<WithinServiceConsentSignResponse> signWithinServiceConsent(String surrogateId,
			String slrId, WithinServiceConsentSignRequest request)
			throws AccountNotFoundException, JsonProcessingException, JOSEException;

	public abstract ResponseEntity<ThirdPartyReuseConsentSignResponse> signThirdPartyReuseConsent(String surrogateId,
			String sinkSlrId, String sourceSlrId, ThirdPartyReuseConsentSignRequest request)
			throws AccountNotFoundException, ServiceLinkRecordNotFoundException, JsonProcessingException, JOSEException;

	public abstract ResponseEntity<ConsentStatusRecordSigned> signConsentStatusRecord(String accountId, String slrId,
			ConsentStatusRecordPayload consentStatusRecord)
			throws AccountNotFoundException, JsonProcessingException, JOSEException, ServiceLinkRecordNotFoundException;

	public abstract ResponseEntity<Base64> signConsentStatusRecordsList(String accountId, String slrId,
			@Valid List<ConsentStatusRecordSigned> consentStatusRecord)
			throws AccountNotFoundException, JsonProcessingException, JOSEException, ServiceLinkRecordNotFoundException;

}
