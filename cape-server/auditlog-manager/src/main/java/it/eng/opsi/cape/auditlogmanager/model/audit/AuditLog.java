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
package it.eng.opsi.cape.auditlogmanager.model.audit;

import java.util.HashMap;
import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document("auditLogs")
public class AuditLog {

	@Id
	@JsonIgnore
	private ObjectId _id;

	@NotBlank(message = "accountId field is mandatory")
	@Indexed(unique = true)
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
