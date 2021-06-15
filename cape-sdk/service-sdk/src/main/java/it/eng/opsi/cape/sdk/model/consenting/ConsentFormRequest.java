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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ConsentFormRequest {

	@Schema(description = "Surrogate Id related to Service initiating the Consenting request (can be either the Sink or, if any (in case of 3rd party re-use case) the Source service.")
	@JsonProperty(value = "requester_surrogate_id")
	@NonNull
	@NotBlank
	private String requesterSurrogateId;

	@Schema(description = "Role (SINK, SOURCE) of requester Service initiating the Consenting request (is the Service related to the input Surrogate Id")
	@JsonProperty(value = "requester_role")
	@NonNull
	@NotNull
	private Role requesterSurrogateRole;

	@Schema(description = "Purpose Id of Processing Bases described in the (Sink) Service Description.")
	@JsonProperty(value = "purpose_id")
	@NonNull
	@NotBlank
	private String purposeId;

	@Schema(description = "Id of the Service being part of the Consent as Sink party (3rd party) or Service processing data within itself.")
	@JsonProperty(value = "sink_service_id")
	@NonNull
	@NotBlank
	private String sinkServiceId;

	@Schema(description = "Id of the Service being part of the Consent as Source party (if any, in case of 3rd party re-use case.")
	@JsonProperty(value = "source_service_id")
	private String sourceServiceId;

	@Schema(description = "Dataset Id of the Source Service whose Data Mappings will be matched with the ones of datasets required by Sink Processing Bases (Purpose required datasets). In the within Service case, the processed Datasets are directly the ones required by Sink Processing Bases (Purpose).")
	@JsonProperty(value = "source_service_dataset_id")
	private String sourceServiceDatasetId;

}
