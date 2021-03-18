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
package it.eng.opsi.cape.auditlogmanager.model.servicelink;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

import it.eng.opsi.cape.auditlogmanager.model.event.EventLog;
import it.eng.opsi.cape.auditlogmanager.model.event.EventType;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "eventLogs")
public class ServiceLinkEventLog extends EventLog {

	@NotBlank(message = "slrId field is mandatory")
	private String slrId;

	@NotBlank(message = "serviceId field is mandatory")
	private String serviceId;

	@NotBlank(message = "serviceName field is mandatory")
	private String serviceName;

	@NotBlank(message = "serviceUri field is mandatory")
	private String serviceUri;

	@NotNull(message = "action field is mandatory")
	private ServiceLinkActionType action;

	private ChangeSlrStatusRequestFrom requestFrom;

	public ServiceLinkEventLog(ZonedDateTime created, EventType type, String accountId, LegalBasis legalBasis,
			String message, String slrId, String serviceId, String serviceName, String serviceUri,
			ServiceLinkActionType action, ChangeSlrStatusRequestFrom requestFrom) {
		super(created, type, accountId, legalBasis, message);
		this.slrId = slrId;
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.serviceUri = serviceUri;
		this.action = action;
		this.requestFrom = requestFrom;

	}

}
