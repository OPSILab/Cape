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
package it.eng.opsi.cape.accountmanager.repository;

import java.util.Optional;

import it.eng.opsi.cape.accountmanager.model.Account;
import it.eng.opsi.cape.accountmanager.model.AccountInfo;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordPayload;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordAlreadyPresentException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;

public interface AccountCustomRepository {

	public Optional<AccountInfo> updateAccountInfo(String accountId, AccountInfo accountInfo);

	public Optional<Account> updateAccount(String accountId, Account account);

	public Optional<ServiceLinkRecordPayload> addSlrPartialPayload(String accountId,
			ServiceLinkRecordPayload slrPayload)
			throws AccountNotFoundException, ServiceLinkRecordAlreadyPresentException;

	public Optional<Long> removeSlrPartialPayload(String accountId, String slrId)
			throws AccountNotFoundException, ServiceLinkRecordNotFoundException;

	public Optional<ServiceLinkRecordDoubleSigned> storeFinalSlr(String accountId,
			ServiceLinkRecordDoubleSigned doubleSignedSlr) throws AccountNotFoundException, // ServiceLinkRecordAlreadyPresentException,
			ServiceLinkRecordNotFoundException;

}
