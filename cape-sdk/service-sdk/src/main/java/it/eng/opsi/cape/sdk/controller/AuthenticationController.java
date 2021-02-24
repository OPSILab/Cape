/*******************************************************************************
 * CaPe - a Consent Based Personal Data Suite
 *   Copyright (C) 2019 Engineering Ingegneria Informatica S.p.A.
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
