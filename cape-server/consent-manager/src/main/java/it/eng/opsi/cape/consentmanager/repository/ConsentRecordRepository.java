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
package it.eng.opsi.cape.consentmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import it.eng.opsi.cape.consentmanager.model.ConsentRecordSigned;
import it.eng.opsi.cape.consentmanager.model.ConsentStatusRecordSigned;

public interface ConsentRecordRepository
		extends MongoRepository<ConsentRecordSigned, String>, ConsentRecordCustomRepository {

	public List<ConsentRecordSigned> findByPayload_commonPart_surrogateId(String surrogateId);

	public List<ConsentRecordSigned> findByPayload_commonPart_subjectId(String subjectId);

	public List<ConsentRecordSigned> findByPayload_commonPart_serviceProviderBusinessId(String businessId);
//
//	public List<ConsentRecordSigned> findByPayload_commonPart_serviceProviderBusinessIdAndPayload_commonPart_surrogateId(
//			String businessId, String surrogateId);
//
//	public List<ConsentRecordSigned> findByPayload_commonPart_serviceProviderBusinessIdAndPayload_commonPart_rsDescription_resourceSet_datasets_purposeIdOrderByPayload_commonPart_iatDesc(
//			String businessId, String purposeId);

	public List<ConsentRecordSigned> findByPayload_commonPart_surrogateIdAndPayload_commonPart_rsDescription_resourceSet_datasets_purposeIdOrderByPayload_commonPart_iatDesc(
			String surrogateId, String purposeId);

	public List<ConsentRecordSigned> findByPayload_commonPart_rsDescription_resourceSet_datasets_purposeIdOrderByPayload_commonPart_iatDesc(
			String purposeId);

	public Optional<ConsentRecordSigned> findByPayload_commonPart_surrogateIdAndPayload_commonPart_crId(
			String surrogateId, String crId);

	public List<ConsentRecordSigned> findByPayload_commonPart_surrogateIdAndPayload_commonPart_slrId(String surrogateId,
			String slrId);

	public List<ConsentRecordSigned> findByAccountId(String accountId);

	public List<ConsentRecordSigned> findByAccountIdAndPayload_commonPart_slrId(String accountId, String slrId);

	public Optional<ConsentRecordSigned> findByAccountIdAndPayload_commonPart_crId(String accountId, String crId);

	@Aggregation(pipeline = {
			"{ $match: { $and: [{'accountId': ?0},{'payload.commonPart.slrId': ?1}, {'payload.commonPart.crId': ?2}]}}",
			"{ $unwind : '$consentStatusList'}", "{ $replaceRoot: { newRoot : '$consentStatusList'}}" })
	public Optional<List<ConsentStatusRecordSigned>> getConsentStatusRecordsByAccountIdAndSlrIdAndCrId(String accountId,
			String slrId, String crId);

	@Aggregation(pipeline = {
			"{ $match: { $and: [{'accountId': ?0},{'payload.commonPart.slrId': ?1}, {'payload.commonPart.crId': ?2}]}}",
			"{ $unwind : '$consentStatusList'}", "{ $match: {'consentStatusList.payload.recordId': ?3 }}",
			"{ $replaceRoot: { newRoot : '$consentStatusList'}}" })
	public Optional<ConsentStatusRecordSigned> getConsentStatusRecordByAccountIdAndSlrIdAndCrIdAndRecordId(
			String accountId, String slrId, String crId, String recordId);

	@Aggregation(pipeline = {
			"{ $match: { $and: [{'accountId': ?0},{'payload.commonPart.slrId': ?1}, {'payload.commonPart.crId': ?2}]}}",
			"{ $unwind : '$consentStatusList'}", "{ $sort: { 'consentStatusList.payload.iat': -1}}", "{ $limit: 1 }",
			"{ $replaceRoot: { newRoot : '$consentStatusList'}}" })
	public Optional<ConsentStatusRecordSigned> getLastConsentStatusRecordByAccountIdAndSlrIdAndCrId(String accountId,
			String slrId, String crId);

	public Optional<ConsentRecordSigned[]> findByAccountIdAndPayload_commonPart_crPairId(String accountId,
			String crPairId);

	public Optional<ConsentRecordSigned[]> findByPayload_commonPart_surrogateIdAndPayload_commonPart_crPairId(
			String surrogateId, String crPairId);

	public Optional<ConsentRecordSigned[]> findByPayload_commonPart_crPairId(String crPairId);

	public Optional<ConsentRecordSigned[]> findByPayload_commonPart_rsDescription_resourceSet_rsId(String rsId);

	public Long deleteConsentRecordSignedByAccountId(String accountId);

}
