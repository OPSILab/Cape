package it.eng.opsi.cape.consentmanager.model;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "authorisationTokens")
public class AuthorisationTokenResponse {


	@NotNull(message = "cr_id is mandatory")
	@JsonProperty(value = "cr_id")
	String crId;

	
	@NotNull(message = "auth_token is mandatory")
	@JsonProperty(value = "auth_token")
	String authToken;

	
	
}
