/*******************************************************************************
 * CaPe - a Consent Based Personal Data Suite
 *   Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package it.eng.opsi.cape.sdk.model.consenting;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
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
	private String sourceId;

	@JsonProperty(value = "sink_service_id")
	@NonNull
	@NotBlank
	private String sinkId;

	@JsonProperty(value = "source_name")
	private String sourceName;

	@JsonProperty(value = "sink_name")
	@NonNull
	@NotBlank
	private String sinkName;

	@JsonProperty(value = "sink_library_domain_url")
	private String sinkLibraryDomainUrl;

	@JsonProperty(value = "source_library_domain_url")
	private String sourceLibraryDomainUrl;

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
	private SinkUsageRules usageRules;

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

	@NonNull
	@NotBlank
	@JsonProperty(value = "service_provider_business_id")
	private String serviceProviderBusinessId;
}
