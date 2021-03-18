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
package it.eng.opsi.cape.sdk.model.consenting;

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
