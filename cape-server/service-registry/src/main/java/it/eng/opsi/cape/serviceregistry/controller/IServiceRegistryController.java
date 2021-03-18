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
package it.eng.opsi.cape.serviceregistry.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import it.eng.opsi.cape.exception.DatasetIdNotFoundException;
import it.eng.opsi.cape.exception.PurposeIdNotFoundException;
import it.eng.opsi.cape.exception.ServiceNotEditableException;
import it.eng.opsi.cape.exception.ServiceNotFoundException;
import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;
import it.eng.opsi.cape.serviceregistry.model.ServiceReport;
import it.eng.opsi.cape.serviceregistry.model.ServiceReportGroupingCriteria;

public interface IServiceRegistryController {

	public abstract ResponseEntity<List<ServiceEntry>> getServices(String serviceName, String serviceUrl,
			String businessId, Boolean onlyRegistered) throws ServiceNotFoundException;

	public abstract ResponseEntity<ServiceEntry> getServiceById(String serviceId, Boolean onlyRegistered) throws ServiceNotFoundException;

	public abstract ResponseEntity<String> getServicesCount(Boolean onlyRegistered);

	public abstract ResponseEntity<ServiceEntry> createService(ServiceEntry service);

	public abstract ResponseEntity<ServiceEntry> updateService(String serviceId, ServiceEntry service)
			throws ServiceNotFoundException, ServiceNotEditableException;

	public abstract ResponseEntity<Object> deleteService(String serviceId, Boolean deleteOnlyCertificate)
			throws ServiceNotFoundException;

	public abstract ResponseEntity<List<DataMapping>> getDataMappingListByServiceIdAndDatasetId(String serviceId,
			String datasetId) throws DatasetIdNotFoundException, ServiceNotFoundException, PurposeIdNotFoundException;

	public abstract ResponseEntity<List<DataMapping>> getDataMappingListByServiceIdAndPurposeId(String serviceId,
			String purposeId) throws ServiceNotFoundException, PurposeIdNotFoundException, DatasetIdNotFoundException;

	public abstract ResponseEntity<ProcessingBasis> getProcessingBasisByServiceIdAndPurposeId(String serviceId,
			String purposeId) throws ServiceNotFoundException, PurposeIdNotFoundException;

	public abstract ResponseEntity<List<ServiceReport>> getServiceReport(ServiceReportGroupingCriteria groupBy);

}
