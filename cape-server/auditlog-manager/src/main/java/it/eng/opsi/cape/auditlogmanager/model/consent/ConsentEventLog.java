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
package it.eng.opsi.cape.auditlogmanager.model.consent;

import java.time.ZonedDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

import it.eng.opsi.cape.auditlogmanager.model.event.EventLog;
import it.eng.opsi.cape.auditlogmanager.model.event.EventType;
import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Document("eventLogs")
public class ConsentEventLog extends EventLog {

	@NotNull(message = "usageRules field is mandatory")
	@Valid
	private SinkUsageRules usageRules;

	@NotBlank(message = "sinkId field is mandatory")
	private String sinkId;

	private String sourceId;

	@NotNull(message = "dataConcepts field is mandatory")
	private DataMapping[] dataConcepts;

	private DataMapping[] previousDataConcepts;

	@NotNull(message = "action field is mandatory")
	private ConsentActionType action;

	private ConsentRecordStatusEnum previousStatus;

	private String consentRecordId;

	public ConsentEventLog(ZonedDateTime created, EventType type, String accountId, LegalBasis legalBasis,
			String message, SinkUsageRules usageRule, String sinkId, String sourceId, DataMapping[] dataConcepts,
			DataMapping[] previousConcepts, ConsentActionType action, ConsentRecordStatusEnum previousStatus,
			String consentRecordId) {
		super(created, type, accountId, legalBasis, message);
		this.usageRules = usageRule;
		this.sinkId = sinkId;
		this.sourceId = sourceId;
		this.dataConcepts = dataConcepts;
		this.previousDataConcepts = previousConcepts;
		this.action = action;
		this.consentRecordId = consentRecordId;
		this.setPreviousStatus(previousStatus);
	}

}
