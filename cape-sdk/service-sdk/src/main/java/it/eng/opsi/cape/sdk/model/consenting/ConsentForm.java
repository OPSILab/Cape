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
package it.eng.opsi.cape.sdk.model.consenting;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import it.eng.opsi.cape.serviceregistry.data.DataController;
import it.eng.opsi.cape.serviceregistry.data.HumanReadableDescription;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry.Role;
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
@RequiredArgsConstructor
@ToString
@Document(collection = "consentForms")
public class ConsentForm {

	@Id
	@JsonIgnore
	@NonNull
	String id;

	@JsonProperty(value = "surrogate_id")
	@NonNull
	@NotBlank
	private String surrogateId;

	@JsonProperty(value = "source_surrogate_id")
	private String sourceSurrogateId;

	@JsonProperty(value = "source_service_id")
	private String sourceServiceId;

	@JsonProperty(value = "sink_service_id")
	@NonNull
	@NotBlank
	private String sinkServiceId;

	@JsonProperty(value = "source_name")
	private String sourceName;

	@JsonProperty(value = "sink_name")
	@NonNull
	@NotBlank
	private String sinkName;

	@JsonProperty(value = "source_library_domain_url")
	private String sourceLibraryDomainUrl;

	@JsonProperty(value = "sink_library_domain_url")
	@NonNull
	@NotBlank
	private String sinkLibraryDomainUrl;

	@JsonProperty(value = "source_humanreadable_descriptions")
	private List<HumanReadableDescription> sourceHumanReadableDescriptions;

	@JsonProperty(value = "sink_humanreadable_descriptions")
	@NonNull
	private List<HumanReadableDescription> sinkHumanReadableDescriptions;

	@JsonProperty(value = "resource_set")
	@NonNull
	@Valid
	@NotNull
	private ResourceSet resourceSet;

	@JsonProperty(value = "usage_rules")
	@NonNull
	@NotNull
	@Valid
	private UsageRules usageRules;

	@JsonProperty(value = "jurisdiction")
	@NonNull
	private String jurisdiction;

	@JsonProperty(value = "data_controller")
	@NonNull
	@Valid
	@NotNull
	private DataController dataController;

	@JsonProperty(value = "service_description_version")
	@NonNull
	@NotBlank
	private String serviceDescriptionVersion;

	@JsonProperty(value = "service_description_signature")
	@NonNull
	@NotBlank
	private String serviceDescriptionSignature;

	@JsonProperty(value = "source_service_description_version")
	private String sourceServiceDescriptionVersion;

	@JsonProperty(value = "source_service_description_signature")
	private String sourceServiceDescriptionSignature;

	@NonNull
	@NotBlank
	@JsonProperty(value = "service_provider_business_id")
	private String serviceProviderBusinessId;

	@Schema(description = "Role (SINK, SOURCE) of requester Service initiating the Consenting request (is the Service related to the input Surrogate Id")
	@JsonProperty(value = "requester_role")
	@NonNull
	@NotNull
	private Role requesterSurrogateRole;

	@Schema(description = "Identifier of the physical operator collecting the Consent on behalf of the Data Subject (e.g. after Data Subject gave previously the consent offline, for instance by paper). Identifier values can have any meaning, according to specific agreement made with the Service Provider")
	@JsonProperty(value = "collection_operator_id")
	private String collectionOperatorId;
}
