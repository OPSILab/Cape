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

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.sun.mail.imap.protocol.Item;

import it.eng.opsi.cape.consentmanager.model.ConsentRecordSigned;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordStatusEnum;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;

public class ConsentRecordCustomRepositoryImpl implements ConsentRecordCustomRepository {

	@Autowired
	MongoTemplate template;

	@Override
	public List<ConsentRecordSigned> findByAccountIdAndQuery(String accountId, String consentId, String serviceId,
			String sourceServiceId, String datasetId, ConsentRecordStatusEnum status, String purposeId,
			String purposeName, PurposeCategory purposeCategory, ProcessingCategory processingCategory) {

		List<AggregationOperation> pipeline = new ArrayList<AggregationOperation>();

		pipeline.add(match(where("accountId").is(accountId)));

		if (consentId != null)
			pipeline.add(match(where("payload.commonPart.crId").is(consentId)));

		if (serviceId != null)
			pipeline.add(match(where("payload.commonPart.subjectId").is(serviceId)));

		if (sourceServiceId != null)
			pipeline.add(match(where("payload.commonPart.sourceSubjectId").is(sourceServiceId)));

		if (datasetId != null)
			pipeline.add(match(where("payload.commonPart.rsDescription.resourceSet.datasets._id").is(datasetId)));

		if (purposeId != null)
			pipeline.add(match(where("payload.commonPart.rsDescription.resourceSet.datasets.purposeId").is(purposeId)));

		if (purposeName != null)
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
	public List<ConsentRecordSigned> findBySurrogateIdAndQuery(String surrogateId, String serviceId,
			String sourceServiceId, String datasetId, ConsentRecordStatusEnum status, String purposeId,
			String purposeName, PurposeCategory purposeCategory, ProcessingCategory processingCategory) {

		List<AggregationOperation> pipeline = new ArrayList<AggregationOperation>();

		pipeline.add(match(where("payload.commonPart.surrogateId").is(surrogateId)));

		if (serviceId != null)
			pipeline.add(match(where("payload.commonPart.subjectId").is(serviceId)));

		if (sourceServiceId != null)
			pipeline.add(match(where("payload.commonPart.sourceSubjectId").is(sourceServiceId)));

		if (datasetId != null)
			pipeline.add(match(where("payload.commonPart.rsDescription.resourceSet.datasets._id").is(datasetId)));

		if (purposeId != null)
			pipeline.add(match(where("payload.commonPart.rsDescription.resourceSet.datasets.purposeId").is(purposeId)));

		if (purposeName != null)
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
			ProcessingCategory processingCategory) {

		List<AggregationOperation> pipeline = new ArrayList<AggregationOperation>();

		pipeline.add(match(where("payload.commonPart.subjectId").is(serviceId)));

		if (sourceServiceId != null)
			pipeline.add(match(where("payload.commonPart.sourceSubjectId").is(sourceServiceId)));

		if (datasetId != null)
			pipeline.add(match(where("payload.commonPart.rsDescription.resourceSet.datasets._id").is(datasetId)));

		if (purposeId != null)
			pipeline.add(match(where("payload.commonPart.rsDescription.resourceSet.datasets.purposeId").is(purposeId)));

		if (purposeName != null)
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
			String purposeName, PurposeCategory purposeCategory, ProcessingCategory processingCategory) {

		List<AggregationOperation> pipeline = new ArrayList<AggregationOperation>();

		pipeline.add(match(where("payload.commonPart.serviceProviderBusinessId").is(businessId)));

		if (surrogateId != null)
			pipeline.add(match(where("payload.commonPart.surrogateId").is(serviceId)));

		if (serviceId != null)
			pipeline.add(match(where("payload.commonPart.subjectId").is(serviceId)));

		if (sourceServiceId != null)
			pipeline.add(match(where("payload.commonPart.sourceSubjectId").is(sourceServiceId)));

		if (datasetId != null)
			pipeline.add(match(where("payload.commonPart.rsDescription.resourceSet.datasets._id").is(datasetId)));

		if (purposeId != null)
			pipeline.add(match(where("payload.commonPart.rsDescription.resourceSet.datasets.purposeId").is(purposeId)));

		if (purposeName != null)
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
