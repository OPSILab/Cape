package it.eng.opsi.cape.sdk.model.linking;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class ServiceSignSlrResponse {

	@NotBlank(message = "code is mandatory")
	@JsonProperty(value = "code")
	private String code;

	@NotBlank(message = "service_signed_slr is mandatory")
	@JsonProperty(value = "service_signed_slr")
	private ServiceLinkRecordDoubleSigned serviceSignedSlr; 
}
