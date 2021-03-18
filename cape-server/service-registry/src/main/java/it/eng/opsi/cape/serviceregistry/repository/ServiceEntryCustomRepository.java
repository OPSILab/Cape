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
package it.eng.opsi.cape.serviceregistry.repository;

import java.util.List;
import java.util.Optional;

import it.eng.opsi.cape.exception.DatasetIdNotFoundException;
import it.eng.opsi.cape.exception.PurposeIdNotFoundException;
import it.eng.opsi.cape.exception.ServiceNotFoundException;
import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;
import it.eng.opsi.cape.serviceregistry.model.ServiceReport;
import it.eng.opsi.cape.serviceregistry.model.ServiceReportGroupingCriteria;

public interface ServiceEntryCustomRepository {

	public Long getServicesCount(Boolean onlyRegistered);

	public Optional<ServiceEntry> updateService(String serviceId, ServiceEntry service);

	public List<DataMapping> getDatasetDataMappingByServiceIdAndPurposeId(String service, String purposeId)
			throws ServiceNotFoundException, PurposeIdNotFoundException, DatasetIdNotFoundException;

	public List<ServiceReport> getServiceReport(ServiceReportGroupingCriteria groupBy);

	public List<DataMapping> getDatasetDataMappingByServiceIdAndDatasetId(String serviceId, String datasetId)
			throws ServiceNotFoundException, PurposeIdNotFoundException, DatasetIdNotFoundException;

	public ProcessingBasis getProcessingBasisByServiceIdAndPurposeId(String serviceId, String purposeId)
			throws ServiceNotFoundException, PurposeIdNotFoundException;
}
