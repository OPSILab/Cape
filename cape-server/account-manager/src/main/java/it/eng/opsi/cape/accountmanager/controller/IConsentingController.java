package it.eng.opsi.cape.accountmanager.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.Base64;

import it.eng.opsi.cape.accountmanager.model.consenting.ConsentSignRequest;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentSignResponse;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentStatusRecordPayload;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;

public interface IConsentingController {

	public abstract ResponseEntity<ConsentSignResponse> signConsentRecordAndConsentStatusRecord(String surrogateId,
			String slrId, ConsentSignRequest request)
			throws AccountNotFoundException, JsonProcessingException, JOSEException;

	public abstract ResponseEntity<ConsentStatusRecordSigned> signConsentStatusRecord(String accountId, String slrId,
			ConsentStatusRecordPayload consentStatusRecord)
			throws AccountNotFoundException, JsonProcessingException, JOSEException, ServiceLinkRecordNotFoundException;

	public abstract ResponseEntity<Base64> signConsentStatusRecordsList(String accountId, String slrId,
			@Valid List<ConsentStatusRecordSigned> consentStatusRecord)
			throws AccountNotFoundException, JsonProcessingException, JOSEException, ServiceLinkRecordNotFoundException;

}
