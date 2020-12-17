package it.eng.opsi.cape.servicemanager.model.consenting;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.util.Base64URL;

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
public class ConsentStatusRecordSigned {

	@NotNull(message = "header is mandatory")
	JWSHeader header;

	@NotNull(message = "protected is mandatory")
	@JsonProperty(value = "protected")
	Base64URL _protected;

	@NotNull(message = "payload is mandatory")
	@Valid
	ConsentStatusRecordPayload payload;

	@NotNull(message = "signature is mandatory")
	Base64URL signature;

}
