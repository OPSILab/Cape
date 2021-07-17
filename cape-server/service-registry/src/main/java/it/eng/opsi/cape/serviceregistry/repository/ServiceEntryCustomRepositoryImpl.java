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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import it.eng.opsi.cape.exception.DatasetIdNotFoundException;
import it.eng.opsi.cape.exception.PurposeIdNotFoundException;
import it.eng.opsi.cape.exception.ServiceNotFoundException;
import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.Dataset;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;
import it.eng.opsi.cape.serviceregistry.model.ServiceReport;
import it.eng.opsi.cape.serviceregistry.model.ServiceReportGroupingCriteria;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

		Set<String> requiredDatasets = matchingPurpose.getRequiredDatasets();

		for (String requiredDatasetId : requiredDatasets) {

			Dataset matchingDataset = service.getIsDescribedAt().stream()
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

		List<Dataset> datasets = service.getIsDescribedAt();

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

	@Override
	public Optional<ServiceEntry> getServiceByServiceName(String serviceName, Boolean onlyRegistered,
			Boolean withSignature, Boolean withCertificate) {

		Query q = query(where("name").is(serviceName));

		if (onlyRegistered)
			q.addCriteria(where("serviceInstance.cert").ne(null));
		if (!withSignature)
			q.fields().exclude("serviceDescriptionSignature");
		if (!withCertificate)
			q.fields().exclude("serviceInstance.cert");

		return Optional.ofNullable(template.findOne(q, ServiceEntry.class));
	}

	@Override
	public List<ServiceEntry> getServices(Boolean onlyRegistered, Boolean withSignature, Boolean withCertificate) {

		Query q = new Query();

		if (onlyRegistered)
			q.addCriteria(where("serviceInstance.cert").ne(null));
		if (!withSignature)
			q.fields().exclude("serviceDescriptionSignature");
		if (!withCertificate)
			q.fields().exclude("serviceInstance.cert");

		return template.find(q, ServiceEntry.class);

	}

	@Override
	public Optional<ServiceEntry> getServiceByServiceUrl(String serviceUrl, Boolean onlyRegistered,
			Boolean withSignature, Boolean withCertificate) {

		Query q = query(where("identifier").is(serviceUrl));

		if (onlyRegistered)
			q.addCriteria(where("serviceInstance.cert").ne(null));
		if (!withSignature)
			q.fields().exclude("serviceDescriptionSignature");
		if (!withCertificate)
			q.fields().exclude("serviceInstance.cert");

		return Optional.ofNullable(template.findOne(q, ServiceEntry.class));
	}
	
	
	
	

	@Override
	public List<ServiceEntry> getServicesByBusinessId(String businessId, Boolean onlyRegistered, Boolean withSignature,
			Boolean withCertificate) {

		Query q = query(where("serviceInstance.serviceProvider.businessId").is(businessId));

		if (onlyRegistered)
			q.addCriteria(where("serviceInstance.cert").ne(null));
		if (!withSignature)
			q.fields().exclude("serviceDescriptionSignature");
		if (!withCertificate)
			q.fields().exclude("serviceInstance.cert");

		return template.find(q, ServiceEntry.class);

	}

}
