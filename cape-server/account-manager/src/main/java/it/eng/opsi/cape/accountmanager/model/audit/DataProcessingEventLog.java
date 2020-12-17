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
