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
		} else if (end != null) {
			q.addCriteria(Criteria.where("created").lt(end));
		}

		if (legalBasis != null && !legalBasis.isEmpty())
			q.addCriteria(Criteria.where("legalBasis").in(legalBasis));

		if (processingCategories != null && !processingCategories.isEmpty())
			q.addCriteria(Criteria.where("usageRules.processingCategories").in(processingCategories.stream().map(p ->

			p.name()).toArray()));

		return Optional.of(template.find(q, EventLog.class));
	}

}
