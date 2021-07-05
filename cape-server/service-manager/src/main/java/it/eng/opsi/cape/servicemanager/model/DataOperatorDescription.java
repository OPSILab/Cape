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
package it.eng.opsi.cape.servicemanager.model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;

import it.eng.opsi.cape.serviceregistry.data.Cert;
import it.eng.opsi.cape.serviceregistry.data.ServiceProvider;
import it.eng.opsi.cape.serviceregistry.data.SupportedProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AccessLevel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "dataOperatorDescriptions")
public class DataOperatorDescription implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1978097724649222840L;

	@Id
	@JsonIgnore
	private ObjectId _id;

	@NotBlank(message = "operatorId field is mandatory")
	@Indexed(unique = true)
	private String operatorId;

	private ServiceProvider serviceProvider;

	private String operatorServiceDescriptionVersion;

	private List<SupportedProfile> supportedProfiles;

	private Cert cert;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private RSAKey keyPair;

	@Valid
	@NotNull(message = "operatorUrls field is mandatory")
	private OperatorUrls operatorUrls;

	private ZonedDateTime createdOnDate;

	private String createdByUserId;

	@JsonIgnore
	public void setKeyPair(RSAKey keyPair) {
		this.keyPair = keyPair;
	}

	@JsonProperty
	public RSAKey getKeyPair() {
		return keyPair;
	}
}
