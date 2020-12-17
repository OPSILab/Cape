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
package it.eng.opsi.cape.auditlogmanager.service;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import it.eng.opsi.cape.auditlogmanager.ApplicationProperties;
import it.eng.opsi.cape.exception.AuditLogException;

@Service
public class ClientService {

	private final ApplicationProperties appProperty;

	private final String acccountManagerHost;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	public ClientService(ApplicationProperties appProperty) {

		this.appProperty = appProperty;
		acccountManagerHost = this.appProperty.getCape().getAccountManager().getHost();
	}

	public String callGetAccountIdFromSlrIdAndSurrogateId(String slrId, String surrogateId)
			throws AccountNotFoundException, AuditLogException {

		RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
		ResponseEntity<String> response = restTemplate.getForEntity(
				acccountManagerHost + "/api/v2/users/{surrogateId}/servicelink/{slrId}", String.class, surrogateId, slrId);

		HttpStatus responseStatus = response.getStatusCode();

		if (responseStatus.is2xxSuccessful())
			return response.getBody();
		else if (responseStatus == HttpStatus.NOT_FOUND) {
			throw new AccountNotFoundException(
					"No account found associated to Slr Id: " + slrId + " and surrogate Id: " + surrogateId);
		} else
			throw new AuditLogException("There was an error while retrieving Account id from Account Manager");
	}

}
