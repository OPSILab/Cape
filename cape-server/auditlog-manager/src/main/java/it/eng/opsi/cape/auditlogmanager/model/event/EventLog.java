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
package it.eng.opsi.cape.auditlogmanager.model.event;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import it.eng.opsi.cape.auditlogmanager.model.consent.ConsentEventLog;
import it.eng.opsi.cape.auditlogmanager.model.dataprocessing.DataProcessingEventLog;
import it.eng.opsi.cape.auditlogmanager.model.servicelink.ServiceLinkEventLog;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({ @Type(value = ServiceLinkEventLog.class, name = "ServiceLink"),
		@Type(value = ConsentEventLog.class, name = "Consent"),
		@Type(value = DataProcessingEventLog.class, name = "DataProcessing"),
		@Type(value = AccountEventLog.class, name= "Account")})
@Document("eventLogs")
@RequiredArgsConstructor
public class EventLog {

	@Id
	@JsonIgnore
	private ObjectId _id;

	@NotNull(message = "created field is mandatory")
	@NonNull
	private ZonedDateTime created;

	@NotNull(message = "type field is mandatory")
	@NonNull
	transient private EventType type;

	@NotBlank(message = "accountId field is mandatory")
	@NonNull
	private String accountId;

	@NonNull
	@NotNull
	private LegalBasis legalBasis;

	@NonNull
	private String message;

}
