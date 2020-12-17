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
package it.eng.opsi.cape.accountmanager.model.audit;

import java.util.HashMap;
import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuditLog {

	@Id
	@JsonIgnore
	private ObjectId _id;

	@NotBlank(message = "accountId field is mandatory")
	@Indexed
	private String accountId;

	private Integer linkedServicesCount;
	private Integer givenConsentsCount;
	private Integer totalProcessedPersonalDataCount;
	private HashMap<String, AuditDataMapping> processedPersonalDataCount;
	private HashMap<String, HashMap<String, AuditDataMapping>> processingCategoryPersonalData;
	private HashMap<String, Integer> purposeCategoryCount;
	private HashMap<String, Integer> legalBasisCount;
	private HashMap<String, HashMap<String, AuditDataMapping>> storageLocationPersonalData;

	public AuditLog(String accountId, Integer linkedServices, Integer givenConsents, Integer totalProcessedPersonalData,
			HashMap<String, AuditDataMapping> processedPersonalData,
			HashMap<String, HashMap<String, AuditDataMapping>> processingCategoryPersonalData,
			HashMap<String, Integer> purposeCategoryCount, HashMap<String, Integer> legalBasisCount,
			HashMap<String, HashMap<String, AuditDataMapping>> storageLocationPersonalData) {
		super();
		this.accountId = accountId;
		this.linkedServicesCount = linkedServices;
		this.givenConsentsCount = givenConsents;
		this.totalProcessedPersonalDataCount = totalProcessedPersonalData;
		this.processedPersonalDataCount = processedPersonalData;
		this.processingCategoryPersonalData = processingCategoryPersonalData;
		this.purposeCategoryCount = purposeCategoryCount;
		this.legalBasisCount = legalBasisCount;
		this.storageLocationPersonalData = storageLocationPersonalData;
	}

}
