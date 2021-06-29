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

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.eng.opsi.cape.sdk.model.linking.UserSurrogateIdLink;

public interface UserSurrogateIdLinkRepository extends MongoRepository<UserSurrogateIdLink, String> {

	public Optional<UserSurrogateIdLink> findTopByUserIdAndServiceIdAndOperatorIdOrderByCreatedDesc(String userId,
			String serviceId, String operatorId);

	public Optional<UserSurrogateIdLink> findTopByUserIdAndServiceIdOrderByCreatedDesc(String userId, String serviceId);

	public List<UserSurrogateIdLink> findByUserIdOrderByCreatedDesc(String userId);

	public List<UserSurrogateIdLink> findByUserIdOrderByCreatedAsc(String userId);
	
	public Long deleteUserSurrogateIdLinkBySurrogateId(String surrogateId);
}
