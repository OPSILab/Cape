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
package it.eng.opsi.cape.sdk.model.linking;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.eng.opsi.cape.sdk.model.ServicePopKey;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString

public class ContinueSinkLinkingRequest extends ContinueLinkingRequest {

	@NotBlank(message = "pop_key is mandatory")
	@Valid
	@JsonProperty(value = "pop_key")
	private ServicePopKey popKey;

	@Builder(builderMethodName = "sinkBuilder")
	public ContinueSinkLinkingRequest(String sessionCode, String serviceId, String surrogateId, ServicePopKey popKey) {
		super(sessionCode, serviceId, surrogateId);
		this.popKey = popKey;

	}

}
