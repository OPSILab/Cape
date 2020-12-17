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

	public Long getServicesCount();

	public Optional<ServiceEntry> updateService(String serviceId, ServiceEntry service);

	public List<DataMapping> getDatasetDataMappingByServiceIdAndPurposeId(String service, String purposeId)
			throws ServiceNotFoundException, PurposeIdNotFoundException, DatasetIdNotFoundException;

	public List<ServiceReport> getServiceReport(ServiceReportGroupingCriteria groupBy);

	public List<DataMapping> getDatasetDataMappingByServiceIdAndDatasetId(String serviceId, String datasetId)
			throws ServiceNotFoundException, PurposeIdNotFoundException, DatasetIdNotFoundException;

	public ProcessingBasis getProcessingBasisByServiceIdAndPurposeId(String serviceId, String purposeId)
			throws ServiceNotFoundException, PurposeIdNotFoundException;
}
