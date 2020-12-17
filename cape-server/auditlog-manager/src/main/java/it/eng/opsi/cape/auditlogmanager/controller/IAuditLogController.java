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
package it.eng.opsi.cape.auditlogmanager.controller;

import org.springframework.http.ResponseEntity;

import it.eng.opsi.cape.auditlogmanager.model.audit.AuditLog;
import it.eng.opsi.cape.exception.AuditLogNotFoundException;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.Storage.Location;

public interface IAuditLogController {

	public ResponseEntity<AuditLog> createAuditLog(AuditLog auditLog);

	public ResponseEntity<AuditLog> deleteAuditLog(String accountId, Boolean deleteEventLogs) throws AuditLogNotFoundException;

	public ResponseEntity<AuditLog> getAuditByAccountId(String accountId) throws AuditLogNotFoundException;

	public ResponseEntity<AuditLog> incLinkedServiceToAuditLog(String accountId);

	public ResponseEntity<AuditLog> decLinkedServiceToAuditLog(String accountId);

	public ResponseEntity<AuditLog> incGivenConsentsToAuditLog(String accountId);

	public ResponseEntity<AuditLog> decGivenConsentsToAuditLog(String accountId);

	public ResponseEntity<AuditLog> incProcessedPersonalDataToAuditLog(String accountId);

	public ResponseEntity<AuditLog> decProcessedPersonalDataToAuditLog(String accountId);

	public ResponseEntity<AuditLog> addProcessedPersonalDataToAuditLog(String accountId, String conceptId);

	public ResponseEntity<AuditLog> subtractProcessedPersonalDataToAuditLog(String accountId, String conceptId);

	public ResponseEntity<AuditLog> addProcessingCategoryPersonalDataToAuditLog(String accountId,
			ProcessingCategory processingCategory, String conceptId);

	public ResponseEntity<AuditLog> subtractProcessingCategoryPersonalDataToAuditLog(String accountId,
			ProcessingCategory processingCategory, String conceptId);

	public ResponseEntity<AuditLog> addPurposeCategoryPersonalDataToAuditLog(String accountId, String purposeCategory);

	public ResponseEntity<AuditLog> subtractPurposeCategoryPersonalDataToAuditLog(String accountId,
			String purposeCategory);

	public ResponseEntity<AuditLog> incLegalBasisToAuditLog(String accountId, LegalBasis legalBasis);

	public ResponseEntity<AuditLog> decLegalBasisToAuditLog(String accountId, LegalBasis legalBasis);

	public ResponseEntity<AuditLog> addStoragePersonalDataToAuditLog(String accountId, Location storage,
			String conceptId);

	public ResponseEntity<AuditLog> substactStoragePersonalDataToAuditLog(String accountId, Location storage,
			String conceptId);

}
