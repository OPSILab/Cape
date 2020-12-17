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
//import org.quartz.Job;
//import org.quartz.JobDataMap;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.quartz.JobKey;
//
//import it.eng.opsi.cape.consentmanager.notarization.model.Asset;
//import it.eng.opsi.cape.consentmanager.notarization.model.AssetGroup;
//import it.eng.opsi.cape.consentmanager.notarization.model.BasicTransactionInfo;
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//public class ConsentNotarizationTransactionJob implements Job {
//
//	private Integer assetId;
//	private Integer assetGroupId;
//	private String consentRecordId;
//
//	// Check and retrieve the transaction associated to a notarized Consent Record
//	@Override
//	public void execute(JobExecutionContext context) throws JobExecutionException {
//
//		try {
//			JobKey key = context.getJobDetail().getKey();
//			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
//
//			// Call GET information in order to retrieve Transaction hash for the
//			// corresponding AssetGroup
//			String authToken = dataMap.getString("authToken");
//			
//			Asset asset = ConsentNotarizationClient.callGetAssetByAssetId(assetId, authToken);
//
//			AssetGroup assetGroup = asset.getAssetGroup();
//
//			// If asset !isNotarized, the transaction has not been already created, continue
//			// polling
//
//			if (!asset.getIsNotarized()) {
//				dataMap.put("notarizationStatus", false);
//
//				// Else stop the Job scheduling and notify to the Job Listener the Transaction
//				// Info
//			} else {
//
//				BasicTransactionInfo basicTransaction = null;
//
//				if (assetGroup != null)
//					basicTransaction = assetGroup.getTransaction();
//
//				dataMap.put("notarizationStatus", true);
//
//				if (basicTransaction != null) {
//					dataMap.put("notarizationHash", basicTransaction.getTransactionHash());
//					dataMap.put("notarizationTimestamp", basicTransaction.getTimestamp());
//				}
//			}
//
//		} catch (Exception e) {
//			throw new JobExecutionException(e);
//		}
//	}
//
//}
