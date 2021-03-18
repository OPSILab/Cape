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
package it.eng.opsi.cape.accountmanager.model.audit;

import java.time.ZonedDateTime;
import java.util.Arrays;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;

@Document("eventLogs")
public class DataProcessingEventLog extends EventLog {

	@NotBlank(message="purposeId field is mandatory")
	private String purposeId;

	@NotBlank(message="purposeCategory field is mandatory")
	private String purposeCategory;

	@NotBlank(message="purposeName field is mandatory")
	private String purposeName;

	@NotNull(message="processingCategories field is mandatory")
	private String[] processingCategories;

	@NotBlank(message="sinkId field is mandatory")
	private String sinkId;

	@NotBlank(message="sourceId field is mandatory")
	private String sourceId;

	@NotNull(message="dataConcepts field is mandatory")
	private String[] dataConcepts;

	@NotNull(message="action field is mandatory")
	private DataProcessingActionType action;

	public DataProcessingEventLog() {
	}

	public DataProcessingEventLog(ZonedDateTime created, EventType type, String accountId, LegalBasis legalBasis,
			String message, String purposeId, String purposeCategory, String purposeName, String[] processingCategories,
			String sinkId, String sourceId, String[] dataConcepts, DataProcessingActionType action) {
		super(created, type, accountId, legalBasis, message);
		this.purposeId = purposeId;
		this.purposeCategory = purposeCategory;
		this.purposeName = purposeName;
		this.processingCategories = processingCategories;
		this.sinkId = sinkId;
		this.sourceId = sourceId;
		this.dataConcepts = dataConcepts;
		this.action = action;
	}

	public String getPurposeId() {
		return purposeId;
	}

	public void setPurposeId(String purposeId) {
		this.purposeId = purposeId;
	}

	public String getPurposeCategory() {
		return purposeCategory;
	}

	public void setPurposeCategory(String purposeCategory) {
		this.purposeCategory = purposeCategory;
	}

	public String getPurposeName() {
		return purposeName;
	}

	public void setPurposeName(String purposeName) {
		this.purposeName = purposeName;
	}

	public String[] getProcessingCategories() {
		return processingCategories;
	}

	public void setProcessingCategories(String[] processingCategories) {
		this.processingCategories = processingCategories;
	}

	public String getSinkId() {
		return sinkId;
	}

	public void setSinkId(String sinkId) {
		this.sinkId = sinkId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String[] getDataConcepts() {
		return dataConcepts;
	}

	public void setDataConcepts(String[] dataConcepts) {
		this.dataConcepts = dataConcepts;
	}

	public DataProcessingActionType getAction() {
		return action;
	}

	public void setAction(DataProcessingActionType action) {
		this.action = action;
	}

	@Override
	public String toString() {
		return "DataProcessingEventLog [purposeId=" + purposeId + ", purposeCategory=" + purposeCategory
				+ ", purposeName=" + purposeName + ", processingCategories=" + Arrays.toString(processingCategories)
				+ ", sinkId=" + sinkId + ", sourceId=" + sourceId + ", dataConcepts=" + Arrays.toString(dataConcepts)
				+ ", action=" + action + "]";
	}

}
