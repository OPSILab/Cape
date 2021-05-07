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
package it.eng.opsi.cape.servicemanager.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.eng.opsi.cape.servicemanager.model.linking.LinkingSession;

public interface LinkingSessionRepository extends MongoRepository<LinkingSession, String> {

	public Optional<LinkingSession> findBySessionCode(String sessionCode);

	public Optional<LinkingSession> findFirstByAccountIdAndServiceId(String accountId, String serviceId);

	public Long deleteLinkingSessionBySessionCode(String sessionCode);

	public Long deleteLinkingSessionByAccountIdAndServiceId(String accountId, String serviceId);
	
	public Long deleteLinkingSessionByAccountId(String accountId);
}
