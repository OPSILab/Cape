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
package it.eng.opsi.cape.auditlogmanager.repository;

import java.util.Optional;

import it.eng.opsi.cape.auditlogmanager.model.audit.AuditLog;
import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis;
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

	public Optional<AuditLog> addPurposeCategory(String accountId, String purposeCategory);

	public Optional<AuditLog> subtractPurposeCategory(String accountId, String purposeCategory);

	public Optional<AuditLog> addLegalBasis(String accountId, ProcessingBasis.LegalBasis legalBasis);

	public Optional<AuditLog> subtractLegalBasis(String accountId, ProcessingBasis.LegalBasis legalBasis);

	public Optional<AuditLog> addStoragePersonalData(String accountId, Storage.Location location, DataMapping concept);

	public Optional<AuditLog> subtractStoragePersonalData(String accountId, Storage.Location location,
			DataMapping concept);
}
