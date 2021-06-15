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

import org.springframework.data.mongodb.repository.MongoRepository;

import it.eng.opsi.cape.consentmanager.model.ConsentForm;

public interface ConsentFormRepository extends MongoRepository<ConsentForm, String> {

	List<ConsentForm> findBySurrogateIdAndSinkServiceIdAndUsageRules_purposeId(String surrogateId, String sinkId,
			String purposeId);

	List<ConsentForm> findBySurrogateIdAndSinkServiceIdAndSourceServiceIdAndUsageRules_purposeId(String surrogateId,
			String sinkId, String sourceId, String purposeId);

	Long deleteConsentFormBySurrogateId(String surrogateId);
}
