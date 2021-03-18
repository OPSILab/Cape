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
package it.eng.opsi.cape.auditlogmanager.repository;

import java.util.Optional;

import it.eng.opsi.cape.auditlogmanager.model.audit.AuditLog;
import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.Storage;

public interface AuditLogCustomRepository {

	public Optional<AuditLog> incLinkedService(String accountId);

	public Optional<AuditLog> decLinkedService(String accountId);

	public Optional<AuditLog> incGivenConsent(String accountId);

	public Optional<AuditLog> decGivenConsent(String accountId);

	public Optional<AuditLog> addGivenConsent(String accountId, Integer increment);

	public Optional<AuditLog> subtractGivenConsent(String accountId, Integer increment);

	public Optional<AuditLog> incProcessedPersonalData(String accountId);

	public Optional<AuditLog> decProcessedPersonalData(String accountId);

	public Optional<AuditLog> addProcessedPersonalData(String accountId, Integer increment);

	public Optional<AuditLog> subtractPersonalData(String accountId, Integer increment);

	public Optional<AuditLog> addProcessedPersonalData(String accountId, DataMapping concept);

	public Optional<AuditLog> subtractProcessedPersonalData(String accountId, DataMapping concept);

	public Optional<AuditLog> addProcessingPersonalData(String accountId, ProcessingCategory processing,
			DataMapping concept);

	public Optional<AuditLog> subtractProcessingPersonalData(String accountId, ProcessingCategory processing,
			DataMapping concept);

	public Optional<AuditLog> addPurposeCategory(String accountId, PurposeCategory purposeCategory);

	public Optional<AuditLog> subtractPurposeCategory(String accountId, PurposeCategory purposeCategory);

	public Optional<AuditLog> addLegalBasis(String accountId, ProcessingBasis.LegalBasis legalBasis);

	public Optional<AuditLog> subtractLegalBasis(String accountId, ProcessingBasis.LegalBasis legalBasis);

	public Optional<AuditLog> addStoragePersonalData(String accountId, Storage.Location location, DataMapping concept);

	public Optional<AuditLog> subtractStoragePersonalData(String accountId, Storage.Location location,
			DataMapping concept);
}
