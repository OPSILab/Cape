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
package it.eng.opsi.cape.auditlogmanager.model.consent;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import it.eng.opsi.cape.serviceregistry.data.Description__2;
import it.eng.opsi.cape.serviceregistry.data.Obligation;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.Recipient;
import it.eng.opsi.cape.serviceregistry.data.ShareWith;
import it.eng.opsi.cape.serviceregistry.data.Storage;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SinkUsageRules {

	@NotBlank(message = "purposeId field is mandatory")
	private String purposeId;

	@NotBlank(message = "purposeCategory field is mandatory")
	private PurposeCategory purposeCategory;

	@NotNull(message = "purposeDescription field is mandatory")
	private List<Description__2> purposeDescription;

	@NotNull(message = "legalBasis field is mandatory")
	private LegalBasis legalBasis;

	@NotBlank(message = "purposeName field is mandatory")
	private String purposeName;

	@NotNull(message = "processingCategories field is mandatory")
	private List<ProcessingCategory> processingCategories;

	@Valid
	@NotNull
	private Policy policy = new Policy();

	private Storage storage;

	private List<Recipient> recipients;

	private List<ShareWith> shareWith;

	private List<Obligation> obligations;

	private String collectionMethod;

	private String termination;
}
