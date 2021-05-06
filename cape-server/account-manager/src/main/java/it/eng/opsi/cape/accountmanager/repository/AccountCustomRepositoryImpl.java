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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;

import it.eng.opsi.cape.accountmanager.model.Account;
import it.eng.opsi.cape.accountmanager.model.AccountInfo;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordDoubleSigned.ServiceLinkRecordSignature;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordAlreadyPresentException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordPayload;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkStatusRecordSigned;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class AccountCustomRepositoryImpl implements AccountCustomRepository {

	@Autowired
	MongoTemplate template;

	@Override
	public Optional<Account> updateAccount(String accountId, Account account) {

		Query q = query(new Criteria().orOperator(where("_id").is(accountId), where("username").is(accountId)));
		q.fields().include("accountInfo");

		Account updatedAccount = template.findAndModify(q,
				new Update().set("modified", ZonedDateTime.now()).set("language", account.getLanguage())
						.set("notification", account.getNotification()),
				new FindAndModifyOptions().returnNew(true), Account.class);

		return Optional.ofNullable(updatedAccount);

	}

	@Override
	public Optional<AccountInfo> updateAccountInfo(String accountId, AccountInfo accountInfo) {

		Query q = query(new Criteria().orOperator(where("_id").is(accountId), where("username").is(accountId)));
		q.fields().include("accountInfo");

		Account updatedAccount = template.findAndModify(q,
				new Update().set("accountInfo", accountInfo).set("modified", ZonedDateTime.now()),
				new FindAndModifyOptions().returnNew(true), Account.class);

		return Optional.ofNullable(updatedAccount.getAccountInfo());

	}

	@Override
	public Optional<ServiceLinkRecordPayload> addSlrPartialPayload(String accountId,
			ServiceLinkRecordPayload slrPayload)
			throws AccountNotFoundException, ServiceLinkRecordAlreadyPresentException {

		Query q = query(new Criteria().orOperator(where("_id").is(accountId), where("username").is(accountId)));

		Account existingAccount = template.findOne(q, Account.class);

		if (existingAccount == null)
			throw new AccountNotFoundException("The Account with id: " + accountId + " was not found");

		q.addCriteria(new Criteria().and("serviceLinkRecords.payload.serviceId").ne(slrPayload.getServiceId())
				.and("serviceLinkRecords.payload.surrogateId").ne(slrPayload.getSurrogateId()));
//		q.fields().include("serviceLinkRecords");

		UpdateResult result = template.updateFirst(q, new Update().set("modified", ZonedDateTime.now())
				.addToSet("serviceLinkRecords", new ServiceLinkRecordDoubleSigned(slrPayload,
						new ArrayList<ServiceLinkRecordSignature>(), new ArrayList<ServiceLinkStatusRecordSigned>())),
				Account.class);

		if (result.getMatchedCount() == 0)
			throw new ServiceLinkRecordAlreadyPresentException("There is already a Service Link Record for Service Id: "
					+ slrPayload.getServiceId() + " and Surrogate Id: " + slrPayload.getSurrogateId());

		if (result.getMatchedCount() == 1 && result.getModifiedCount() == 0)
			throw new ServiceLinkRecordAlreadyPresentException(
					"The Service Link Record with id: " + slrPayload.getSlrId() + " is already existing");

		return Optional.of(slrPayload);
	}

	@Override
	public Optional<Long> removeSlrPartialPayload(String accountId, String slrId)
			throws AccountNotFoundException, ServiceLinkRecordNotFoundException {

		Query q = query(new Criteria().orOperator(where("_id").is(accountId), where("username").is(accountId)));

		Account existingAccount = template.findOne(q, Account.class);

		if (existingAccount == null)
			throw new AccountNotFoundException("The Account with id: " + accountId + " was not found");

		UpdateResult result = template.updateFirst(q, new Update().set("modified", ZonedDateTime.now())
				.pull("serviceLinkRecords", where("payload._id").is(slrId)), Account.class);

		if (result.getMatchedCount() == 1 && result.getModifiedCount() == 0)
			throw new ServiceLinkRecordNotFoundException("No Service Link Record with id: " + slrId + " was found");

		return Optional.of(result.getModifiedCount());
	}

	@Override
	public Optional<ServiceLinkRecordDoubleSigned> storeFinalSlr(String accountId,
			ServiceLinkRecordDoubleSigned doubleSignedSlr) throws AccountNotFoundException, // ServiceLinkRecordAlreadyPresentException,
			ServiceLinkRecordNotFoundException {

		ServiceLinkRecordPayload slrPayload = doubleSignedSlr.getPayload();

		Query q = query(new Criteria().andOperator(
				new Criteria().orOperator(where("_id").is(accountId), where("username").is(accountId)),
				Criteria.where("serviceLinkRecords.payload._id").is(new ObjectId(slrPayload.getSlrId()))));
//		q.fields().include("serviceLinkRecords");

		UpdateResult result = template.updateFirst(q,
				new Update().set("modified", ZonedDateTime.now()).set("serviceLinkRecords.$", doubleSignedSlr),
				Account.class);

		if (result.getMatchedCount() == 0)
			throw new ServiceLinkRecordNotFoundException("The Service Link Record with id: " + slrPayload.getSlrId()
					+ " for Account with Id: " + accountId + " was not found");

		return Optional.of(doubleSignedSlr);
	}

}
