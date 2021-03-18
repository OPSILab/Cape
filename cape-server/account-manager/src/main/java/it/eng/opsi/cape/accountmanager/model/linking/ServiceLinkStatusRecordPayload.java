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
package it.eng.opsi.cape.accountmanager.model.linking;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

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
