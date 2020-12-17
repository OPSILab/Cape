package it.eng.opsi.cape.accountmanager.model.consenting;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
public class ThirdPartyReuseConsentSignRequest {

	@NotNull(message = "sink is mandatory")
	@Valid
	WithinServiceConsentSignRequest sink;

	@NotNull(message = "source is mandatory")
	@Valid
	WithinServiceConsentSignRequest source;

}
