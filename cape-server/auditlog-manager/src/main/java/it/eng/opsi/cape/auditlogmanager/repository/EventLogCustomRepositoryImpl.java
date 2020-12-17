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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import it.eng.opsi.cape.auditlogmanager.model.event.EventLog;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;

public class EventLogCustomRepositoryImpl implements EventLogCustomRepository {

	@Autowired
	MongoTemplate template;

	@Override
	public Optional<List<EventLog>> findByAccountIdAndQuery(String accountId, List<LegalBasis> legalBasis,
			ZonedDateTime start, ZonedDateTime end, List<ProcessingCategory> processingCategories) {

		Query q = query(where("accountId").is(accountId));

		if (start != null && end != null) {
			q.addCriteria(new Criteria().andOperator(Criteria.where("created").gte(start),
					Criteria.where("created").lt(end)));
		} else if (start != null) {
			q.addCriteria(Criteria.where("created").gte(start));
		} else {
			q.addCriteria(Criteria.where("created").lt(end));
		}

		if (legalBasis != null && !legalBasis.isEmpty())
			q.addCriteria(Criteria.where("legalBasis").in(legalBasis));

		if (processingCategories != null && !processingCategories.isEmpty())
			q.addCriteria(Criteria.where("usageRules.processingCategories").in(processingCategories));

		return Optional.of(template.find(q, EventLog.class));
	}

}
