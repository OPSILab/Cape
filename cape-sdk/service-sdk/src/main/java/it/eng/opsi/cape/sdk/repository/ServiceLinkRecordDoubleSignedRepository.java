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
package it.eng.opsi.cape.sdk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkStatusRecordSigned;

public interface ServiceLinkRecordDoubleSignedRepository
		extends MongoRepository<ServiceLinkRecordDoubleSigned, String>, ServiceLinkRecordDoubleSignedCustomRepository {

	public List<ServiceLinkRecordDoubleSigned> findByPayload_ServiceId(String serviceId);

	public Optional<ServiceLinkRecordDoubleSigned> findByPayload_SlrId(String slrId);

	public Optional<ServiceLinkRecordDoubleSigned> findByPayload_SurrogateId(String surrogateId);

	public Optional<ServiceLinkRecordDoubleSigned> findByPayload_SurrogateIdAndPayload_ServiceId(String surrogateId,
			String serviceId);

	@Aggregation(pipeline = { "{ $match: {'payload.slrID': ?0}}", "{ $unwind : '$payload.serviceLinkStatuses'}",
			"{ $sort: { 'payload.serviceLinkStatuses.payload.iat': -1}}", "{ $limit: 1 }",
			"{ $replaceRoot: { newRoot : '$payload.serviceLinkStatuses'}}" })
	public Optional<ServiceLinkStatusRecordSigned> getLastServiceLinkStatusRecordBySlrId(String slrId);

}
