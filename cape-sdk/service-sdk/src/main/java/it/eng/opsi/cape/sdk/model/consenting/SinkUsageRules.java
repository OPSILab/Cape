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

import it.eng.opsi.cape.serviceregistry.data.Description__2;
import it.eng.opsi.cape.serviceregistry.data.Obligation;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
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
	private String purposeCategory;

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
