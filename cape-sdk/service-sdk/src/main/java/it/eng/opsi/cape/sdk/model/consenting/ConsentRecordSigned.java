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

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.util.Base64URL;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "consentRecords")
public class ConsentRecordSigned {

	@Id
	@JsonIgnore
	private ObjectId _id;

	@NotNull(message = "header is mandatory")
	JWSHeader header;

	@NotNull(message = "protected is mandatory")
	@JsonProperty(value = "protected")
	Base64URL _protected;

	@NotNull(message = "payload is mandatory")
	@Valid
	ConsentRecordPayload payload;

	@NotNull(message = "signature is mandatory")
	Base64URL signature;

	@NonNull
	private List<ConsentStatusRecordSigned> consentStatusList;

	public void addConsentStatusRecord(ConsentStatusRecordSigned consentStatusRecords) {
		if (this.consentStatusList == null)
			this.consentStatusList = new ArrayList<ConsentStatusRecordSigned>();
		this.consentStatusList.add(consentStatusRecords);
	}

	public void removeConsentStatusRecord(int index) {
		if (this.consentStatusList != null)
			this.consentStatusList.remove(index);
	}
}
