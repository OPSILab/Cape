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
package it.eng.opsi.cape.servicemanager.model.audit;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import com.fasterxml.jackson.annotation.JsonIgnore;

import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventLog {

	@JsonIgnore
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
