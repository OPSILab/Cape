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
package it.eng.opsi.cape.sdk.model.account;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.jwk.RSAKey;

import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordDoubleSigned;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Account {

	@Id
	@JsonIgnore
	private ObjectId _id;

	private boolean activated;

	private ZonedDateTime created;
	private ZonedDateTime modified;

	@Valid
	@JsonProperty(value = "account_info")
	private AccountInfo accountInfo;

	@NotBlank(message = "username field is mandatory")
	@Indexed(unique = true)
	private String username;

	private Locale language;

	private List<AccountNotificationEnum> notification = new ArrayList<AccountNotificationEnum>();

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@JsonIgnore
	private RSAKey keyPair;

	@JsonIgnore
	private List<ServiceLinkRecordDoubleSigned> serviceLinkRecords;

	@JsonIgnore
	public void setKeyPair(RSAKey keyPair) {
		this.keyPair = keyPair;
	}

	@JsonProperty
	public RSAKey getKeyPair() {
		return keyPair;
	}

}
