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

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;
import it.eng.opsi.cape.serviceregistry.model.ServiceReport;

public interface ServiceEntryRepository extends MongoRepository<ServiceEntry, String>, ServiceEntryCustomRepository {

	public Optional<ServiceEntry> findByServiceId(String serviceId);

	@Query(value = "{ $and:[{ 'serviceId': ?0}, { 'serviceInstance.cert':{$ne:null}}]}")
	public Optional<ServiceEntry> findRegisteredByServiceId(String serviceId);

	@Query(value = "{ 'serviceInstance.cert':{$ne:null}}")
	public List<ServiceEntry> findAllRegisteredServices();

	@Query(value = "{ name: { $regex : ?0, $options: i}}")
	public Optional<ServiceEntry> findByServiceName(String serviceName);

	@Query(value = "{ identifier: ?0}")
	public Optional<ServiceEntry> findByServiceUrl(String serviceUrl);

	@Query(value = "{ 'serviceInstance.serviceProvider.businessId': ?0}")
	public List<ServiceEntry> findByServiceProviderBusinessId(String businessId);

	@Query(value = "{ $and:[{ 'serviceInstance.serviceProvider.businessId': ?0}, { 'serviceInstance.cert':{$ne:null}}]}")
	public List<ServiceEntry> findRegisteredServicesByServiceProviderBusinessId(String businessId);

	public Long deleteServiceEntryByServiceId(String serviceId);

//	@Aggregation(pipeline = { "{ $match: { 'serviceId' : $0}}", "{ $unwind: '$isDescribedAt'}",
//			"{ $match: { 'isDescribedAt.datasetId' : $1}}", "{ $unwind : '$isDescribedAt.dataMapping' }",
//			"{ $replaceRoot: { 'newRoot' : '$isDescribedAt.dataMapping'}}" })
//	public Optional<List<DataMapping>> getDatasetDataMappingByServiceIdAndDatasetId(String service, String datasetId);

}
