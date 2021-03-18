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
package it.eng.opsi.cape.accountmanager.model.audit;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({ @Type(value = ServiceLinkEventLog.class, name = "ServiceLink"),
		@Type(value = ConsentEventLog.class, name = "Consent"),
		@Type(value = DataProcessingEventLog.class, name = "DataProcessing") })
public class EventLog {

	@Id
	private ObjectId _id;

	@NotBlank(message = "created field is mandatory")
	private ZonedDateTime created;

	@NotBlank(message = "type field is mandatory")
	transient private EventType type;

	@NotBlank(message = "accountId field is mandatory")
	private String accountId;

	private LegalBasis legalBasis;

	private String message;

	public EventLog(ZonedDateTime created, EventType type, String accountId, LegalBasis legalBasis, String message) {
		super();
		this.created = created;
		this.type = type;
		this.accountId = accountId;
		this.legalBasis = legalBasis;
		this.message = message;
	}

}
