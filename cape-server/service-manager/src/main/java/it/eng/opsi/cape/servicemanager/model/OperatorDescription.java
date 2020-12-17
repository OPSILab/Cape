/*******************************************************************************
 * CaPe - a Consent Based Personal Data Suite
 *   Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
@Document(collection = "operatorDescriptions")
public class OperatorDescription implements Serializable {

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
