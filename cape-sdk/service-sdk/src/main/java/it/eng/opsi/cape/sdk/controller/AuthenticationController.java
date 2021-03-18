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
package it.eng.opsi.cape.sdk.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import it.eng.opsi.cape.sdk.ApplicationProperties;
import it.eng.opsi.cape.sdk.service.ClientService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v2")
@Slf4j
public class AuthenticationController {

	@Autowired
	private ApplicationProperties configuration;

	@Autowired
	private ClientService clientService;

	@GetMapping(value = "/idm/user")
	public ResponseEntity<Object> getUserDetails(@RequestParam(name = "token") String token) {

		Object userDetails = clientService.getIdmUserDetail(token);

		return ResponseEntity.ok(userDetails);

	}

	@PostMapping(value = "/idm/oauth2/token")
	public ResponseEntity<Object> postCodeforToken(@RequestParam(name = "grant_type") String grantType,
			@RequestParam(name = "redirect_uri") String redirectUri, @RequestParam(name = "code") String code) {

		Object token = clientService.postCodeForToken(grantType, redirectUri, code);

		return ResponseEntity.ok(token);

	}

	@DeleteMapping(value = "/idm/auth/external_logout")
	public ResponseEntity<Object> externalLogout(@RequestParam(name = "client_id") String clientId) {

		Object response = clientService.externalLogout(clientId);

		return ResponseEntity.ok(response);

	}

}
