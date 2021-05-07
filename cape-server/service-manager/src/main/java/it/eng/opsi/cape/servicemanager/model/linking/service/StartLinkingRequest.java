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

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


@RequiredArgsConstructor
@Getter
@ToString
@Builder
public class StartLinkingRequest {

	
	@NotBlank(message = "session_code is mandatory")
	@JsonProperty(value = "session_code") 
	private final String sessionCode;
	
	@NotBlank(message = "surrogate_id is mandatory")
	@JsonProperty(value = "surrogate_id")
	private final String surrogateId;
	
	@NotBlank(message = "operator_id is mandatory")
	@JsonProperty(value = "operator_id")
	private final String operatorId;
	
	@NotBlank(message = "service_id is mandatory")
	@JsonProperty(value = "service_id")
	private final String serviceId;
	
	@NotBlank(message = "return_url is mandatory")
	@JsonProperty(value = "return_url")
	private final String returnUrl;

}
