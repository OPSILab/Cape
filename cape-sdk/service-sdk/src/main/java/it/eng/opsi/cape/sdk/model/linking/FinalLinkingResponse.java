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

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FinalLinkingResponse {

	@NotBlank(message = "code is mandatory")
	@JsonProperty(value = "code")
	private String code;

	@NotBlank(message = "data is mandatory")
	@JsonProperty(value = "data")
	private LinkingResponseData data;

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	@ToString
	public class LinkingResponseData {

		@NotBlank(message = "slr is mandatory")
		@JsonProperty(value = "slr")
		private ServiceLinkRecordDoubleSigned slr;

		@NotBlank(message = "ssr is mandatory")
		@JsonProperty(value = "ssr")
		private ServiceLinkStatusRecordSigned ssr;

	}

}
