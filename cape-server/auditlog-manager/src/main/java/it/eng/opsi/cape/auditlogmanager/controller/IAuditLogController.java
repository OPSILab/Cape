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
package it.eng.opsi.cape.auditlogmanager.controller;

import org.springframework.http.ResponseEntity;

import it.eng.opsi.cape.auditlogmanager.model.audit.AuditLog;
import it.eng.opsi.cape.exception.AuditLogNotFoundException;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;
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

	public ResponseEntity<AuditLog> addPurposeCategoryPersonalDataToAuditLog(String accountId, PurposeCategory purposeCategory);

	public ResponseEntity<AuditLog> subtractPurposeCategoryPersonalDataToAuditLog(String accountId,
			PurposeCategory purposeCategory);

	public ResponseEntity<AuditLog> incLegalBasisToAuditLog(String accountId, LegalBasis legalBasis);

	public ResponseEntity<AuditLog> decLegalBasisToAuditLog(String accountId, LegalBasis legalBasis);

	public ResponseEntity<AuditLog> addStoragePersonalDataToAuditLog(String accountId, Location storage,
			String conceptId);

	public ResponseEntity<AuditLog> substactStoragePersonalDataToAuditLog(String accountId, Location storage,
			String conceptId);

}
