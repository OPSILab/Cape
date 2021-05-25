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
package it.eng.opsi.cape.consentmanager.model.linking;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.util.Base64URL;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class ServiceLinkRecordDoubleSigned {

	@NotNull(message = "payload is mandatory")
	ServiceLinkRecordPayload payload;

	@NotNull(message = "signatures is mandatory")
	List<ServiceLinkRecordSignature> signatures;

	//@JsonIgnore
	List<ServiceLinkStatusRecordSigned> serviceLinkStatuses = new ArrayList<ServiceLinkStatusRecordSigned>();

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	public static class ServiceLinkRecordSignature {

		@Schema(implementation = Object.class, defaultValue = "{}", name = "header", type = "object", description = "Unprotected JOSE header value", example = "{\"alg\":\"RS256\",\"kid\":\"cape:60a7c2a28c0fe81c37a5a154\"}")
		@NotNull(message = "header is mandatory")
		JWSHeader header;

		@NotNull(message = "protected is mandatory")
		@JsonProperty(value = "protected")
		Base64URL _protected;

		@NotNull(message = "signature is mandatory")
		Base64URL signature;
	}

	public void addServiceLinkStatusRecord(ServiceLinkStatusRecordSigned serviceLinkStatus) {
		serviceLinkStatuses.add(serviceLinkStatus);
	}

}
