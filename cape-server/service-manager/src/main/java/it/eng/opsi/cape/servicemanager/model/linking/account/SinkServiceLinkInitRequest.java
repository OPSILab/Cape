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
package it.eng.opsi.cape.servicemanager.model.linking.account;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.eng.opsi.cape.servicemanager.model.ServicePopKey;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@Builder
public class SinkServiceLinkInitRequest {

	@NonNull
	@NotBlank(message = "session_code is mandatory")
	@JsonProperty(value = "session_code")
	private String sessionCode;

	@NonNull
	@NotBlank(message = "slr_id is mandatory")
	@JsonProperty(value = "slr_id")
	private String slrId;

	@NonNull
	@NotBlank(message = "service_id field is mandatory")
	@JsonProperty(value = "service_id")
	private String serviceId;

	@NonNull
	@NotBlank(message = "surrogate_id field is mandatory")
	@JsonProperty(value = "surrogate_id")
	private String surrogateId;

	@NonNull
	@NotNull(message = "pop_key is mandatory")
	@Valid
	@JsonProperty(value = "pop_key")
	private ServicePopKey popKey;

}
