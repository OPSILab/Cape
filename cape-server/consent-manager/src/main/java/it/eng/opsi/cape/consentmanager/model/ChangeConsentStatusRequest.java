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
package it.eng.opsi.cape.consentmanager.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChangeConsentStatusRequest {

	
	ConsentRecordStatusEnum status;

	@NotNull(message = "resource_set field is mandatory")
	@Valid
	@JsonProperty(value = "resource_set")
	ResourceSet resourceSet;

	@NotNull(message = "usage_rules field is mandatory")
	@Valid
	@JsonProperty(value = "usage_rules")
	UsageRules usageRules;

	@NotNull(message = "request_from field is mandatory")

	@JsonProperty(value = "request_from")
	ChangeConsentStatusRequestFrom requestFrom;

	public enum ChangeConsentStatusRequestFrom {

		SERVICE("Service"), OPERATOR("Operator");

		private String value;

		ChangeConsentStatusRequestFrom(String value) {
			this.value = value;
		}

		@JsonValue
		public String getText() {
			return value;
		}

		@Override
		public String toString() {
			return value;
		}

	}

}
