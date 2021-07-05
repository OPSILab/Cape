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
package it.eng.opsi.cape.servicemanager.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.nimbusds.jose.jwk.RSAKey;

import it.eng.opsi.cape.servicemanager.model.DataOperatorDescription;

public interface DataOperatorDescriptionRepository extends MongoRepository<DataOperatorDescription, String> {

	public Optional<DataOperatorDescription> findByOperatorId(String operatorId);

	public Long deleteDataOperatorDescriptionByOperatorId(String operatorId);

	@Aggregation(pipeline = { "{ $match: { operatorId : ?0}}", "{ $replaceRoot: { newRoot : '$keyPair'}}" })
	public Optional<RSAKey> getKeyPairByOperatorId(String operatorId);
}
