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

import com.fasterxml.jackson.annotation.JsonProperty;

import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkStatusRecordPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class FinalStoreSlrRequest {

	@NotNull(message = "slr is mandatory")
	@Valid
	private ServiceLinkRecordDoubleSigned slr;

	@NotNull(message = "ssr is mandatory")
	@Valid
	private ServiceLinkStatusRecordPayload ssr;

	@NotBlank(message = "session_code is mandatory")
	@JsonProperty(value = "session_code")
	private String sessionCode;

}
