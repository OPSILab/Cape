package it.eng.opsi.cape.consentmanager.model;

import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Policy {

	@NotBlank(message = "policyRef field is mandatory")
	public @NotBlank(message = "policyRef field is mandatory") String policyRef;
	public String policyHash;
	public String policyVersion;

}