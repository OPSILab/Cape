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

	public abstract ResponseEntity<Account> deleteAccount(String accountId) throws AccountNotFoundException;

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
