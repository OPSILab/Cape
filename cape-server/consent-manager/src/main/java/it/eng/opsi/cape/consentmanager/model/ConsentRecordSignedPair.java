package it.eng.opsi.cape.consentmanager.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConsentRecordSignedPair {

	@NotNull(message = "sink field is mandatory")
	@Valid
	ConsentRecordSigned sink;

	@Valid
	ConsentRecordSigned source;

}
