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

import it.eng.opsi.cape.consentmanager.model.ConsentRecordSigned;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordStatusEnum;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;

public interface ConsentRecordCustomRepository {

	public List<ConsentRecordSigned> findByAccountIdAndQuery(String accountId, String consentId, String serviceId,
			String sourceServiceId, String datasetId, ConsentRecordStatusEnum status, String purposeId,
			String purposeName, PurposeCategory purposeCategory, ProcessingCategory processingCategory);

	public List<ConsentRecordSigned> findByServiceIdAndQuery(String serviceId, String sourceServiceId, String datasetId,
			ConsentRecordStatusEnum status, String purposeId, String purposeName, PurposeCategory purposeCategory,
			ProcessingCategory processingCategory);

	public List<ConsentRecordSigned> findBySurrogateIdAndQuery(String surrogateId, String serviceId,
			String sourceServiceId, String datasetId, ConsentRecordStatusEnum status, String purposeId,
			String purposeName, PurposeCategory purposeCategory, ProcessingCategory processingCategory);

	public List<ConsentRecordSigned> findByBusinessIdAndQuery(String businessId, String surrogateId, String serviceId,
			String sourceServiceId, String datasetId, ConsentRecordStatusEnum status, String purposeId,
			String purposeName, PurposeCategory purposeCategory, ProcessingCategory processingCategory);

}
