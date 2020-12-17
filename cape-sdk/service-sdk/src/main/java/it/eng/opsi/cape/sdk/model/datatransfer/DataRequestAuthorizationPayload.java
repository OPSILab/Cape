package it.eng.opsi.cape.sdk.model.datatransfer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DataRequestAuthorizationPayload {

	@NotBlank
	String at;

	@NotNull
	Long ts;
}
