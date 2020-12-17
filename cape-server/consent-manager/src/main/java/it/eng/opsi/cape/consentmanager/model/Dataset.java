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
package it.eng.opsi.cape.consentmanager.model;

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
	@JsonProperty(value = "purposeName")
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
