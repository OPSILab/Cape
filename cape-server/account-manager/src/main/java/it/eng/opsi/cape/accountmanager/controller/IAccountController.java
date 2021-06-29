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
package it.eng.opsi.cape.accountmanager.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;

import it.eng.opsi.cape.accountmanager.model.Account;
import it.eng.opsi.cape.accountmanager.model.AccountExport;
import it.eng.opsi.cape.accountmanager.model.AccountInfo;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.exception.AccountManagerException;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.AuditLogNotFoundException;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkStatusRecordNotFoundException;

public interface IAccountController {

	public abstract ResponseEntity<Account> createAccount(Account account) throws JOSEException;

	public abstract ResponseEntity<Account> getAccount(String accountId) throws AccountNotFoundException;

	public abstract ResponseEntity<Account> deleteAccount(String accountId) throws AccountNotFoundException, AccountManagerException, ServiceDescriptionNotFoundException;

	public abstract ResponseEntity<Account> updateAccount(String accountId, @Valid Account account)
			throws AccountNotFoundException;

	public abstract ResponseEntity<AccountExport> exportAccount(String accountId) throws AccountNotFoundException, AccountManagerException, ServiceDescriptionNotFoundException, AuditLogNotFoundException;

	public abstract ResponseEntity<AccountInfo> getAccountInfo(String accountId) throws AccountNotFoundException;

	public abstract ResponseEntity<AccountInfo> updateAccountInfo(String accountId, AccountInfo accountInfo)
			throws AccountNotFoundException;

	public abstract ResponseEntity<List<ServiceLinkRecordDoubleSigned>> getServiceLinkRecords(String accountId)
			throws AccountNotFoundException;

	public abstract ResponseEntity<ServiceLinkRecordDoubleSigned> getServiceLinkRecord(String accountId, String slrId)
			throws ServiceLinkRecordNotFoundException;

	public abstract ResponseEntity<ServiceLinkRecordDoubleSigned> getServiceLinkByAccountIdOrUsernameAndServiceId(
			String accountId, String serviceId) throws ServiceLinkRecordNotFoundException;

	public abstract ResponseEntity<ServiceLinkRecordDoubleSigned> getServiceLinkRecordBySurrogateIdAndServiceId(
			String surrogateId, String serviceId) throws ServiceLinkRecordNotFoundException;

	public abstract ResponseEntity<ServiceLinkRecordDoubleSigned> getServiceLinkRecordBySurrogateId(String surrogateId)
			throws ServiceLinkRecordNotFoundException;

	public abstract ResponseEntity<List<ServiceLinkStatusRecordSigned>> getServiceLinkStatusRecords(String accountId,
			String slrId) throws ServiceLinkRecordNotFoundException;

	public abstract ResponseEntity<ServiceLinkStatusRecordSigned> getServiceLinkStatusRecord(String accountId,
			String slrId, String ssrId) throws ServiceLinkStatusRecordNotFoundException;

	public abstract ResponseEntity<ServiceLinkStatusRecordSigned> getLastServiceLinkStatusRecord(String accountId,
			String slrId) throws ServiceLinkStatusRecordNotFoundException;

	public abstract ResponseEntity<String> getAccountIdFromSlrIdAndSurrogateId(String surrogateId, String slrId)
			throws ServiceLinkStatusRecordNotFoundException, ServiceLinkRecordNotFoundException;

	public abstract byte[] exportAndDownloadAccount(String accountId) throws AccountNotFoundException,
			AccountManagerException, ServiceDescriptionNotFoundException, AuditLogNotFoundException, JsonProcessingException;

}
