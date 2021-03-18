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
