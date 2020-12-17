package it.eng.opsi.cape.consentmanager.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class WithinServiceConsentSignRequest {

	@NotNull(message = "consent_record_payload is mandatory")
	@Valid
	@JsonProperty(value = "consent_record_payload")
	ConsentRecordPayload crPayload;

	@NotNull(message = "consent_status_record_payload is mandatory")
	@Valid
	@JsonProperty(value = "consent_status_record_payload")
	ConsentStatusRecordPayload csrPayload;

	@JsonProperty(value = "consent_status_records_list")
	private List<ConsentStatusRecordSigned> csrList;

}
