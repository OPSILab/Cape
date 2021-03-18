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

import it.eng.opsi.cape.auditlogmanager.model.event.EventLog;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;

public interface EventLogCustomRepository {

	public Optional<List<EventLog>> findByAccountIdAndQuery(String accountId, List<LegalBasis> legalBasis,
			ZonedDateTime start, ZonedDateTime end, List<ProcessingCategory> processingCategories);


}
