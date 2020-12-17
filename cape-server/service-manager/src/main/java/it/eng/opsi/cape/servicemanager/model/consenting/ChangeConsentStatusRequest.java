package it.eng.opsi.cape.servicemanager.model.consenting;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChangeConsentStatusRequest {

	@NotNull(message = "status field is mandatory")
	ConsentRecordStatusEnum status;

	@NotNull(message = "resource_set field is mandatory")
	@Valid
	@JsonProperty(value = "resource_set")
	ResourceSet resourceSet;

	@NotNull(message = "usage_rules field is mandatory")
	@Valid
	@JsonProperty(value = "usage_rules")
	SinkUsageRules usageRules;

	@NotNull(message = "request_from field is mandatory")

	@JsonProperty(value = "request_from")
	ChangeConsentStatusRequestFrom requestFrom;

	public enum ChangeConsentStatusRequestFrom {

		SERVICE("Service"), OPERATOR("Operator");

		private String value;

		ChangeConsentStatusRequestFrom(String value) {
			this.value = value;
		}

		@JsonValue
		public String getText() {
			return value;
		}

		@Override
		public String toString() {
			return value;
		}

	}

}
