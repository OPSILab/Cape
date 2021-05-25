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
package it.eng.opsi.cape.servicemanager.controller;

import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.eng.opsi.cape.exception.OperatorDescriptionNotFoundException;
import it.eng.opsi.cape.servicemanager.model.OperatorDescription;
import it.eng.opsi.cape.servicemanager.model.consenting.AuthorisationTokenPayload;
import it.eng.opsi.cape.servicemanager.model.consenting.AuthorisationTokenResponse;
import it.eng.opsi.cape.servicemanager.repository.OperatorDescriptionRepository;
import it.eng.opsi.cape.servicemanager.service.CryptoService;
import lombok.extern.slf4j.Slf4j;

@OpenAPIDefinition(tags = {
		@Tag(name = "Operator Description", description = "Service Manager APIs to manage Operator Description.") }, info = @Info(title = "CaPe Service Manager API", description = "Service Manager API used by CaPe compliant services.", version = "2.0"))

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

		// Derive domain Url from Base Url of LinkingUri
		if (StringUtils.isBlank(operator.getOperatorUrls().getDomain())) {
			Matcher matcher = Pattern.compile("^.+?[^\\/:](?=[?\\/]|$)")
					.matcher(operator.getOperatorUrls().getLinkingUri());
			if (matcher.find())
				operator.getOperatorUrls().setDomain(matcher.group(0));
		}

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
