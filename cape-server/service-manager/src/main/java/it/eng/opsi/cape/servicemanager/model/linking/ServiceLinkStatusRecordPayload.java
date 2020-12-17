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
package it.eng.opsi.cape.servicemanager.model.linking;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder
public class ServiceLinkStatusRecordPayload {

	@NotBlank(message = "version is mandatory")
	@NonNull
	@JsonProperty(defaultValue = "2.0", value = "version")
	private String version;

	@NotBlank(message = "record_id is mandatory")
	@NonNull
	@JsonProperty(value = "record_id")
	private String _id;

	@NotBlank(message = "surrogate_id is mandatory")
	@NonNull
	@JsonProperty(value = "surrogate_id")
	private String surrogateId;

	@NotBlank(message = "slr_id is mandatory")
	@NonNull
	@JsonProperty(value = "slr_id")
	private String serviceLinkRecordId;

	@NotNull(message = "sl_status is mandatory")
	@NonNull
	@JsonProperty(value = "sl_status")
	private ServiceLinkStatusEnum serviceLinkStatus;

	@NotNull(message = "iat is mandatory")
	@NonNull
	private ZonedDateTime iat;

	@NotBlank(message = "prev_record_id is mandatory")
	@NonNull
	@JsonProperty(value = "prev_record_id")
	private String prevRecordId;

	public ServiceLinkStatusRecordPayload(ServiceLinkStatusEnum serviceLinkStatus) {
		super();
		this.serviceLinkStatus = serviceLinkStatus;
		this.iat = ZonedDateTime.now(ZoneId.of("UTC"));
	}

}
