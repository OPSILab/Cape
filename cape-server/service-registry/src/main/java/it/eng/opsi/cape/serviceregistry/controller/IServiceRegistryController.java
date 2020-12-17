/*******************************************************************************
 * CaPe - a Consent Based Personal Data Suite
 *   Copyright (C) 2019 Engineering Ingegneria Informatica S.p.A.
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

	public abstract ResponseEntity<String> getServicesCount();

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
