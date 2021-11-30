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
package it.eng.opsi.cape.accountmanager.model.consenting;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.eng.opsi.cape.accountmanager.utils.ProcessingCategoriesOrderedSetConverter;
import it.eng.opsi.cape.accountmanager.utils.RecipientsOrderedSetConverter;
import it.eng.opsi.cape.serviceregistry.data.Obligation;
import it.eng.opsi.cape.serviceregistry.data.Organization;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.Recipient;
import it.eng.opsi.cape.serviceregistry.data.Storage;
import it.eng.opsi.cape.serviceregistry.data.TextualDescription__2;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsageRules {

	@NotBlank(message = "purposeId field is mandatory")
	private String purposeId;

	@NotNull(message = "purposeCategory field is mandatory")
	private PurposeCategory purposeCategory;

	@NotNull(message = "purposeDescription field is mandatory")
	private Set<TextualDescription__2> purposeDescription;

	@NotNull(message = "legalBasis field is mandatory")
	private LegalBasis legalBasis;

	@NotBlank(message = "purposeName field is mandatory")
	private String purposeName;

	@NotNull(message = "processingCategories field is mandatory")
	@JsonSerialize(converter = ProcessingCategoriesOrderedSetConverter.class)
	private Set<ProcessingCategory> processingCategories;

	@Valid
//	@NotNull
	private Policy policy = new Policy();

	private Storage storage;

	@JsonSerialize(converter = RecipientsOrderedSetConverter.class)
	private Set<Recipient> recipients;

	private List<Organization> shareWith;

	private List<Obligation> obligations;

	private String collectionMethod;

	private String termination;
}
