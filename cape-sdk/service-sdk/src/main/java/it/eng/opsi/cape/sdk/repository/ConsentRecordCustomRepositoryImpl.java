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

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import it.eng.opsi.cape.exception.ConsentRecordNotFoundException;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordStatusEnum;
import it.eng.opsi.cape.sdk.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;

public class ConsentRecordCustomRepositoryImpl implements ConsentRecordCustomRepository {

	@Autowired
	MongoTemplate template;

	@Override
	public ConsentRecordSigned addStatusToCr(String crId, ConsentStatusRecordSigned csr)
			throws ConsentRecordNotFoundException {

		Query q = query(where("payload.commonPart.crId").is(crId));

		ConsentRecordSigned result = template.findAndModify(q, new Update().push("consentStatusList", csr),
				new FindAndModifyOptions().returnNew(true), ConsentRecordSigned.class);

		if (result == null)
			throw new ConsentRecordNotFoundException("The Consent Record with id: " + crId + " was not found");

		return result;
	}

	@Override
	public List<ConsentRecordSigned> findBySurrogateIdAndQuery(String surrogateId, String serviceId,
			String sourceServiceId, String datasetId, ConsentRecordStatusEnum status, String purposeId,
			String purposeName, PurposeCategory purposeCategory, ProcessingCategory processingCategory,
			Sort.Direction iatSort) {

		List<AggregationOperation> pipeline = new ArrayList<AggregationOperation>();

		pipeline.add(match(where("payload.commonPart.surrogateId").is(surrogateId)));

		if (iatSort != null)
			pipeline.add(Aggregation.sort(iatSort, "payload.commonPart.iat"));

		if (StringUtils.isNotBlank(serviceId))
			pipeline.add(match(where("payload.commonPart.subjectId").is(serviceId)));

		if (StringUtils.isNotBlank(sourceServiceId))
			pipeline.add(match(where("payload.commonPart.sourceSubjectId").is(sourceServiceId)));

		if (StringUtils.isNotBlank(datasetId))
			pipeline.add(match(where("payload.commonPart.rsDescription.resourceSet.datasets._id").is(datasetId)));

		if (StringUtils.isNotBlank(purposeId))
			pipeline.add(match(where("payload.commonPart.rsDescription.resourceSet.datasets.purposeId").is(purposeId)));

		if (StringUtils.isNotBlank(purposeName))
			pipeline.add(
					match(where("payload.commonPart.rsDescription.resourceSet.datasets.purposeName").is(purposeName)));

		if (purposeCategory != null)
			pipeline.add(match(where("payload.roleSpecificPart.usageRules.purposeCategory").is(purposeCategory)));

		if (processingCategory != null)
			pipeline.add(
					match(where("payload.roleSpecificPart.usageRules.processingCategories").is(processingCategory)));

		if (status != null) {
			pipeline.add(new AggregationOperation() {
				@Override
				public Document toDocument(AggregationOperationContext aoc) {
					return new Document("$addFields", new Document("last",
							new Document("$arrayElemAt", Arrays.asList("$consentStatusList", -1))));
				}
			});
			pipeline.add(match(where("last.payload.consentStatus").is(status)));
		}

		Aggregation aggregation = Aggregation.newAggregation(pipeline);
		AggregationResults<ConsentRecordSigned> result = template.aggregate(aggregation, "consentRecords",
				ConsentRecordSigned.class);

		return result.getMappedResults();
	}

	@Override
	public List<ConsentRecordSigned> findByServiceIdAndQuery(String serviceId, String sourceServiceId, String datasetId,
			ConsentRecordStatusEnum status, String purposeId, String purposeName, PurposeCategory purposeCategory,
			ProcessingCategory processingCategory, Sort.Direction iatSort) {

		List<AggregationOperation> pipeline = new ArrayList<AggregationOperation>();

		pipeline.add(match(where("payload.commonPart.subjectId").is(serviceId)));

		if (iatSort != null)
			pipeline.add(Aggregation.sort(iatSort, "payload.commonPart.iat"));

		if (StringUtils.isNotBlank(sourceServiceId))
			pipeline.add(match(where("payload.commonPart.sourceSubjectId").is(sourceServiceId)));

		if (StringUtils.isNotBlank(datasetId))
			pipeline.add(match(where("payload.commonPart.rsDescription.resourceSet.datasets._id").is(datasetId)));

		if (StringUtils.isNotBlank(purposeId))
			pipeline.add(match(where("payload.commonPart.rsDescription.resourceSet.datasets.purposeId").is(purposeId)));

		if (StringUtils.isNotBlank(purposeName))
			pipeline.add(
					match(where("payload.commonPart.rsDescription.resourceSet.datasets.purposeName").is(purposeName)));

		if (purposeCategory != null)
			pipeline.add(match(where("payload.roleSpecificPart.usageRules.purposeCategory").is(purposeCategory)));

		if (processingCategory != null)
			pipeline.add(
					match(where("payload.roleSpecificPart.usageRules.processingCategories").is(processingCategory)));

		if (status != null) {
			pipeline.add(new AggregationOperation() {
				@Override
				public Document toDocument(AggregationOperationContext aoc) {
					return new Document("$addFields", new Document("last",
							new Document("$arrayElemAt", Arrays.asList("$consentStatusList", -1))));
				}
			});
			pipeline.add(match(where("last.payload.consentStatus").is(status)));
		}

		Aggregation aggregation = Aggregation.newAggregation(pipeline);
		AggregationResults<ConsentRecordSigned> result = template.aggregate(aggregation, "consentRecords",
				ConsentRecordSigned.class);

		return result.getMappedResults();
	}

	@Override
	public List<ConsentRecordSigned> findByBusinessIdAndQuery(String businessId, String surrogateId, String serviceId,
			String sourceServiceId, String datasetId, ConsentRecordStatusEnum status, String purposeId,
			String purposeName, PurposeCategory purposeCategory, ProcessingCategory processingCategory,
			Sort.Direction iatSort) {

		List<AggregationOperation> pipeline = new ArrayList<AggregationOperation>();

		pipeline.add(match(where("payload.commonPart.serviceProviderBusinessId").is(businessId)));

		if (iatSort != null)
			pipeline.add(Aggregation.sort(iatSort, "payload.commonPart.iat"));

		if (StringUtils.isNotBlank(surrogateId))
			pipeline.add(match(where("payload.commonPart.surrogateId").is(surrogateId)));

		if (StringUtils.isNotBlank(serviceId))
			pipeline.add(match(where("payload.commonPart.subjectId").is(serviceId)));

		if (StringUtils.isNotBlank(sourceServiceId))
			pipeline.add(match(where("payload.commonPart.sourceSubjectId").is(sourceServiceId)));

		if (StringUtils.isNotBlank(datasetId))
			pipeline.add(match(where("payload.commonPart.rsDescription.resourceSet.datasets._id").is(datasetId)));

		if (StringUtils.isNotBlank(purposeId))
			pipeline.add(match(where("payload.commonPart.rsDescription.resourceSet.datasets.purposeId").is(purposeId)));

		if (StringUtils.isNotBlank(purposeName))
			pipeline.add(
					match(where("payload.commonPart.rsDescription.resourceSet.datasets.purposeName").is(purposeName)));

		if (purposeCategory != null)
			pipeline.add(match(where("payload.roleSpecificPart.usageRules.purposeCategory").is(purposeCategory)));

		if (processingCategory != null)
			pipeline.add(
					match(where("payload.roleSpecificPart.usageRules.processingCategories").is(processingCategory)));

		if (status != null) {
			pipeline.add(new AggregationOperation() {
				@Override
				public Document toDocument(AggregationOperationContext aoc) {
					return new Document("$addFields", new Document("last",
							new Document("$arrayElemAt", Arrays.asList("$consentStatusList", -1))));
				}
			});
			pipeline.add(match(where("last.payload.consentStatus").is(status)));
		}

		Aggregation aggregation = Aggregation.newAggregation(pipeline);
		AggregationResults<ConsentRecordSigned> result = template.aggregate(aggregation, "consentRecords",
				ConsentRecordSigned.class);

		return result.getMappedResults();
	}

}
