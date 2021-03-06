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
package it.eng.opsi.cape.accountmanager.controller;

import java.net.URI;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import it.eng.opsi.cape.accountmanager.ApplicationProperties;
import it.eng.opsi.cape.accountmanager.model.Account;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentStatusRecordPayload;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.accountmanager.model.consenting.RSDescription;
import it.eng.opsi.cape.accountmanager.model.consenting.ThirdPartyReuseConsentSignRequest;
import it.eng.opsi.cape.accountmanager.model.consenting.ThirdPartyReuseConsentSignResponse;
import it.eng.opsi.cape.accountmanager.model.consenting.WithinServiceConsentSignRequest;
import it.eng.opsi.cape.accountmanager.model.consenting.WithinServiceConsentSignResponse;
import it.eng.opsi.cape.accountmanager.model.linking.AccountSignSlrRequest;
import it.eng.opsi.cape.accountmanager.model.linking.AccountSignSlrResponse;
import it.eng.opsi.cape.accountmanager.model.linking.FinalStoreSlrRequest;
import it.eng.opsi.cape.accountmanager.model.linking.FinalStoreSlrResponse;
import it.eng.opsi.cape.accountmanager.model.linking.FinalStoreSlrResponse.LinkingResponseData;
import it.eng.opsi.cape.accountmanager.model.linking.LinkingSession;
import it.eng.opsi.cape.accountmanager.model.linking.LinkingSessionStateEnum;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkInitResponse;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordAccountSigned;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordPayload;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkStatusRecordPayload;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.accountmanager.model.linking.SinkServiceLinkInitRequest;
import it.eng.opsi.cape.accountmanager.model.linking.SourceServiceLinkInitRequest;
import it.eng.opsi.cape.accountmanager.repository.AccountRepository;
import it.eng.opsi.cape.accountmanager.service.ClientService;
import it.eng.opsi.cape.accountmanager.service.CryptoService;
import it.eng.opsi.cape.exception.AccountManagerException;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordAlreadyPresentException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.exception.SessionNotFoundException;
import it.eng.opsi.cape.exception.SessionStateNotAllowedException;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v2")
@Slf4j
public class ServiceLinkingController implements IServiceLinkingController {

	private final ApplicationProperties appProperty;

	private final String accountManagerHost;

	@Autowired
	CryptoService cryptoService;

	@Autowired
	AccountRepository accountRepo;

	@Autowired
	ClientService clientService;

	@Autowired
	public ServiceLinkingController(ApplicationProperties appProperty) {
		this.appProperty = appProperty;
		this.accountManagerHost = this.appProperty.getCape().getAccountManager().getHost();
	}

	@Override
	@Operation(summary = "Init Service Linking at Account Manager - Sink", description = "Stores Service Link ID to Account with PoP Key.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns Redirect to Service Login page for authentication.", responseCode = "302") })
	@PostMapping(value = "/accounts/{account_id}/servicelinks/init/sink", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceLinkInitResponse> storeSinkSlrId(@PathVariable("account_id") String accountId,
			@RequestBody @Valid SinkServiceLinkInitRequest request)
			throws SessionNotFoundException, AccountManagerException, AccountNotFoundException,
			ServiceLinkRecordAlreadyPresentException, SessionStateNotAllowedException {

		/*
		 * Get session by input code, check if is in an allowed State and if its
		 * accountId matches the ones in input
		 */
		LinkingSession session = clientService.callGetLinkingSession(request.getCode());
		if (!session.getState().equals(LinkingSessionStateEnum.STARTED))
			throw new SessionStateNotAllowedException(
					"The Linking Session should be in STARTED state, " + session.getState() + " found instead");

		if (!session.getAccountId().equals(accountId))
			throw new SessionNotFoundException("The Session Account Id does not match with the input AccountId");

		ServiceLinkRecordPayload partialSlr = new ServiceLinkRecordPayload();
		partialSlr.setSlrId(request.getSlrId());
		partialSlr.setServiceId(request.getServiceId());
		partialSlr.setSurrogateId(request.getSurrogateId());
		// partialSlr.setAccountId(accountId);
		partialSlr.setPopKey(request.getPopKey());

		accountRepo.addSlrPartialPayload(accountId, partialSlr);

		return ResponseEntity
				.created(UriComponentsBuilder
						.fromHttpUrl(accountManagerHost + "/api/v2/accounts/{account_id}/servicelinks/{link_id}")
						.build(accountId, partialSlr.getSlrId()))
				.body(new ServiceLinkInitResponse(request.getCode(), partialSlr.getSlrId().toString()));
	}

	@Override
	@Operation(summary = "Init Service Linking at Account Manager - Source.", description = "Stores Service Link ID to Account.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns Redirect to Service Login page for authentication.", responseCode = "302") })
	@PostMapping(value = "/accounts/{account_id}/servicelinks/init/source", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceLinkInitResponse> storeSourceSlrId(@PathVariable("account_id") String accountId,
			@RequestBody @Valid SourceServiceLinkInitRequest request)
			throws SessionNotFoundException, AccountManagerException, AccountNotFoundException,
			ServiceLinkRecordAlreadyPresentException, SessionStateNotAllowedException {

		/*
		 * Get session by input code, check if is in an allowed State and if its
		 * accountId matches the ones in input
		 */
		LinkingSession session = clientService.callGetLinkingSession(request.getCode());
		if (!session.getState().equals(LinkingSessionStateEnum.STARTED))
			throw new SessionStateNotAllowedException(
					"The Linking Session should be in STARTED state, " + session.getState() + " found instead");

		if (!session.getAccountId().equals(accountId))
			throw new SessionNotFoundException("The Session Account Id does not match with the input AccountId");

		ServiceLinkRecordPayload partialSlr = new ServiceLinkRecordPayload();
		partialSlr.setSlrId(request.getSlrId());
//		partialSlr.setAccountId(accountId);
		partialSlr.setServiceId(request.getServiceId());
		partialSlr.setSurrogateId(request.getSurrogateId());

		accountRepo.addSlrPartialPayload(accountId, partialSlr);

		return ResponseEntity
				.created(UriComponentsBuilder
						.fromHttpUrl(accountManagerHost + "/api/v2/accounts/{account_id}/servicelinks/{link_id}")
						.build(accountId, partialSlr.getSlrId()))
				.body(new ServiceLinkInitResponse(request.getCode(), partialSlr.getSlrId().toString()));

	}

	@Operation(summary = "Sign Service Link.", description = "Signs constructed Service Link Record with Account owner's key.", tags = {
			"Service Linking" }, responses = { @ApiResponse(description = "	\r\n"
					+ "Service Link Record and Service Link Status Record created.", responseCode = "302"), })

	@Override
	@PatchMapping(value = "/accounts/{account_id}/servicelinks/{link_id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountSignSlrResponse> accountSignSlr(@PathVariable("account_id") String accountId,
			@PathVariable("link_id") String slrId, @RequestBody @Valid AccountSignSlrRequest request)
			throws SessionNotFoundException, AccountManagerException, JOSEException, AccountNotFoundException,
			JsonProcessingException, SessionStateNotAllowedException {

		ServiceLinkRecordPayload partialSlr = request.getPartialSlrPayload();

		if (!partialSlr.getSlrId().equals(slrId))
			throw new IllegalArgumentException("The slr Id in input does not match with the payload one");

		/*
		 * Get session by input code, check if is in an allowed State and if its
		 * accountId matches the ones in input
		 */
		LinkingSession session = clientService.callGetLinkingSession(request.getCode());
		if (!session.getState().equals(LinkingSessionStateEnum.SLR_ID_STORED))
			throw new SessionStateNotAllowedException(
					"The Linking Session should be in SLR_ID_STORED state, " + session.getState() + " found instead");

		if (!session.getAccountId().equals(accountId) || !session.getServiceId().equals(partialSlr.getServiceId()))
			throw new SessionNotFoundException("The Session Account Id - Service ID don't match with the input one");

		/*
		 * Get Account Key Pair
		 */
		RSAKey accountKeyPair = cryptoService.getKeyPairByAccountId(accountId);

		/*
		 * Add public key to SLR payload (cr_keys field)
		 */
		partialSlr.addCrKey(accountKeyPair.toPublicJWK());

		ServiceLinkRecordAccountSigned accountSignedSlr = cryptoService.signPartialSLR(accountKeyPair, partialSlr);

		return ResponseEntity
				.created(UriComponentsBuilder
						.fromHttpUrl(accountManagerHost + "/api/v2/accounts/{account_id}/servicelinks/{link_id}")
						.build(accountId, partialSlr.getSlrId()))
				.body(new AccountSignSlrResponse(request.getCode(), accountSignedSlr));

	}

	@Operation(summary = "Init Service Linking at Account Manager - Source", description = "Stores Service Link ID to Account.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns Redirect to Service Login page for authentication.", responseCode = "302") })

	@Override
	@PostMapping(value = "/accounts/{account_id}/servicelinks/{link_id}/store", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FinalStoreSlrResponse> storeFinalSlr(@RequestBody @Valid FinalStoreSlrRequest request,
			@PathVariable("account_id") String accountId, @PathVariable("link_id") String slrId)
			throws SessionNotFoundException, AccountManagerException, JsonProcessingException, ParseException,
			JOSEException, AccountNotFoundException, ServiceLinkRecordAlreadyPresentException,
			ServiceLinkRecordNotFoundException, SessionStateNotAllowedException {

		ServiceLinkRecordPayload slrPayload = request.getSlr().getPayload();
		ServiceLinkRecordDoubleSigned slrDoubleSigned = request.getSlr();

		if (!slrPayload.getSlrId().equals(slrId))
			throw new IllegalArgumentException("The slr Id in input does not match with the payload one");

		/*
		 * Get session by input code, check if is in an allowed State and if its
		 * accountId matches the ones in input
		 */
		LinkingSession session = clientService.callGetLinkingSession(request.getCode());
		if (!session.getState().equals(LinkingSessionStateEnum.DOUBLE_SIGNED_SLR))
			throw new SessionStateNotAllowedException("The Linking Session should be in DOUBLE_SIGNED_SLR state, "
					+ session.getState() + " found instead");

		if (!session.getAccountId().equals(accountId) || !session.getServiceId().equals(slrPayload.getServiceId()))
			throw new SessionNotFoundException("The Session Account Id - Service ID don't match with the input one");

		/*
		 * Verify input SLR Account signature
		 */
		if (!cryptoService.verifyAccountSignedSLR(slrDoubleSigned))
			throw new AccountManagerException("The Account signature validation of input SLR failed");

		/*
		 * Get Account Key Pair
		 */
		RSAKey accountKeyPair = cryptoService.getKeyPairByAccountId(accountId);

		/*
		 * Sign SSR Payload with Account private key
		 */
		ServiceLinkStatusRecordSigned signedSsr = cryptoService.signSSR(accountKeyPair, request.getSsr());
		slrDoubleSigned.addServiceLinkStatusRecord(signedSsr);

		accountRepo.storeFinalSlr(accountId, slrDoubleSigned);

		return ResponseEntity
				.created(UriComponentsBuilder
						.fromHttpUrl(accountManagerHost + "/api/v2/accounts/{account_id}/servicelinks/{link_id}")
						.build(accountId, slrPayload.getSlrId()))
				.body(new FinalStoreSlrResponse(request.getCode(),
						new LinkingResponseData(slrDoubleSigned, signedSsr)));
	}

	@Operation(summary = "Sign and store a new Service Link Status Record.", description = "Constructs signed Service Link Status Record based on provided Service Link Status Record payload. Signs constructed Service Link Status Record with Account owner's key. Finally Service Link Status Record is stored.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns the newly signed Service Link Status Record.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
	@Override
	@PostMapping(value = "/accounts/{account_id}/servicelinks/{link_id}/statuses", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceLinkStatusRecordSigned> signAndStoreServiceLinkStatus(
			@PathVariable("account_id") String accountId, @PathVariable("link_id") String slrId,
			@RequestBody @Valid ServiceLinkStatusRecordPayload newSsr) throws ServiceLinkRecordNotFoundException,
			AccountNotFoundException, JsonProcessingException, JOSEException {

		/*
		 * Get the Service Link Record corresponding to the input SSR
		 */
		ServiceLinkRecordDoubleSigned existingSlr = accountRepo
				.getServiceLinkRecordByAccountIdOrUsernameAndSlrId(accountId, slrId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException("The Service Link Record with id: " + slrId
						+ " for account Id: " + accountId + " was not found"));

		/*
		 * Check id prev_record_id of the new Ssr matches with the id of the last
		 * current Service Link Status Record
		 */
		ServiceLinkStatusRecordSigned lastCurrentSsr = existingSlr.getServiceLinkStatuses()
				.get(existingSlr.getServiceLinkStatuses().size() - 1);

		if (!lastCurrentSsr.getPayload().get_id().equals(newSsr.getPrevRecordId()))
			throw new IllegalArgumentException(
					"The prevRecordId of input Ssr does not match with the last Ssr id of the SLR with id: " + slrId);
		/*
		 * Get Account Key Pair
		 */
		RSAKey accountKeyPair = cryptoService.getKeyPairByAccountId(accountId);

		/*
		 * Sign SSR Payload with Account private key
		 */
		ServiceLinkStatusRecordSigned signedSsr = cryptoService.signSSR(accountKeyPair, newSsr);
		existingSlr.addServiceLinkStatusRecord(signedSsr);

		accountRepo.storeFinalSlr(accountId, existingSlr);

		return ResponseEntity.created((UriComponentsBuilder
				.fromHttpUrl(
						accountManagerHost + "/api/v2/accounts/{account_id}/servicelinks/{link_id}/statuses/{ssr_id}")
				.build(accountId, slrId, newSsr.get_id()))).body(signedSsr);
	}

	@Operation(summary = "Only store a new Service Link Status Record, already signed by Service Manager.", description = "Store a new Service Link Status Record, already signed by Service Manager with the Operator's private key.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns the stored Service Link Status Record.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceLinkStatusRecordSigned.class))) })
	@Override
	@PostMapping(value = "/users/{surrogate_id}/servicelinks/{link_id}/statuses/storeOnly", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceLinkStatusRecordSigned> storeSignedServiceLinkStatus(
			@PathVariable("surrogate_id") String surrogateId, @PathVariable("link_id") String slrId,
			@RequestBody @Valid ServiceLinkStatusRecordSigned newSsr) throws ServiceLinkRecordNotFoundException,
			AccountNotFoundException, JsonProcessingException, JOSEException {

		/*
		 * Get AccountId which belongs the input surrogateId and slrId
		 */
		Account matchingAccount = accountRepo
				.findByServiceLinkRecords_Payload_slrIdAndServiceLinkRecords_Payload_SurrogateId(slrId, surrogateId)
				.orElseThrow(() -> new RuntimeException("No account found matching surrogateId and slrId"));

		/*
		 * Get the Service Link Record corresponding to the input SSR
		 */
		ServiceLinkRecordDoubleSigned existingSlr = accountRepo
				.getServiceLinkRecordByAccountIdOrUsernameAndSlrId(matchingAccount.getUsername(), slrId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException("The Service Link Record with id: " + slrId
						+ " for account Id: " + matchingAccount.getUsername() + " was not found"));

		/*
		 * Check id prev_record_id of the new Ssr matches with the id of the last
		 * current Service Link Status Record
		 */
		ServiceLinkStatusRecordSigned lastCurrentSsr = existingSlr.getServiceLinkStatuses()
				.get(existingSlr.getServiceLinkStatuses().size() - 1);

		if (!lastCurrentSsr.getPayload().get_id().equals(newSsr.getPayload().getPrevRecordId()))
			throw new IllegalArgumentException(
					"The prevRecordId of input Ssr does not match with the last Ssr id of the SLR with id: " + slrId);

		/*
		 * Store signed SSR
		 */
		existingSlr.addServiceLinkStatusRecord(newSsr);
		accountRepo.storeFinalSlr(matchingAccount.getUsername(), existingSlr);

		return ResponseEntity.created((UriComponentsBuilder
				.fromHttpUrl(
						accountManagerHost + "/api/v2/accounts/{account_id}/servicelinks/{link_id}/statuses/{ssr_id}")
				.build(matchingAccount.getUsername(), slrId, newSsr.getPayload().get_id()))).body(newSsr);
	}

	@Operation(summary = "Constructs Consent Record's and Consent Status Record's, for consenting a single service, based on provided payloads.", description = "Signs constructed record's with Account owner's key.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns the stored Service Link Status Record.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WithinServiceConsentSignResponse.class))) })
	@Override
	@PostMapping(value = "/users/{surrogate_id}/servicelinks/{link_id}/consents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<WithinServiceConsentSignResponse> signWithinServiceConsent(
			@PathVariable("surrogate_id") String surrogateId, @PathVariable("link_id") String slrId,
			@RequestBody @Valid WithinServiceConsentSignRequest request)
			throws AccountNotFoundException, JsonProcessingException, JOSEException {

		/*
		 * Get AccountId which belongs the input surrogateId and slrId
		 */
		Account matchingAccount = accountRepo
				.findByServiceLinkRecords_Payload_slrIdAndServiceLinkRecords_Payload_SurrogateId(slrId, surrogateId)
				.orElseThrow(() -> new RuntimeException("No account found matching surrogateId and slrId"));

		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

		/*
		 * Get Account Key Pair
		 */
		RSAKey accountKeyPair = cryptoService.getKeyPairByAccountId(matchingAccount.getUsername());

		/*
		 * Sign Consent Record and Consent Status Record and the new Csr List with
		 * Account private key. Add the signature of the csrList in the
		 * ConsentRecordPayload before signing it
		 */

		ConsentStatusRecordSigned signedCsr = cryptoService.signConsentStatusRecord(accountKeyPair,
				request.getCsrPayload());

		request.getCsrList().add(signedCsr);

		Base64URL csrListSignature = cryptoService.signConsentStatusRecordsList(accountKeyPair, request.getCsrList());
		request.getCrPayload().getCommonPart().setConsentStatusesSignature(csrListSignature);

		/*
		 * Update the Common Part with the new current ConsentStatus and ResourceSet
		 */
		request.getCrPayload().getCommonPart().setConsentStatus(request.getCsrPayload().getConsentStatus());
		request.getCrPayload().getCommonPart()
				.setRsDescription(new RSDescription(request.getCsrPayload().getConsentResourceSet()));
		request.getCrPayload().getCommonPart().setMat(now);

		/*
		 * Sign the update Consent Record containing the updated Csr List Signature
		 */
		ConsentRecordSigned signedCr = cryptoService.signConsentRecord(accountKeyPair, request.getCrPayload());
		signedCr.setConsentStatusList(request.getCsrList());

		return ResponseEntity.ok(new WithinServiceConsentSignResponse(signedCr, signedCsr));

	}

	@Operation(summary = "Constructs Consent Record and Consent Status Record, for consenting a service for third party reuse case, based on provided payloads.", description = "Signs constructed record with Account owner key.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns the stored Service Link Status Record.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThirdPartyReuseConsentSignResponse.class))) })
	@Override
	@PostMapping(value = "/users/{surrogate_id}/servicelinks/{sink_link_id}/{source_link_id}/consents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ThirdPartyReuseConsentSignResponse> signThirdPartyReuseConsent(
			@PathVariable("surrogate_id") String surrogateId, @PathVariable("sink_link_id") String sinkSlrId,
			@PathVariable("source_link_id") String sourceSlrId,
			@RequestBody @Valid ThirdPartyReuseConsentSignRequest request) throws ServiceLinkRecordNotFoundException,
			JsonProcessingException, JOSEException, AccountNotFoundException {

		/*
		 * Get AccountId which belongs the input surrogateId and slrId
		 */
		Account matchingAccount = accountRepo
				.findByServiceLinkRecords_Payload_slrIdAndServiceLinkRecords_Payload_SurrogateId(sinkSlrId, surrogateId)
				.orElseThrow(() -> new RuntimeException("No account found matching surrogateId and slrId"));
		String accountId = matchingAccount.getUsername();
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

		/*
		 * Check in addition that exists Source Service Link
		 */
		ServiceLinkRecordDoubleSigned sourceSlr = accountRepo
				.getServiceLinkRecordByAccountIdOrUsernameAndSlrId(accountId, sourceSlrId)
				.orElseThrow(() -> new ServiceLinkRecordNotFoundException(
						"No Service Link was found for Source SlrId: " + sourceSlrId));

		/*
		 * Get Account Key Pair
		 */
		RSAKey accountKeyPair = cryptoService.getKeyPairByAccountId(matchingAccount.getUsername());

		/*
		 * Sign Consent Record and Consent Status Record for Sink and Source with
		 * Account private key
		 */

		/*
		 * Sink
		 */
		ConsentStatusRecordSigned signedSinkCsr = cryptoService.signConsentStatusRecord(accountKeyPair,
				request.getSink().getCsrPayload());
		request.getSink().getCsrList().add(signedSinkCsr);

		Base64URL sinkCsrListSignature = cryptoService.signConsentStatusRecordsList(accountKeyPair,
				request.getSink().getCsrList());

		/*
		 * Update the Common Part with the new current ConsentStatus and ResourceSet
		 */
		request.getSink().getCrPayload().getCommonPart()
				.setConsentStatus(request.getSink().getCsrPayload().getConsentStatus());
		request.getSink().getCrPayload().getCommonPart().setConsentStatusesSignature(sinkCsrListSignature);
		request.getSink().getCrPayload().getCommonPart()
				.setRsDescription(new RSDescription(request.getSink().getCsrPayload().getConsentResourceSet()));
		request.getSink().getCrPayload().getCommonPart().setMat(now);

		ConsentRecordSigned signedSinkCr = cryptoService.signConsentRecord(accountKeyPair,
				request.getSink().getCrPayload());
		signedSinkCr.setConsentStatusList(request.getSink().getCsrList());

		/*
		 * Source
		 */
		ConsentStatusRecordSigned signedSourceCsr = cryptoService.signConsentStatusRecord(accountKeyPair,
				request.getSource().getCsrPayload());
		request.getSource().getCsrList().add(signedSourceCsr);

		Base64URL sourceCsrListSignature = cryptoService.signConsentStatusRecordsList(accountKeyPair,
				request.getSource().getCsrList());

		request.getSource().getCrPayload().getCommonPart().setConsentStatusesSignature(sourceCsrListSignature);

		/*
		 * Update the Common Part with the new current ConsentStatus and ResourceSet
		 */
		request.getSource().getCrPayload().getCommonPart()
				.setConsentStatus(request.getSource().getCsrPayload().getConsentStatus());
		request.getSource().getCrPayload().getCommonPart()
				.setRsDescription(new RSDescription(request.getSource().getCsrPayload().getConsentResourceSet()));
		request.getSource().getCrPayload().getCommonPart().setMat(now);

		ConsentRecordSigned signedSourceCr = cryptoService.signConsentRecord(accountKeyPair,
				request.getSource().getCrPayload());
		signedSourceCr.setConsentStatusList(request.getSource().getCsrList());

		return ResponseEntity.ok(new ThirdPartyReuseConsentSignResponse(
				new WithinServiceConsentSignResponse(signedSinkCr, signedSinkCsr),
				new WithinServiceConsentSignResponse(signedSourceCr, signedSourceCsr)));

	}

	@Operation(summary = "Sign input Consent Status Record Payload.", description = "Signs constructed record's with Account owner's key.", tags = {
			"Service Linking" }, responses = {
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

		/*
		 * Get Account Key Pair
		 */
		RSAKey accountKeyPair = cryptoService.getKeyPairByAccountId(accountId);

		return ResponseEntity.ok(cryptoService.signConsentStatusRecord(accountKeyPair, consentStatusRecord));

	}

	@Operation(summary = "Sign input Consent Status Record List.", description = "Signs constructed Conssent Status Records List with Account owner's key.", tags = {
			"Service Linking" }, responses = {
					@ApiResponse(description = "Returns the Base64 Signature Consent Status Records List with the private part of the key pair matching with the input Account Id.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WithinServiceConsentSignResponse.class))) })
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

		/*
		 * Get Account Key Pair
		 */
		RSAKey accountKeyPair = cryptoService.getKeyPairByAccountId(accountId);

		return ResponseEntity.ok(cryptoService.signConsentStatusRecordsList(accountKeyPair, csrList));

	}

}
