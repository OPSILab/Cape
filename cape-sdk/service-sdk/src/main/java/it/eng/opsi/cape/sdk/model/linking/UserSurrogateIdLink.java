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
package it.eng.opsi.cape.sdk.model.linking;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("user2surrogate")
@CompoundIndex(unique = true, def = "{ 'userId': 1, 'surrogateId': 1}")
public class UserSurrogateIdLink {

	@Id
	@JsonIgnore
	private ObjectId _id;

	@NotBlank(message = "userId field is mandatory")
	private String userId;

	@NotBlank(message = "surrogateId field is mandatory")
	@Indexed(unique = true)
	private String surrogateId;

	@NotBlank(message = "serviceId field is mandatory")
	private String serviceId;

	@NotBlank(message = "operatorId field is mandatory")
	private String operatorId;
	
	private ZonedDateTime created;

}
