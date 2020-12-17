/*******************************************************************************
 * CaPe - a Consent Based Personal Data Suite
 *   Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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

	@NotBlank(message = "sourceId field is mandatory")
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
