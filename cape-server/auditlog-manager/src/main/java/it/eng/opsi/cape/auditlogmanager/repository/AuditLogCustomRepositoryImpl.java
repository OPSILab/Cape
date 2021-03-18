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
package it.eng.opsi.cape.auditlogmanager.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import it.eng.opsi.cape.auditlogmanager.model.audit.AuditLog;
import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.Storage.Location;

public class AuditLogCustomRepositoryImpl implements AuditLogCustomRepository {

	@Autowired
	MongoTemplate template;

	@Override
	public Optional<AuditLog> incLinkedService(String accountId) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q, new Update().inc("linkedServicesCount", 1),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> decLinkedService(String accountId) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q, new Update().inc("linkedServicesCount", -1),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> incGivenConsent(String accountId) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q, new Update().inc("givenConsentsCount", 1),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> decGivenConsent(String accountId) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q, new Update().inc("givenConsentsCount", -1),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> addGivenConsent(String accountId, Integer increment) {
		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q, new Update().inc("givenConsentsCount", increment),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> subtractGivenConsent(String accountId, Integer increment) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q, new Update().inc("givenConsentsCount", -increment),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);

	}

	@Override
	public Optional<AuditLog> incProcessedPersonalData(String accountId) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q, new Update().inc("totalProcessedPersonalDataCount", 1),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> decProcessedPersonalData(String accountId) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q, new Update().inc("totalProcessedPersonalDataCount", -1),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> addProcessedPersonalData(String accountId, Integer increment) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q,
				new Update().inc("totalProcessedPersonalDataCount", increment),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> subtractPersonalData(String accountId, Integer increment) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q,
				new Update().inc("totalProcessedPersonalDataCount", -increment),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> addProcessedPersonalData(String accountId, DataMapping concept) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q,
				new Update().inc("processedPersonalDataCount." + concept.getConceptId() + ".count", 1)
						.set("processedPersonalDataCount." + concept.getConceptId() + ".name", concept.getName()),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> subtractProcessedPersonalData(String accountId, DataMapping concept) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q,
				new Update().inc("processedPersonalDataCount." + concept.getConceptId() + ".count", -1),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> addProcessingPersonalData(String accountId, ProcessingCategory processing,
			DataMapping concept) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q,
				new Update()
						.inc("processingCategoryPersonalData." + processing + "." + concept.getConceptId() + ".count",
								1)
						.set("processingCategoryPersonalData." + processing + "." + concept.getConceptId() + ".name",
								concept.getName()),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> subtractProcessingPersonalData(String accountId, ProcessingCategory processing,
			DataMapping concept) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q,
				new Update().inc(
						"processingCategoryPersonalData." + processing + "." + concept.getConceptId() + ".count", -1),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> addPurposeCategory(String accountId, PurposeCategory purposeCategory) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q,
				new Update().inc("purposeCategoryCount." + purposeCategory, 1),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> subtractPurposeCategory(String accountId, PurposeCategory purposeCategory) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q,
				new Update().inc("purposeCategoryCount." + purposeCategory, -1),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> addLegalBasis(String accountId, LegalBasis legalBasis) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q, new Update().inc("legalBasisCount." + legalBasis, 1),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> subtractLegalBasis(String accountId, LegalBasis legalBasis) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q, new Update().inc("legalBasisCount." + legalBasis, -1),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> addStoragePersonalData(String accountId, Location location, DataMapping concept) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q,
				new Update().inc("storageLocationPersonalData." + location + "." + concept.getConceptId() + ".count", 1)
						.set("storageLocationPersonalData." + location + "." + concept.getConceptId() + ".name",
								concept.getName()),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);

	}

	@Override
	public Optional<AuditLog> subtractStoragePersonalData(String accountId, Location location, DataMapping concept) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q, new Update()
				.inc("storageLocationPersonalData." + location + "." + concept.getConceptId() + ".count", -1),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);

	}

}
