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
package it.eng.opsi.cape.consentmanager.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class ConsentStatusRecordPayload {

	@JsonProperty(value = "record_id")
	@NotBlank
	private String recordId;

	@JsonProperty(value = "version")
	@NotBlank
	private String version;

	@JsonProperty(value = "surrogate_id")
	@NotBlank
	private String surrogateId;

	@JsonProperty(value = "cr_id")
	@NotBlank
	private String crId;

	@JsonProperty(value = "consent_status")
	@NotNull
	@Valid
	private ConsentRecordStatusEnum consentStatus;

	@JsonProperty(value = "consent_resource_set")
	@Valid
	private ResourceSet consentResourceSet;

	@JsonProperty(value = "consent_usage_rules")
	@Valid
	private SinkUsageRules consentUsageRules;

	@JsonProperty(value = "iat")
	@NotNull
	@Valid
	private ZonedDateTime iat;

	@JsonProperty(value = "prev_record_id")
	@NotBlank
	private String prevRecordId;

}
