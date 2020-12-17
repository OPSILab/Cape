package it.eng.opsi.cape.accountmanager.model.consenting;

import javax.validation.Valid;
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
public class WithinServiceConsentSignResponse {

	@NotNull(message = "consent_record is mandatory")
	@Valid
	@JsonProperty(value = "consent_record")
	ConsentRecordSigned signedCr;

	@NotNull(message = "consent_status_record is mandatory")
	@Valid
	@JsonProperty(value = "consent_status_record")
	ConsentStatusRecordSigned signedCsr;

}
