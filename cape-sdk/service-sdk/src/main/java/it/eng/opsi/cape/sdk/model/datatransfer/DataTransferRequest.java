package it.eng.opsi.cape.sdk.model.datatransfer;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataTransferRequest {

	@NotBlank
	@JsonProperty(value = "surrogate_id")
	String surrogateId;

	@NotBlank
	@JsonProperty(value = "cr_id")
	String crId;

	@NotBlank
	@JsonProperty(value = "rs_id")
	String rsId;

	@JsonProperty(value = "dataset_id")
	String datasetId;

}
