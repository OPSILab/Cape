/*******************************************************************************
 * CaPe - A Consent Based Personal Data Suite
 *  Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package it.eng.opsi.cape.servicemanager.model.consenting;

import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.util.Base64URL;

import it.eng.opsi.cape.serviceregistry.data.DataController;
import it.eng.opsi.cape.serviceregistry.data.HumanReadableDescription;
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
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class CommonPart {

	@NonNull
	@NotBlank
	@JsonProperty(defaultValue = "2.0", value = "version")
	private String version;

	@NonNull
	@NotBlank
	@JsonProperty(value = "cr_id")
	@Indexed(unique = true)
	private String crId;

	@JsonProperty(value = "cr_pair_id")
	private String crPairId;

	@NonNull
	@NotBlank
	@JsonProperty(value = "surrogate_id")
	private String surrogateId;

	@JsonProperty(value = "source_surrogate_id")
	private String sourceSurrogateId;

	@NonNull
	@NotBlank
	@JsonProperty(value = "slr_id")
	private String slrId;

	@JsonProperty(value = "source_slr_id")
	private String sourceSlrId;

	@NonNull
	@NotBlank
	@JsonProperty(value = "subject_id")
	private String subjectId;

	@NonNull
	@NotBlank
	@JsonProperty(value = "subject_name")
	private String subjectName;

	@NonNull
	@NotBlank
	@JsonProperty(value = "subject_humanreadable_descriptions")
	private List<HumanReadableDescription> subjectHumanReadableDescriptions;

	@JsonProperty(value = "source_subject_id")
	private String sourceSubjectId;

	@JsonProperty(value = "source_subject_name")
	private String sourceSubjectName;

	@JsonProperty(value = "source_subject_humanreadable_descriptions")
	private List<HumanReadableDescription> sourceSubjectHumanReadableDescriptions;

	@NonNull
	@NotBlank
	@JsonProperty(value = "service_description_version")
	private String serviceDescriptionVersion;

	@NonNull
	@JsonProperty(value = "service_description_signature")
	private String serviceDescriptionSignature;

	@JsonProperty(value = "source_service_description_version")
	private String sourceServiceDescriptionVersion;

	@JsonProperty(value = "source_service_description_signature")
	private String sourceServiceDescriptionSignature;

	@NonNull
	@NotBlank
	@JsonProperty(value = "service_provider_business_id")
	private String serviceProviderBusinessId;

	@NonNull
	@NotNull
	@Valid
	@JsonProperty(value = "rs_description")
	private RSDescription rsDescription;

	@NonNull
	@NotBlank
	private String jurisdiction;

	@NonNull
	@NotNull
	@Valid
	@JsonProperty(value = "data_controller")
	private DataController dataController;

	@NonNull
	@NotNull(message = "iat field is mandatory")
	@Indexed(direction = IndexDirection.DESCENDING)
	private ZonedDateTime iat;

	@NonNull
	@NotNull(message = "mat field is mandatory")
	private ZonedDateTime mat;

	private ZonedDateTime nbf;

	private ZonedDateTime exp;

	@NonNull
	@NotBlank
	private String operator;

	@NonNull
	@NotBlank(message = "consent_status is mandatory")
	@JsonProperty(value = "consent_status")
	private ConsentRecordStatusEnum consentStatus;

	@JsonProperty(value = "consent_statuses_signature")
	private Base64URL consentStatusesSignature;
}
