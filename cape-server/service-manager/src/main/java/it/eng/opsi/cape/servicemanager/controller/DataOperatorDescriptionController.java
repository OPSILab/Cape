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
import it.eng.opsi.cape.exception.DataOperatorDescriptionNotFoundException;
import it.eng.opsi.cape.servicemanager.model.DataOperatorDescription;
import it.eng.opsi.cape.servicemanager.model.consenting.AuthorisationTokenPayload;
import it.eng.opsi.cape.servicemanager.model.consenting.AuthorisationTokenResponse;
import it.eng.opsi.cape.servicemanager.repository.DataOperatorDescriptionRepository;
import it.eng.opsi.cape.servicemanager.service.CryptoService;
import lombok.extern.slf4j.Slf4j;

@OpenAPIDefinition(tags = {
		@Tag(name = "Data Operator Description", description = "Service Manager APIs to manage Data Operator Description, namely the Description of the Cape instance acting as Data Operator.") }, info = @Info(title = "CaPe Service Manager API", description = "Service Manager API used by CaPe compliant services.", version = "2.0"))

@RestController
@RequestMapping("/api/v2")
@Slf4j
public class DataOperatorDescriptionController implements IDataOperatorDescriptionController {

	@Autowired
	private DataOperatorDescriptionRepository repository;

	@Autowired
	CryptoService cryptoService;

	@Operation(summary = "Create a new Data Operator Description for CaPe.", tags = { "Data Operator Description" }, responses = {
			@ApiResponse(description = "Returns 201 Created with the created Operator Description.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataOperatorDescription.class))) })
	@Override
	@PostMapping(value = "/dataOperatorDescriptions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DataOperatorDescription> createDataOperatorDescription(
			@Valid @RequestBody DataOperatorDescription operator) throws JOSEException, CertIOException, IOException {

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

		DataOperatorDescription storedDescription = repository.insert(operator);

		return ResponseEntity.created(URI.create(
				operator.getOperatorUrls().getDomain() + "/dataOperatorDescriptions/" + storedDescription.getOperatorId()))
				.body(storedDescription);

	}

	@Operation(summary = "Get Data Operator Description for CaPe by Operator Id", tags = {
			"Data Operator Description" }, responses = {
					@ApiResponse(description = "Returns the requested Data Operator Description.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataOperatorDescription.class))) })
	@Override
	@GetMapping(value = "/dataOperatorDescriptions/{operatorId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DataOperatorDescription> getDataOperatorDescription(@PathVariable("operatorId") String operatorId)
			throws DataOperatorDescriptionNotFoundException {

		DataOperatorDescription description = repository.findByOperatorId(operatorId).orElseThrow(
				() -> new DataOperatorDescriptionNotFoundException("No Data Operator found with Id: " + operatorId));

		return ResponseEntity.ok().body(description);
	}

	@Operation(summary = "Delete Data Operator Description for CaPe by Operator Id.", tags = {
			"Data Operator Description" }, responses = {
					@ApiResponse(description = "Returns No Content.", responseCode = "204") })
	@Override
	@DeleteMapping(value = "/dataOperatorDescriptions/{operatorId}")
	public ResponseEntity<String> deleteDataOperatorDescription(@PathVariable("operatorId") String operatorId)
			throws DataOperatorDescriptionNotFoundException {

		if (repository.deleteDataOperatorDescriptionByOperatorId(operatorId) == 0L)
			throw new DataOperatorDescriptionNotFoundException("No Data Operator with Id: " + operatorId + " found");

		return ResponseEntity.noContent().build();

	}

	@Operation(summary = "Sign the Authorisation Token paylod with the operator private key", tags = {
			"Data Operator Description" }, responses = {
					@ApiResponse(description = "Returns the .", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorisationTokenResponse.class))) })
	@Override
	@PostMapping(value = "/dataOperatorDescriptions/{operatorId}/signToken", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AuthorisationTokenResponse> signAuthorisationToken(
			@PathVariable("operatorId") String operatorId, @RequestBody AuthorisationTokenPayload payload)
			throws DataOperatorDescriptionNotFoundException, JsonProcessingException, JOSEException {

		DataOperatorDescription description = repository.findByOperatorId(operatorId).orElseThrow(
				() -> new DataOperatorDescriptionNotFoundException("No Data Operator found with Id: " + operatorId));

		return ResponseEntity.ok().body(cryptoService.signAuthorisationToken(description.getKeyPair(), payload));
	}

}
