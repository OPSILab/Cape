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

import it.eng.opsi.cape.exception.ConsentRecordNotFoundException;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordStatusEnum;
import it.eng.opsi.cape.sdk.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;

public interface ConsentRecordCustomRepository {

	public ConsentRecordSigned addStatusToCr(String crId, ConsentStatusRecordSigned csr)
			throws ConsentRecordNotFoundException;

	public List<ConsentRecordSigned> findByServiceIdAndQuery(String serviceId, String datasetId,
			ConsentRecordStatusEnum status, PurposeCategory purposeCategory, ProcessingCategory processingCategory);

	public List<ConsentRecordSigned> findBySurrogateIdAndQuery(String surrogateId, String datasetId,
			ConsentRecordStatusEnum status, PurposeCategory purposeCategory, ProcessingCategory processingCategory);

	public List<ConsentRecordSigned> findByBusinessIdAndQuery(String businessId, String serviceId, String datasetId,
			ConsentRecordStatusEnum status, PurposeCategory purposeCategory, ProcessingCategory processingCategory);

}
