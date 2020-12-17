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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
	public ResponseEntity<Object> getUserDetails(@RequestParam(name = "access_token") String token) {

		Object userDetails = clientService.getIdmUserDetail(token);

		return ResponseEntity.ok(userDetails);

	}

//	private final FiwareIDMAuthenticationManager idmAuthManager;
//
//	@Autowired
//	public AuthenticationController(ApplicationProperties configuration) {
//		this.configuration = configuration;
//		this.idmAuthManager = FiwareIDMAuthenticationManager.getInstance(new IDMAuthenticationConfiguration(
//				FiwareIDMVersion.fromString(this.configuration.getIdm().getVersion()),
//				this.configuration.getIdm().getProtocol(), this.configuration.getIdm().getHost(), -1,
//				this.configuration.getIdm().getClientId(), this.configuration.getIdm().getClientSecret(),
//				this.configuration.getIdm().getRedirectUri(), "", "", -1));
//	}
//
//	@PostMapping(value = "/login")
//	public ResponseEntity<JSONObject> login(@RequestParam(value = "code") String code, HttpServletRequest httpRequest)
//			throws Exception {
//
//		String token = null;
//
//		if (StringUtils.isBlank(code))
//			return ResponseEntity.badRequest().build();
//
//		Token t = (Token) idmAuthManager.login(null, null, code);
//
//		token = t.getAccess_token();
//
//		String refresh_token = t.getRefresh_token();
//
//		if (token != null && ((String) token).trim().length() > 0) {
//			UserInfo info = idmAuthManager.getUserInfo(t.getAccess_token());
//			JSONObject response = new JSONObject();
//			response.put("userId", info.getId());
//			response.put("name", info.getDisplayName());
//			response.put("token", token);
//
//			return ResponseEntity.ok(response);
//
//		} else {
//
//			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
//					"The IdM returned no token, check if input credentials are correct");
//
//		}
//
//	}

}
