package it.eng.opsi.cape.accountmanager.model.consenting;

import java.util.List;

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
public class WithinServiceConsentSignRequest {

	@NotNull(message = "consent_record_payload is mandatory")
	@Valid
	@JsonProperty(value = "consent_record_payload")
	private ConsentRecordPayload crPayload;

	@NotNull(message = "consent_status_record_payload is mandatory")
	@Valid
	@JsonProperty(value = "consent_status_record_payload")
	private ConsentStatusRecordPayload csrPayload;

	@JsonProperty(value = "consent_status_records_list")
	private List<ConsentStatusRecordSigned> csrList;

}
