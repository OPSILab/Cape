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
