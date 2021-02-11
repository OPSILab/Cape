package it.eng.opsi.cape.serviceregistry.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import it.eng.opsi.cape.exception.DatasetIdNotFoundException;
import it.eng.opsi.cape.exception.PurposeIdNotFoundException;
import it.eng.opsi.cape.exception.ServiceNotFoundException;
import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.IsDescribedAt;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;
import it.eng.opsi.cape.serviceregistry.model.ServiceReport;
import it.eng.opsi.cape.serviceregistry.model.ServiceReportGroupingCriteria;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceEntryCustomRepositoryImpl implements ServiceEntryCustomRepository {

	@Autowired
	MongoTemplate template;

	@Override
	public Long getServicesCount(Boolean onlyRegistered) {

		if (onlyRegistered)
			return template.count(query(where("serviceInstance.cert").ne(null)), ServiceEntry.class);
		else
			return template.count(query(new Criteria()), ServiceEntry.class);
	}

	@Override
	public Optional<ServiceEntry> updateService(String serviceId, ServiceEntry service) {

		ServiceEntry updatedService = template.findAndReplace(query(where("serviceId").is(serviceId)), service);

		return Optional.ofNullable(updatedService);
	}

	@Override
	public List<DataMapping> getDatasetDataMappingByServiceIdAndPurposeId(String serviceId, String purposeId)
			throws ServiceNotFoundException, PurposeIdNotFoundException, DatasetIdNotFoundException {

		List<DataMapping> result = new ArrayList<DataMapping>();
		ServiceEntry service = template.findOne(query(where("serviceId").is(serviceId)), ServiceEntry.class);

		if (service == null)
			throw new ServiceNotFoundException("The service Entry with id: " + serviceId + " was not found");

		List<ProcessingBasis> purposes = service.getProcessingBases();

		ProcessingBasis matchingPurpose = purposes.stream().filter(p -> p.getPurposeId().equals(purposeId)).findFirst()
				.orElseThrow(() -> new PurposeIdNotFoundException(
						"The purpose with id: " + purposeId + " was not found for the Service Id: " + serviceId));

		List<String> requiredDatasets = matchingPurpose.getRequiredDatasets();

		for (String requiredDatasetId : requiredDatasets) {

			IsDescribedAt matchingDataset = service.getIsDescribedAt().stream()
					.filter(d -> d.getDatasetId().equals(requiredDatasetId)).findFirst()
					.orElseThrow(() -> new DatasetIdNotFoundException("The required dataset with id: "
							+ requiredDatasetId + " was not found in the Service Entry with id: " + serviceId));
			result.addAll(matchingDataset.getDataMapping());
		}

		return result;
	}

	@Override
	public List<DataMapping> getDatasetDataMappingByServiceIdAndDatasetId(String serviceId, String datasetId)
			throws ServiceNotFoundException, DatasetIdNotFoundException {

		ServiceEntry service = template.findOne(query(where("serviceId").is(serviceId)), ServiceEntry.class);

		if (service == null)
			throw new ServiceNotFoundException("The service Entry with id: " + serviceId + " was not found");

		List<IsDescribedAt> datasets = service.getIsDescribedAt();

		return datasets.stream().filter(p -> p.getDatasetId().equals(datasetId)).findFirst()
				.orElseThrow(() -> new DatasetIdNotFoundException(
						"The Data with id: " + datasetId + " was not found for the Service Id: " + serviceId))
				.getDataMapping();

	}

	@Override
	public ProcessingBasis getProcessingBasisByServiceIdAndPurposeId(String serviceId, String purposeId)
			throws ServiceNotFoundException, PurposeIdNotFoundException {

		ServiceEntry service = template.findOne(query(where("serviceId").is(serviceId)), ServiceEntry.class);

		if (service == null)
			throw new ServiceNotFoundException("The service Entry with id: " + serviceId + " was not found");

		List<ProcessingBasis> purposes = service.getProcessingBases();

		return purposes.stream().filter(p -> p.getPurposeId().equals(purposeId)).findFirst()
				.orElseThrow(() -> new PurposeIdNotFoundException(
						"The purpose with id: " + purposeId + " was not found for the Service Id: " + serviceId));
	}

	@Override
	public List<ServiceReport> getServiceReport(ServiceReportGroupingCriteria groupBy) {

		Aggregation agg = newAggregation(unwind("sector"),
				group(groupBy.getText()).first(groupBy.getText()).as("category").count().as("count"),
				project("count", "category"));

		AggregationResults<ServiceReport> result = template.aggregate(agg, ServiceEntry.class, ServiceReport.class);
		return result.getMappedResults();
	}

}
