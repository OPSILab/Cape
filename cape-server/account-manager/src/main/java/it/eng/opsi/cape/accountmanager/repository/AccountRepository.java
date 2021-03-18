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

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nimbusds.jose.jwk.RSAKey;

import it.eng.opsi.cape.accountmanager.model.Account;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkStatusRecordSigned;

public interface AccountRepository extends MongoRepository<Account, String>, AccountCustomRepository {

	public Optional<Account> findBy_idOrUsername(String id, String username);

	public Long deleteAccountBy_idOrUsername(String id, String username);

	// @Query(value="{ '$or': [{'_id': ?0}, {'username': ?1}]}", fields="
	// {'accountInfo': 1}")
	public Optional<AccountInfoOnly> getAccountInfoBy_idOrUsername(String id, String username);

//	@Query(value = "{ '$or': [{'_id': ?0}, {'username': ?1}]}", fields = " {'_id': 0, 'keyPair': 1}")
	@Aggregation(pipeline = { "{ $match: { $or: [{'_id': ?0}, {'username': ?1}]}}",
			"{ $replaceRoot: { newRoot : '$keyPair'}}" })
	public Optional<RSAKey> getKeyPairBy_idOrUsername(String id, String username);

	// @Query(value = "{ '$or': [{'_id': ?0}, {'username': ?1}]}", fields = "
	// {'serviceLinkRecords.payload': 1}")
	@Aggregation(pipeline = { "{ $match: { $or: [{'_id': ?0}, {'username': ?0}]}}",
			"{ $unwind : '$serviceLinkRecords'}", "{ $replaceRoot: { newRoot : '$serviceLinkRecords'}}" })
	public Optional<List<ServiceLinkRecordDoubleSigned>> getServiceLinksRecordsBy_idOrUsername(String accountId);

	@Aggregation(pipeline = { "{ $match: { $or: [{'_id': ?0}, {'username': ?0}]}}",
			"{ $unwind : '$serviceLinkRecords'}", "{ $match:  { 'serviceLinkRecords.payload.slrId': ?1}}}",
			"{ $replaceRoot: { newRoot : '$serviceLinkRecords'}}" })
	public Optional<ServiceLinkRecordDoubleSigned> getServiceLinkRecordByAccountIdOrUsernameAndSlrId(String accountId,
			String slrId);

	@Aggregation(pipeline = { "{ $unwind : '$serviceLinkRecords'}",
			"{ $match:  { 'serviceLinkRecords.payload.surrogateId': ?0}}",
			"{ $replaceRoot: { newRoot : '$serviceLinkRecords'}}" })
	public Optional<ServiceLinkRecordDoubleSigned> getServiceLinkRecordBySurrogateId(String surrogateId);

	@Aggregation(pipeline = { "{ $unwind : '$serviceLinkRecords'}",
			"{ $match:  { $and :[{ 'serviceLinkRecords.payload.surrogateId': ?0},{'serviceLinkRecords.payload.serviceId': ?1}]}}",
			"{ $replaceRoot : { newRoot : '$serviceLinkRecords'}}" })
	public Optional<ServiceLinkRecordDoubleSigned> getServiceLinkRecordBySurrogateIdAndServiceId(String surrogateId,
			String serviceId);

	@Aggregation(pipeline = { "{ $match: { '$or': [{'_id': ?0}, {'username': ?0}]}}",
			"{ $unwind : '$serviceLinkRecords'}", "{ $match: {'serviceLinkRecords.payload.serviceId': ?1 }}",
			"{$replaceRoot: { 'newRoot' : '$serviceLinkRecords'}}" })
	public Optional<ServiceLinkRecordDoubleSigned> getServiceLinkRecordByAccountIdOrUsernameAndServiceId(
			String accountId, String serviceId);

	@Aggregation(pipeline = { "{ $match: { $or: [{'_id': ?0}, {'username': ?0}]}}",
			"{ $unwind : '$serviceLinkRecords'}", "{ $match:  { 'serviceLinkRecords.payload.slrId': ?1}}}",
			"{ $unwind: '$serviceLinkRecords.serviceLinkStatuses'}",
			"{ $match: {'serviceLinkRecords.serviceLinkStatuses.payload._id': ?2 }}",
			"{ $replaceRoot: { newRoot : '$serviceLinkRecords.serviceLinkStatuses'}}" })
	public Optional<ServiceLinkStatusRecordSigned> getServiceLinkStatusRecordByAccountIdOrUsernameAndSlrIdAndSsrId(
			String accountId, String slrId, String ssrId);

	@Aggregation(pipeline = { "{ $match: { $or: [{'_id': ?0}, {'username': ?0}]}}",
			"{ $unwind : '$serviceLinkRecords'}", "{ $match:  { 'serviceLinkRecords.payload.slrId': ?1}}}",
			"{ $unwind: '$serviceLinkRecords.serviceLinkStatuses'}",
			"{ $replaceRoot: { newRoot : '$serviceLinkRecords.serviceLinkStatuses'}}" })
	public Optional<List<ServiceLinkStatusRecordSigned>> getServiceLinkStatusRecordsByAccountOrUsernameAndSlrId(
			String accountId, String slrId);

	@Aggregation(pipeline = { "{ $match: { $or: [{'_id': ?0}, {'username': ?0}]}}",
			"{ $unwind : '$serviceLinkRecords'}", "{ $match:  { 'serviceLinkRecords.payload.slrId': ?1}}}",
			"{ $unwind: '$serviceLinkRecords.serviceLinkStatuses'}",
			"{ $sort: { 'serviceLinkRecords.serviceLinkStatuses.payload.iat': -1}}", "{ $limit: 1 }",
			"{ $replaceRoot: { newRoot : '$serviceLinkRecords.serviceLinkStatuses'}}" })
	public Optional<ServiceLinkStatusRecordSigned> getLastServiceLinkStatusRecordByAccountOrUsernameAndSlrId(
			String accountId, String slrId);

	//@Query(value="{ $and: [{ 'serviceLinkRecords.payload._id': ?0},{'serviceLinkRecords.payload.surrogateId': ?1}]}", fields="{username : 1, _id : 0}")
	public Optional<Account> findByServiceLinkRecords_Payload_slrIdAndServiceLinkRecords_Payload_SurrogateId(String slrId, String surrogateId);
	
	
	
	
	
	
	
}
