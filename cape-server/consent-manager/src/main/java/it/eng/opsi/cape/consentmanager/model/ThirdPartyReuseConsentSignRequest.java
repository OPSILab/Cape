package it.eng.opsi.cape.consentmanager.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ThirdPartyReuseConsentSignRequest {

	@NotNull(message = "sink is mandatory")
	@Valid
	WithinServiceConsentSignRequest sink;

	@NotNull(message = "source is mandatory")
	@Valid
	WithinServiceConsentSignRequest source;

}
