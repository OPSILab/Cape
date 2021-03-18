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
package it.eng.opsi.cape.auditlogmanager.controller;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;

import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import it.eng.opsi.cape.auditlogmanager.model.event.EventLog;
import it.eng.opsi.cape.auditlogmanager.model.event.EventType;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;

public interface IEventLogController {

	public ResponseEntity<EventLog> addEventLog(EventLog eventLog);

	public ResponseEntity<List<EventLog>> getAllEventLogs();

	public ResponseEntity<Object> deleteEventLogsByAccountId(String accountId);

//	public Response getEventLogsByAccountId(String accountId, EventType type, LegalBasis legalBasis, ZonedDateTime start,
//			ZonedDateTime end, List<ProcessingCategory> processingCategories);

	public ResponseEntity<List<EventLog>> getEventLogsByAccountId(String accountId, EventType type,
			List<LegalBasis> legalBasis, ZonedDateTime startDate, ZonedDateTime endDate,
			List<ProcessingCategory> processingCategories);

//	public Response getEventLogsByAccountId(String accountId, EventType type, LegalBasis legalBasis,
//			ZonedDateTime start, ZonedDateTime end);

}
