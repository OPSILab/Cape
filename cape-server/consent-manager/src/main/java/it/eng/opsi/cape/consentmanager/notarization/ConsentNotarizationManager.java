///*******************************************************************************
// * CaPe - a Consent Based Personal Data Suite
// *   Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
// * 
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// * 
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// * 
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// ******************************************************************************/
//package it.eng.opsi.cape.consentmanager.notarization;
//
//import javax.ws.rs.core.Response;
//
//import org.quartz.JobDetail;
//import org.quartz.JobKey;
//import org.quartz.Scheduler;
//import org.quartz.SchedulerException;
//import org.quartz.SchedulerFactory;
//import org.quartz.Trigger;
//import org.quartz.TriggerKey;
//
//import static org.quartz.TriggerBuilder.*;
//import org.quartz.impl.StdSchedulerFactory;
//import org.quartz.impl.matchers.GroupMatcher;
//
//import static org.quartz.SimpleScheduleBuilder.*;
//import static org.quartz.JobKey.*;
//import static org.quartz.JobKey.*;
//import static org.quartz.impl.matchers.KeyMatcher.*;
//import static org.quartz.impl.matchers.GroupMatcher.*;
//
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//import it.eng.opsi.cape.consentmanager.model.CapeProperty;
//import it.eng.opsi.cape.consentmanager.model.ConsentRecord;
//import it.eng.opsi.cape.consentmanager.notarization.model.Asset;
//import it.eng.opsi.cape.consentmanager.notarization.model.AssetGroup;
//import it.eng.opsi.cape.consentmanager.notarization.model.AuthenticateResponse;
//import it.eng.opsi.cape.consentmanager.notarization.model.ConsentNotarizationException;
//import it.eng.opsi.cape.consentmanager.utils.PropertyManager;
//
//public class ConsentNotarizationManager {
//
//	private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
//	private static Boolean notarizationEnabled = Boolean
//			.valueOf(PropertyManager.getProperty(CapeProperty.CONSENT_NOTARIZATION_ENABLED));
//	private static Integer notarizationCheckInterval = Integer
//			.parseInt(PropertyManager.getProperty(CapeProperty.CONSENT_NOTARIZATION_CHECK_INTERVAL));
//	private static String notarizationUser = PropertyManager.getProperty(CapeProperty.CONSENT_NOTARIZATION_USER);
//	private static String notarizationPassword = PropertyManager
//			.getProperty(CapeProperty.CONSENT_NOTARIZATION_PASSWORD);
//
//	private static Scheduler scheduler = null;
//
//	static {
//
//		try {
//			if (notarizationEnabled) {
//				scheduler = schedulerFactory.getScheduler();
//				scheduler.start();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static <T extends ConsentRecord> void notarizeConsentRecord(T consentRecord, String accountId)
//			throws ConsentNotarizationException {
//
//		CompletableFuture.runAsync(() -> {
//
//			try {
//				if (notarizationEnabled && scheduler != null && scheduler.isStarted()) {
//
//					AuthenticateResponse authResponse = ConsentNotarizationClient.callAuthenticate(notarizationUser,
//							notarizationPassword);
//					String authToken = authResponse.getToken();
//
//					Asset assetResponse = ConsentNotarizationClient.callAddAssetAsString(consentRecord, authToken);
//					Integer assetId = assetResponse.getId();
//					Response setNotarizationResponse = ConsentNotarizationClient.callSetAssetForNotarization(assetId,
//							authToken);
//
//					if (setNotarizationResponse.getStatus() != 200)
//						throw new RuntimeException("Failed : HTTP error code : " + setNotarizationResponse.getStatus());
//
//					/*
//					 * Reschedule all preceding jobs, in order to force a Notarization check after
//					 * inserting the current one (in case the current notarization trigger a
//					 * transaction for that AssetGroup)
//					 */
//					rescheduleAllConsentNotarizationCheckJobs();
//
//					Integer assetGroupId = assetResponse.getId();
//
//					/*
//					 * * Starts Job which periodically checks if the transaction of the Consent just
//					 * notarized was made available
//					 */
//
//					String consentRecordId = consentRecord.getCommon_part().getCr_id();
//					JobDetail job = ConsentNotarizationTransactionJobDetail.build(accountId, consentRecordId, assetId,
//							assetGroupId, authToken);
//					JobKey jobKey = jobKey(consentRecordId, "consentNotarizationJobsGroup");
//					// Trigger the job to run now, and then every 40 seconds
//					Trigger trigger = newTrigger().withIdentity(consentRecordId, "consentNotarizationTriggersGroup")
//							.startNow()
//							.withSchedule(
//									simpleSchedule().withIntervalInSeconds(notarizationCheckInterval).repeatForever())
//							.build();
//
//					// Adds a listener for the triggered job (one per Consent Record ? )
//					scheduler.getListenerManager().addJobListener(new ConsentNotarizationTransactionJobListener(jobKey),
//							keyEquals(jobKey));
//
//					if (scheduler.checkExists(jobKey))
//						// Tell quartz to schedule the job using our trigger
//						scheduler.unscheduleJob(trigger.getKey());
//
//					scheduler.scheduleJob(job, trigger);
//
//				}
//
//			} catch (Exception e) {
//				e.printStackTrace();
//
//			}
//		});
//	}
//
//	private static void rescheduleAllConsentNotarizationCheckJobs() throws SchedulerException {
//
//		for (String group : scheduler.getTriggerGroupNames()) {
//			// enumerate each trigger in group
//			for (TriggerKey triggerKey : scheduler.getTriggerKeys(groupEquals(group))) {
//
//				// Recreate a new Trigger in order to reschedule the Job associated with the old
//				// one
//				Trigger newTrigger = newTrigger().withIdentity(triggerKey.getName(), "consentNotarizationTriggersGroup")
//						.startNow()
//						.withSchedule(simpleSchedule().withIntervalInSeconds(notarizationCheckInterval).repeatForever())
//						.build();
//
//				scheduler.rescheduleJob(triggerKey, newTrigger);
//			}
//		}
//	}
//
//}
