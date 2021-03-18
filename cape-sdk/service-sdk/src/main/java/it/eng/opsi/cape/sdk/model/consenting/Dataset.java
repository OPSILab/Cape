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

import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
@RequiredArgsConstructor
@ToString
public class Dataset {

	@NonNull
	@NotBlank(message = "dataset_id field is mandatory")
	@JsonProperty(value = "dataset_id")
	private String id;

	private String contactPoint;

	private String description;

	private String issued;

	private List<String> keyword;

	@NonNull
	@NotBlank(message = "purposeId field is mandatory")
	private String purposeId;

	@NonNull
	@NotBlank(message = "purposeName field is mandatory")
	private String purposeName;
	
	private String language;

	private String modified;

	private String publisher;

	private String serviceDataType;

	private String spatial;

	private String title;

	private String dataStructureSpecification;

	@NonNull
	@NotNull(message = "dataMappings field is mandatory")
	@Valid
	private List<DataMapping> dataMappings;

	@NonNull
	@NotNull(message = "created field is mandatory")
	@Valid
	private ZonedDateTime created;

}
