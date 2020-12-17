/*******************************************************************************
 * CaPe - a Consent Based Personal Data Suite
 *   Copyright (C) 2019 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.opsi.cape.sdk.model.linking;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;

import it.eng.opsi.cape.sdk.model.ServicePopKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@CompoundIndexes({
    @CompoundIndex(name = "serviceId_surrogateId", def = "{'surrogateId' : 1, 'serviceId': 1}", unique = true)
})
public class ServiceLinkRecordPayload {

	@NonNull
	@NotBlank(message = "version field is mandatory")
	@JsonProperty(defaultValue = "2.0", value = "version")
	private String version;

	@NonNull
	@NotBlank(message = "link_id is mandatory")
	@JsonProperty(value = "link_id")
	@Id
	private String slrId;

	@NonNull
	@NotBlank(message = "operator_id field is mandatory")
	@JsonProperty(value = "operator_id")
	private String operatorId;

	@NonNull
	@NotBlank(message = "service_id field is mandatory")
	@JsonProperty(value = "service_id")
	private String serviceId;

	@NonNull
	@NotBlank(message = "service_uri field is mandatory")
	@JsonProperty(value = "service_uri")
	private String serviceUri;

	@NonNull
	@NotBlank(message = "service_name field is mandatory")
	@JsonProperty(value = "service_name")
	private String serviceName;

	@JsonProperty(value = "pop_key")
	@Valid
	private ServicePopKey popKey;

	@NonNull
	@NotBlank(message = "service_description_version field is mandatory")
	@JsonProperty(value = "service_description_version")
	private String serviceDescriptionVersion;

	/*
	 * ID​ ​representing​ ​Account​ ​Owner​ ​in communication​ ​between​ ​Operator
	 * and​ ​service
	 */
	@NonNull
	@NotBlank(message = "surrogate_id field is mandatory")
	@JsonProperty(value = "surrogate_id")
	private String surrogateId;

	/*
	 * JSON​ ​Web​ ​Key​ ​(JWK)​ ​presentation​ ​of​ ​Operator's​ ​public​ ​key​
	 * ​used​ ​to​ ​verify operator​ ​issued​ ​status​ ​change​ ​messages
	 */
	@NonNull
	@NotNull(message = "operator_key field is mandatory")
	@JsonProperty(value = "operator_key")
	private RSAKey operatorKey;

	/*
	 * Account​ ​Owner’s​ ​public​ ​key(s) [1..*]​ ​used​ ​to​ ​verify​ ​MyData
	 * Consent​ ​and​ ​Consent​ ​Status​ ​Records delivered​ ​to​ ​Service.​ ​
	 */
	@JsonProperty(value = "cr_keys")
	private List<RSAKey> crKeys = new ArrayList<RSAKey>();

	@NonNull
	@NotNull(message = "iat field is mandatory")
	private ZonedDateTime iat;

	public void addCrKey(RSAKey crKey) {
		this.crKeys.add(crKey);
	}
}
