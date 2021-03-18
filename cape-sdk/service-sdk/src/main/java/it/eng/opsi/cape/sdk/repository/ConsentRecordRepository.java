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

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSigned;

public interface ConsentRecordRepository
		extends MongoRepository<ConsentRecordSigned, String>, ConsentRecordCustomRepository {

	public ConsentRecordSigned[] findByPayload_commonPart_surrogateId(String surrogateId);

	public ConsentRecordSigned[] findByPayload_commonPart_subjectId(String subjectId);

	public ConsentRecordSigned[] findByPayload_commonPart_serviceProviderBusinessId(String businessId);

	public ConsentRecordSigned[] findByPayload_commonPart_surrogateIdAndPayload_commonPart_rsDescription_resourceSet_datasets_purposeIdOrderByPayload_commonPart_iatDesc(
			String surrogateId, String purposeId);

	public Optional<ConsentRecordSigned> findByPayload_commonPart_crId(String crId);

	public Optional<ConsentRecordSigned> findByPayload_commonPart_surrogateIdAndPayload_commonPart_crId(
			String surrogateId, String crId);

}
