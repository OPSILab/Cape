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
//import java.util.Date;
//
//import org.quartz.JobDataMap;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.quartz.JobKey;
//import org.quartz.SchedulerException;
//import org.quartz.TriggerKey;
//import org.quartz.listeners.JobListenerSupport;
//
//import it.eng.opsi.cape.consentmanager.model.CapeProperty;
//import it.eng.opsi.cape.consentmanager.model.ConsentNotarizationPart;
//import it.eng.opsi.cape.consentmanager.model.ConsentRecordSink;
//import it.eng.opsi.cape.consentmanager.model.exception.AccountNotFoundException;
//import it.eng.opsi.cape.consentmanager.model.exception.ConsentManagerException;
//import it.eng.opsi.cape.consentmanager.model.exception.ServiceLinkRecordNotFoundException;
//import it.eng.opsi.cape.consentmanager.model.exception.SinkConsentRecordNotFoundException;
//import it.eng.opsi.cape.consentmanager.repository.ConsentDAO;
//import it.eng.opsi.cape.consentmanager.utils.PropertyManager;
//
//public class ConsentNotarizationTransactionJobListener extends JobListenerSupport {
//
//	private static ConsentDAO dao = new ConsentDAO(PropertyManager.getProperty(CapeProperty.ACCOUNT_COLLECTION_NAME));
//	private JobKey jobKey;
//
//	@Override
//	public String getName() {
//		// TODO Auto-generated method stub
//		return this.jobKey.getName() + ":" + this.jobKey.getGroup();
//	}
//
//	@Override
//	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
//
//		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
//
//		String accountId = dataMap.getString("accountId");
//		Boolean notarizationStatus = dataMap.getBoolean("notarizationStatus");
//		System.out.println("Job Listener for Asset id: " + dataMap.get("assetId"));
//		/*
//		 * If The Consent was notarized, unschedule the job and Update the related
//		 * Consent Record with updated Transaction Info
//		 */
//		if (notarizationStatus)
//			try {
//				String notarizationHash = dataMap.getString("notarizationHash");
//				String consentRecordId = dataMap.getString("consentRecordId");
//				Date notarizationTimestamp = (Date) dataMap.get("notarizationTimestamp");
//
//				ConsentRecordSink consentRecordSink = dao.getConsentRecordSinkByConsentId(accountId, consentRecordId);
//				consentRecordSink.setConsentNotarization(
//						new ConsentNotarizationPart(notarizationStatus, notarizationHash, notarizationTimestamp));
//				dao.updateConsentRecordSink(accountId, consentRecordSink);
//
//				context.getScheduler()
//						.unscheduleJob(TriggerKey.triggerKey(consentRecordId, "consentNotarizationGroup"));
//
//			} catch (SchedulerException | ConsentManagerException | AccountNotFoundException
//					| SinkConsentRecordNotFoundException e) {
//				e.printStackTrace();
//			}
//
//	}
//
//	public ConsentNotarizationTransactionJobListener(JobKey jobKey) {
//
//		this.jobKey = jobKey;
//	}
//
//	public ConsentNotarizationTransactionJobListener() {
//	}
//
//}
