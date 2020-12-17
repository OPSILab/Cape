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
package it.eng.opsi.cape.servicemanager.controller;

import java.io.IOException;
import java.net.URI;
import javax.validation.Valid;

import org.bouncycastle.cert.CertIOException;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.Base64URL;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import it.eng.opsi.cape.exception.OperatorDescriptionNotFoundException;
import it.eng.opsi.cape.servicemanager.model.OperatorDescription;
import it.eng.opsi.cape.servicemanager.model.consenting.AuthorisationTokenPayload;
import it.eng.opsi.cape.servicemanager.model.consenting.AuthorisationTokenResponse;
import it.eng.opsi.cape.servicemanager.repository.OperatorDescriptionRepository;
import it.eng.opsi.cape.servicemanager.service.CryptoService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v2")
@Slf4j
public class OperatorDescriptionController implements IOperatorDescriptionController {

	@Autowired
	private OperatorDescriptionRepository repository;

	@Autowired
	CryptoService cryptoService;

	@Operation(summary = "Create a new Operator Description for CaPe.", tags = { "Operator Description" }, responses = {
			@ApiResponse(description = "Returns 201 Created with the created Operator Description.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OperatorDescription.class))) })
	@Override
	@PostMapping(value = "/operatorDescriptions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OperatorDescription> createOperatorDescription(
			@Valid @RequestBody OperatorDescription operator) throws JOSEException, CertIOException, IOException {

		/*
		 * Create a new JWK Pair for Operator and a X509 certificate for Public Key and
		 * put all in OperatorDescription
		 */
		cryptoService.createOperatorKeyPairAndCert(operator);

		OperatorDescription storedDescription = repository.insert(operator);

		return ResponseEntity.created(URI.create(
				operator.getOperatorUrls().getDomain() + "/operatorDescriptions/" + storedDescription.getOperatorId()))
				.body(storedDescription);

	}

	@Operation(summary = "Get Operator Description for CaPe by Operator Id", tags = {
			"Operator Description" }, responses = {
					@ApiResponse(description = "Returns the requested Operator Description.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OperatorDescription.class))) })
	@Override
	@GetMapping(value = "/operatorDescriptions/{operatorId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OperatorDescription> getOperatorDescription(@PathVariable("operatorId") String operatorId)
			throws OperatorDescriptionNotFoundException {

		OperatorDescription description = repository.findByOperatorId(operatorId).orElseThrow(
				() -> new OperatorDescriptionNotFoundException("No operator found with Id: " + operatorId));

		return ResponseEntity.ok().body(description);
	}

	@Operation(summary = "Delete Operator Description for CaPe by Operator Id.", tags = {
			"Operator Description" }, responses = {
					@ApiResponse(description = "Returns No Content.", responseCode = "204") })
	@Override
	@DeleteMapping(value = "/operatorDescriptions/{operatorId}")
	public ResponseEntity<String> deleteOperatorDescription(@PathVariable("operatorId") String operatorId)
			throws OperatorDescriptionNotFoundException {

		if (repository.deleteOperatorDescriptionByOperatorId(operatorId) == 0L)
			throw new OperatorDescriptionNotFoundException("No Operator with Id: " + operatorId + " found");

		return ResponseEntity.noContent().build();

	}

	@Operation(summary = "Sign the Authorisation Token paylod with the operator private key", tags = {
			"Operator Description" }, responses = {
					@ApiResponse(description = "Returns the .", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorisationTokenResponse.class))) })
	@Override
	@PostMapping(value = "/operatorDescriptions/{operatorId}/signToken", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AuthorisationTokenResponse> signAuthorisationToken(
			@PathVariable("operatorId") String operatorId, @RequestBody AuthorisationTokenPayload payload)
			throws OperatorDescriptionNotFoundException, JsonProcessingException, JOSEException {

		OperatorDescription description = repository.findByOperatorId(operatorId).orElseThrow(
				() -> new OperatorDescriptionNotFoundException("No operator found with Id: " + operatorId));

		return ResponseEntity.ok().body(cryptoService.signAuthorisationToken(description.getKeyPair(), payload));
	}

}
