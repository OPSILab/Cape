package it.eng.opsi.cape.consentmanager.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ThirdPartyReuseConsentSignResponse {

	@NotNull(message = "sink is mandatory")
	@Valid
	WithinServiceConsentSignResponse sink;

	@NotNull(message = "source is mandatory")
	@Valid
	WithinServiceConsentSignResponse source;

}
