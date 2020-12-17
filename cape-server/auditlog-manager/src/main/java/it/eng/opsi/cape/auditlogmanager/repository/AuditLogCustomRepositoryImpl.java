/*******************************************************************************
 * CaPe - a Consent Based Personal Data Suite
 *   Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
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
	public Optional<AuditLog> addPurposeCategory(String accountId, String purposeCategory) {

		Query q = query(where("accountId").is(accountId));
		AuditLog updatedAuditLog = template.findAndModify(q,
				new Update().inc("purposeCategoryCount." + purposeCategory, 1),
				new FindAndModifyOptions().returnNew(true), AuditLog.class);

		return Optional.ofNullable(updatedAuditLog);
	}

	@Override
	public Optional<AuditLog> subtractPurposeCategory(String accountId, String purposeCategory) {

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
