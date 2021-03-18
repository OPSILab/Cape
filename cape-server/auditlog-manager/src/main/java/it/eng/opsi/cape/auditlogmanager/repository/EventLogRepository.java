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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.eng.opsi.cape.auditlogmanager.model.event.EventLog;
import it.eng.opsi.cape.auditlogmanager.model.event.EventType;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;

public interface EventLogRepository extends MongoRepository<EventLog, String>, EventLogCustomRepository {

	public Optional<List<EventLog>> findByType(EventType type);

	public Optional<List<EventLog>> findByAccountId(String accountId);

	public Optional<List<EventLog>> findByAccountIdAndType(String accountId, EventType type);

	public Optional<List<EventLog>> findByAccountIdAndLegalBasisIn(String accountId, List<LegalBasis> legalBasis);

	@Query(value = "{ $and : [{ 'accountId' : $0} , { 'created' : { $gte: $1}}, { 'created' : { $lte: $2}} ]}")
	public Optional<List<EventLog>> getEventLogsByAccountIdAndCreatedDate(String accountId, ZonedDateTime start,
			ZonedDateTime end);

	public Long deleteEventLogByAccountId(String accountId);
}
