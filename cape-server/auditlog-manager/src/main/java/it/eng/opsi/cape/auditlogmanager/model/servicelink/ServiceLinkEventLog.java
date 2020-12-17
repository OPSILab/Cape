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
