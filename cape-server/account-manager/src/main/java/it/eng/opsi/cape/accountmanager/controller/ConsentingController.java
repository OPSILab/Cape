package it.eng.opsi.cape.accountmanager.controller;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.eng.opsi.cape.accountmanager.ApplicationProperties;
import it.eng.opsi.cape.accountmanager.model.Account;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentRecordPayload;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentSignRequest;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentSignResponse;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentStatusRecordPayload;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.accountmanager.model.consenting.RSDescription;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.accountmanager.repository.AccountRepository;
import it.eng.opsi.cape.accountmanager.service.ClientService;
import it.eng.opsi.cape.accountmanager.service.CryptoService;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v2")
@Slf4j
public class ConsentingController implements IConsentingController {

	private final ApplicationProperties appProperty;

	@Autowired
	CryptoService cryptoService;

	@Autowired
	AccountRepository accountRepo;

	@Autowired
	ClientService clientService;

	@Autowired
	public ConsentingController(ApplicationProperties appProperty) {
		this.appProperty = appProperty;
	}

	@Operation(summary = "Construct signed Consent Record and Consent Status Record based on provided payloads.", description = "Signs constructed records with Account owner's private key. CrId and Role of CommonPart and ConsentRecordRoleSpecificPart are left out of signing process.", tags = {
			"(Internal) Consenting" }, responses = {
					@ApiResponse(description = "Returns the stored Service Link Status Record.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsentSignResponse.class))) })
	@Override
	@PostMapping(value = "/users/{surrogate_id}/servicelinks/{link_id}/consents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ConsentSignResponse> signConsentRecordAndConsentStatusRecord(
			@PathVariable("surrogate_id") String surrogateId, @PathVariable("link_id") String slrId,
			@RequestBody @Valid ConsentSignRequest request) throws AccountNotFoundException, JsonProcessingException, JOSEException {

		ConsentRecordPayload inputCrPayload = request.getCrPayload();

		/************************************************************
		 * Get AccountId which belongs to the input surrogateId and slrId
		 ************************************************************/
		Account matchingAccount = accountRepo
				.findByServiceLinkRecords_Payload_slrIdAndServiceLinkRecords_Payload_SurrogateId(slrId, surrogateId)
				.orElseThrow(() -> new RuntimeException("No account found matching surrogateId and slrId"));

		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

		/************************************************************
		 * Get Account Key Pair
		 ************************************************************/
		RSAKey accountKeyPair = cryptoService.getKeyPairByAccountId(matchingAccount.getUsername());

		/************************************************************
		 * Sign Consent Record and Consent Status Record and the new Csr List with the
		 * Account private key. Add the signature of the csrList in the
		 * ConsentRecordPayload before signing it
		 ************************************************************/

		ConsentStatusRecordSigned signedCsr = cryptoService.signConsentStatusRecord(accountKeyPair,
				request.getCsrPayload());

		request.getCsrList().add(signedCsr);

		Base64URL csrListSignature = cryptoService.signConsentStatusRecordsList(accountKeyPair, request.getCsrList());
		inputCrPayload.getCommonPart().setConsentStatusesSignature(csrListSignature);

		/************************************************************
		 * Update the Common Part with the new current ConsentStatus and ResourceSet
		 ************************************************************/
		inputCrPayload.getCommonPart().setConsentStatus(request.getCsrPayload().getConsentStatus());
		inputCrPayload.getCommonPart()
				.setRsDescription(new RSDescription(request.getCsrPayload().getConsentResourceSet()));
		inputCrPayload.getCommonPart().setMat(now);

		/***************************************************************************
		 * Sign the updated Consent Record containing the updated Csr List Signature
		 ***************************************************************************/
		ConsentRecordSigned signedCr = cryptoService.signConsentRecord(accountKeyPair, inputCrPayload);
		signedCr.setConsentStatusList(request.getCsrList());

		return ResponseEntity.ok(new ConsentSignResponse(signedCr, signedCsr));

	}

//	@Operation(summary = "Constructs Consent Record and Consent Status Record, for consenting a single service, based on provided payloads.", description = "Signs constructed record's with Account owner's key.", tags = {
//			"(Internal) Consenting" }, responses = {
//					@ApiResponse(description = "Returns the stored Service Link Status Record.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WithinServiceConsentSignResponse.class))) })
//	@Override
//	@PostMapping(value = "/users/{surrogate_id}/servicelinks/{link_id}/consents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<WithinServiceConsentSignResponse> signWithinServiceConsent(
//			@PathVariable("surrogate_id") String surrogateId, @PathVariable("link_id") String slrId,
//			@RequestBody @Valid WithinServiceConsentSignRequest request)
//			throws AccountNotFoundException, JsonProcessingException, JOSEException {
//
//		/************************************************************
//		 * Get AccountId which belongs to the input surrogateId and slrId
//		 ************************************************************/
//		Account matchingAccount = accountRepo
//				.findByServiceLinkRecords_Payload_slrIdAndServiceLinkRecords_Payload_SurrogateId(slrId, surrogateId)
//				.orElseThrow(() -> new RuntimeException("No account found matching surrogateId and slrId"));
//
//		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
//
//		/************************************************************
//		 * Get Account Key Pair
//		 ************************************************************/
//		RSAKey accountKeyPair = cryptoService.getKeyPairByAccountId(matchingAccount.getUsername());
//
//		/************************************************************
//		 * Sign Consent Record and Consent Status Record and the new Csr List with the
//		 * Account private key. Add the signature of the csrList in the
//		 * ConsentRecordPayload before signing it
//		 ************************************************************/
//
//		ConsentStatusRecordSigned signedCsr = cryptoService.signConsentStatusRecord(accountKeyPair,
//				request.getCsrPayload());
//
//		request.getCsrList().add(signedCsr);
//
//		Base64URL csrListSignature = cryptoService.signConsentStatusRecordsList(accountKeyPair, request.getCsrList());
//		request.getCrPayload().getCommonPart().setConsentStatusesSignature(csrListSignature);
//
//		/************************************************************
//		 * Update the Common Part with the new current ConsentStatus and ResourceSet
//		 ************************************************************/
//		request.getCrPayload().getCommonPart().setConsentStatus(request.getCsrPayload().getConsentStatus());
//		request.getCrPayload().getCommonPart()
//				.setRsDescription(new RSDescription(request.getCsrPayload().getConsentResourceSet()));
//		request.getCrPayload().getCommonPart().setMat(now);
//
//		/***************************************************************************
//		 * Sign the updated Consent Record containing the updated Csr List Signature
//		 ***************************************************************************/
//		ConsentRecordSigned signedCr = cryptoService.signConsentRecord(accountKeyPair, request.getCrPayload());
//		signedCr.setConsentStatusList(request.getCsrList());
//
//		return ResponseEntity.ok(new WithinServiceConsentSignResponse(signedCr, signedCsr));
//
//	}
//
//	@Operation(summary = "Constructs Consent Record and Consent Status Record, for consenting a service for third party reuse case, based on provided payloads.", description = "Signs constructed record with Account owner key.", tags = {
//			"(Internal) Consenting" }, responses = {
//					@ApiResponse(description = "Returns the stored Service Link Status Record.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThirdPartyReuseConsentSignResponse.class))) })
//	@Override
//	@PostMapping(value = "/users/{surrogate_id}/servicelinks/{sink_link_id}/{source_link_id}/consents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<ThirdPartyReuseConsentSignResponse> signThirdPartyReuseConsent(
//			@PathVariable("surrogate_id") String surrogateId, @PathVariable("sink_link_id") String sinkSlrId,
//			@PathVariable("source_link_id") String sourceSlrId,
//			@RequestBody @Valid ThirdPartyReuseConsentSignRequest request) throws ServiceLinkRecordNotFoundException,
//			JsonProcessingException, JOSEException, AccountNotFoundException {
//
//		/***********************************************************
//		 * Get AccountId which belongs the input surrogateId and slrId
//		 ************************************************************/
//		Account matchingAccount = accountRepo
//				.findByServiceLinkRecords_Payload_slrIdAndServiceLinkRecords_Payload_SurrogateId(sinkSlrId, surrogateId)
//				.orElseThrow(() -> new RuntimeException("No account found matching surrogateId and slrId"));
//		String accountId = matchingAccount.getUsername();
//		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
//
//		/************************************************************
//		 * Check in addition that exists Source Service Link
//		 ************************************************************/
//		ServiceLinkRecordDoubleSigned sourceSlr = accountRepo
//				.getServiceLinkRecordByAccountIdOrUsernameAndSlrId(accountId, sourceSlrId)
//				.orElseThrow(() -> new ServiceLinkRecordNotFoundException(
//						"No Service Link was found for Source SlrId: " + sourceSlrId));
//
//		/************************************************************
//		 * Get Account Key Pair
//		 ************************************************************/
//		RSAKey accountKeyPair = cryptoService.getKeyPairByAccountId(matchingAccount.getUsername());
//
//		/************************************************************
//		 * Sign Consent Record and Consent Status Record for Sink and Source with
//		 * Account private key
//		 ************************************************************/
//
//		/************************************************************
//		 * Sink
//		 ************************************************************/
//		ConsentStatusRecordSigned signedSinkCsr = cryptoService.signConsentStatusRecord(accountKeyPair,
//				request.getSink().getCsrPayload());
//		request.getSink().getCsrList().add(signedSinkCsr);
//
//		Base64URL sinkCsrListSignature = cryptoService.signConsentStatusRecordsList(accountKeyPair,
//				request.getSink().getCsrList());
//
//		/************************************************************
//		 * Update the Common Part with the new current ConsentStatus and ResourceSet
//		 ************************************************************/
//		request.getSink().getCrPayload().getCommonPart()
//				.setConsentStatus(request.getSink().getCsrPayload().getConsentStatus());
//		request.getSink().getCrPayload().getCommonPart().setConsentStatusesSignature(sinkCsrListSignature);
//		request.getSink().getCrPayload().getCommonPart()
//				.setRsDescription(new RSDescription(request.getSink().getCsrPayload().getConsentResourceSet()));
//		request.getSink().getCrPayload().getCommonPart().setMat(now);
//
//		ConsentRecordSigned signedSinkCr = cryptoService.signConsentRecord(accountKeyPair,
//				request.getSink().getCrPayload());
//		signedSinkCr.setConsentStatusList(request.getSink().getCsrList());
//
//		/************************************************************
//		 * Source
//		 ************************************************************/
//		ConsentStatusRecordSigned signedSourceCsr = cryptoService.signConsentStatusRecord(accountKeyPair,
//				request.getSource().getCsrPayload());
//		request.getSource().getCsrList().add(signedSourceCsr);
//
//		Base64URL sourceCsrListSignature = cryptoService.signConsentStatusRecordsList(accountKeyPair,
//				request.getSource().getCsrList());
//
//		request.getSource().getCrPayload().getCommonPart().setConsentStatusesSignature(sourceCsrListSignature);
//
//		/************************************************************
//		 * Update the Common Part with the new current ConsentStatus and ResourceSet
//		 ************************************************************/
//		request.getSource().getCrPayload().getCommonPart()
//				.setConsentStatus(request.getSource().getCsrPayload().getConsentStatus());
//		request.getSource().getCrPayload().getCommonPart()
//				.setRsDescription(new RSDescription(request.getSource().getCsrPayload().getConsentResourceSet()));
//		request.getSource().getCrPayload().getCommonPart().setMat(now);
//
//		ConsentRecordSigned signedSourceCr = cryptoService.signConsentRecord(accountKeyPair,
//				request.getSource().getCrPayload());
//		signedSourceCr.setConsentStatusList(request.getSource().getCsrList());
//
//		return ResponseEntity.ok(new ThirdPartyReuseConsentSignResponse(
//				new WithinServiceConsentSignResponse(signedSinkCr, signedSinkCsr),
//				new WithinServiceConsentSignResponse(signedSourceCr, signedSourceCsr)));
//
//	}

	@Operation(summary = "Sign input Consent Status Record Payload.", description = "Signs constructed record's with Account owner's key. CrId of CommonPart and ConsentRecordRoleSpecificPart are left out of signing process.", tags = {
			"(Internal) Consenting" }, responses = {
					@ApiResponse(description = "Returns the Consent Status Record signed with the private part of the key pair matching with the input Account Id.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsentStatusRecordSigned.class))) })
	@Override
	@PostMapping(value = "/accounts/{account_id}/servicelinks/{link_id}/consents/statuses", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ConsentStatusRecordSigned> signConsentStatusRecord(
			@PathVariable("account_id") String accountId, @PathVariable("link_id") String slrId,
			@RequestBody @Valid ConsentStatusRecordPayload consentStatusRecord) throws AccountNotFoundException,
			JsonProcessingException, JOSEException, ServiceLinkRecordNotFoundException {

		ServiceLinkRecordDoubleSigned existingSlr = accountRepo
				.getServiceLinkRecordByAccountIdOrUsernameAndSlrId(accountId, slrId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException("The Service Link Record with id: " + slrId
						+ " for account Id: " + accountId + " was not found"));

		/************************************************************
		 * Get Account Key Pair
		 ************************************************************/
		RSAKey accountKeyPair = cryptoService.getKeyPairByAccountId(accountId);

		return ResponseEntity.ok(cryptoService.signConsentStatusRecord(accountKeyPair, consentStatusRecord));

	}

	@Operation(summary = "Sign input Consent Status Record List.", description = "Signs constructed Conssent Status Records List with Account owner's key.", tags = {
			"(Internal) Consenting" }, responses = {
					@ApiResponse(description = "Returns the Base64 Signature Consent Status Records List with the private part of the key pair matching with the input Account Id.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Base64.class))) })
	@Override
	@PostMapping(value = "/accounts/{account_id}/servicelinks/{link_id}/consents/statuses/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Base64> signConsentStatusRecordsList(@PathVariable("account_id") String accountId,
			@PathVariable("link_id") String slrId, @RequestBody @Valid List<ConsentStatusRecordSigned> csrList)
			throws AccountNotFoundException, JsonProcessingException, JOSEException,
			ServiceLinkRecordNotFoundException {

		ServiceLinkRecordDoubleSigned existingSlr = accountRepo
				.getServiceLinkRecordByAccountIdOrUsernameAndSlrId(accountId, slrId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException("The Service Link Record with id: " + slrId
						+ " for account Id: " + accountId + " was not found"));

		/************************************************************
		 * Get Account Key Pair
		 ************************************************************/
		RSAKey accountKeyPair = cryptoService.getKeyPairByAccountId(accountId);

		return ResponseEntity.ok(cryptoService.signConsentStatusRecordsList(accountKeyPair, csrList));

	}

}
