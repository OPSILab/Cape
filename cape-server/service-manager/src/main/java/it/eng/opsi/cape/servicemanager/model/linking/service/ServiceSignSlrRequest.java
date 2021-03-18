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
package it.eng.opsi.cape.servicemanager.model.linking.service;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkRecordAccountSigned;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ServiceSignSlrRequest {

	@NotNull(message = "account_signed_slr is mandatory")
	@Valid
	@JsonProperty(value = "account_signed_slr")
	private ServiceLinkRecordAccountSigned accountSignedSlr;

	@NotBlank(message = "surrogate_id is mandatory")
	@JsonProperty(value = "surrogate_id")
	private String surrogateId;

	@NotBlank(message = "operator_id is mandatory")
	@JsonProperty(value = "operator_id")
	private String operatorId;

	@NotBlank(message = "code is mandatory")
	@JsonProperty(value = "code")
	private String code;

}
