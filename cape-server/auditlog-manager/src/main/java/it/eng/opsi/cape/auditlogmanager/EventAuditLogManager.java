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
package it.eng.opsi.cape.auditlogmanager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import it.eng.opsi.cape.auditlogmanager.model.audit.AuditLog;
import it.eng.opsi.cape.auditlogmanager.model.audit.AuditDataMapping;
import it.eng.opsi.cape.auditlogmanager.model.consent.ConsentActionType;
import it.eng.opsi.cape.auditlogmanager.model.consent.ConsentEventLog;
import it.eng.opsi.cape.auditlogmanager.model.consent.ConsentRecordStatusEnum;
import it.eng.opsi.cape.auditlogmanager.model.consent.SinkUsageRules;
import it.eng.opsi.cape.auditlogmanager.model.event.EventLog;
import it.eng.opsi.cape.auditlogmanager.model.event.EventType;
import it.eng.opsi.cape.auditlogmanager.model.servicelink.ChangeSlrStatusRequestFrom;
import it.eng.opsi.cape.auditlogmanager.model.servicelink.ServiceLinkActionType;
import it.eng.opsi.cape.auditlogmanager.model.servicelink.ServiceLinkEventLog;
import it.eng.opsi.cape.auditlogmanager.repository.AuditLogRepository;
import it.eng.opsi.cape.auditlogmanager.service.ClientService;
import it.eng.opsi.cape.exception.AccountAuditAlreadyPresentException;
import it.eng.opsi.cape.exception.AuditLogException;
import it.eng.opsi.cape.exception.AuditLogNotFoundException;
import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.Storage;

@Service
public class EventAuditLogManager {

	@Autowired
	AuditLogRepository auditLogsRepo;

	@Autowired
	ClientService clientService;

	public AuditLog auditEventLog(EventLog event, String accountId) throws AccountNotFoundException, AuditLogException {

		AuditLog auditLog = null;

		try {
			// Check if Audit already exists for the Account Id
			auditLog = auditLogsRepo.findByAccountId(accountId).get();

		} catch (NoSuchElementException e) {

			auditLog = new AuditLog(accountId, 0, 0, 0, new HashMap<String, AuditDataMapping>(),
					new HashMap<String, HashMap<String, AuditDataMapping>>(), new HashMap<String, Integer>(),
					new HashMap<String, Integer>(), new HashMap<String, HashMap<String, AuditDataMapping>>());
			auditLogsRepo.save(auditLog);

		}

		if (event instanceof ServiceLinkEventLog) {

			/*
			 * If the Service Link Event originated from the Service linked to Cape the
			 * accountId here represents the surrogateId instead. It calls Account Manager
			 * to retrieve final Account Id, starting from Slr id and that surrogateId
			 * 
			 */
			ChangeSlrStatusRequestFrom requestFrom = ((ServiceLinkEventLog) event).getRequestFrom();
			if (requestFrom != null && requestFrom.equals(ChangeSlrStatusRequestFrom.SERVICE))
				accountId = clientService
						.callGetAccountIdFromSlrIdAndSurrogateId(((ServiceLinkEventLog) event).getSlrId(), accountId);

			ServiceLinkEventLog serviceLinkEvent = (ServiceLinkEventLog) event;
			ServiceLinkActionType serviceLinkAction = serviceLinkEvent.getAction();

			if (serviceLinkAction.equals(ServiceLinkActionType.CREATE)
					|| serviceLinkAction.equals(ServiceLinkActionType.ENABLE))
				auditLogsRepo.incLinkedService(accountId);
			else if (serviceLinkAction.equals(ServiceLinkActionType.DISABLE)
					|| serviceLinkAction.equals(ServiceLinkActionType.DELETE))
				auditLogsRepo.decLinkedService(accountId);

		} else if (event instanceof ConsentEventLog) {

			ConsentEventLog consentEvent = (ConsentEventLog) event;
			ConsentActionType consentAction = consentEvent.getAction();
			ConsentRecordStatusEnum previousStatus = consentEvent.getPreviousStatus();
			SinkUsageRules usageRules = consentEvent.getUsageRules();
			Storage storage = usageRules.getStorage();
			DataMapping[] processedConcepts = consentEvent.getDataConcepts();
			List<ProcessingCategory> processingCategories = usageRules.getProcessingCategories();

			/*
			 * Update the Audit considering the new Given Consent or updating Consent
			 * previously disabled
			 */
			if (consentAction.equals(ConsentActionType.GIVE)) {

				// givenConsentsCount
				auditLogsRepo.incGivenConsent(accountId);

				// legalBasisCount
				auditLogsRepo.addLegalBasis(accountId, consentEvent.getLegalBasis());

				// purposeCategoryCount
				auditLogsRepo.addPurposeCategory(accountId, usageRules.getPurposeCategory());

				// totalProcessedPersonalData
				auditLogsRepo.addProcessedPersonalData(accountId, processedConcepts.length);

				for (DataMapping processedConcept : processedConcepts) {

					// processedPersonalDataCount
					auditLogsRepo.addProcessedPersonalData(accountId, processedConcept);

					// processingCategoryPersonalData
					for (ProcessingCategory category : processingCategories)
						auditLogsRepo.addProcessingPersonalData(accountId, category, processedConcept);

					// storageLocationPersonalData
					if (storage != null)
						auditLogsRepo.addStoragePersonalData(accountId, storage.getLocation(), processedConcept);
				}

				/*
				 * Update the Audit considering the Updated Consent
				 */
			} else if (consentAction.equals(ConsentActionType.UPDATE)) {

				DataMapping[] previousConcepts = consentEvent.getPreviousDataConcepts();
				Set<DataMapping> previousSet = new HashSet<>(Arrays.asList(previousConcepts));
				Set<DataMapping> currentSet = new HashSet<>(Arrays.asList(processedConcepts));

				/*
				 * Compares new and previous concepts, to determine the new and the removed ones
				 */
				Set<DataMapping> removedConcepts = Sets.difference(previousSet, currentSet);
				Set<DataMapping> newConcepts = Sets.difference(currentSet, previousSet);

				/*
				 * Update the Audit considering the removed Concepts
				 */

				// totalProcessedPersonalData
				auditLogsRepo.subtractPersonalData(accountId, removedConcepts.size());

				for (DataMapping removedConcept : removedConcepts) {

					// processedPersonalDataCount
					auditLogsRepo.subtractProcessedPersonalData(accountId, removedConcept);

					// processingCategoryPersonalData
					for (ProcessingCategory category : processingCategories)
						auditLogsRepo.subtractProcessingPersonalData(accountId, category, removedConcept);

					// storageLocationPersonalData
					auditLogsRepo.subtractStoragePersonalData(accountId, storage.getLocation(), removedConcept);

				}

				/*
				 * Update the Audit considering the new Concepts
				 */

				// totalProcessedPersonalData
				auditLogsRepo.addProcessedPersonalData(accountId, newConcepts.size());

				for (DataMapping newConcept : newConcepts) {

					// processedPersonalDataCount
					auditLogsRepo.addProcessedPersonalData(accountId, newConcept);

					// processingCategoryPersonalData
					for (ProcessingCategory category : processingCategories)
						auditLogsRepo.addProcessingPersonalData(accountId, category, newConcept);

					// storageLocationPersonalData
					auditLogsRepo.addStoragePersonalData(accountId, storage.getLocation(), newConcept);

				}

				/*
				 * Update the Audit considering the Withdrawn Consent
				 */
			} else if (consentAction.equals(ConsentActionType.WITHDRAW)) {

				// givenConsentsCount
				auditLogsRepo.decGivenConsent(accountId);

				// TODO ADD withdrawnConsents?

				// legalBasisCount
				auditLogsRepo.subtractLegalBasis(accountId, consentEvent.getLegalBasis());

				// purposeCategoryCount
				auditLogsRepo.subtractPurposeCategory(accountId, usageRules.getPurposeCategory());

				// totalProcessedPersonalData
				auditLogsRepo.subtractPersonalData(accountId, processedConcepts.length);

				for (DataMapping processedConcept : processedConcepts) {

					// processedPersonalDataCount
					auditLogsRepo.subtractProcessedPersonalData(accountId, processedConcept);

					// processingCategoryPersonalData
					for (ProcessingCategory category : processingCategories)
						auditLogsRepo.subtractProcessingPersonalData(accountId, category, processedConcept);

					// storageLocationPersonalData
					auditLogsRepo.subtractStoragePersonalData(accountId, storage.getLocation(), processedConcept);
				}

			}

		} else {

			// Data Processing Event
		}

		return auditLog;
	}

}
