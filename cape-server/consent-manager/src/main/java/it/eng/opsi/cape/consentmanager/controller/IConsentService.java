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
//package it.eng.opsi.cape.consentmanager.controller;
//
//import javax.ws.rs.core.Response;
//
//import it.eng.opsi.cape.consentmanager.model.ConsentRecordStatusEnum;
//
//public interface IConsentService {
//	public Response giveConsent(String consentForm, String accountId);
//
//	public Response updateConsent(String sinkSourceConsentRecords, String accountId);
//
//	public Response fetchConsentForm(String accountId, String sinkId, String sourceId, String purposeId,
//			String sourceDatasetId) throws Exception;
//
//	public Response verifySinkConsent(String sinkConsentId);
//
//	public Response getSinkConsentRecordsByAccountId(String accountId);
//
//	public Response getSourceConsentRecordsByAccountId(String accountId);
//
//	// public Response getConsentStatusRecords(String accountId, String rs_id);
//	public Response getAllConsentRecordsByAccountId(String accountId);
//
//	public Response changeConsentRecordStatus(String accountId, String rs_id, ConsentRecordStatusEnum status);
//
//	public Response withDrawConsentByServiceid(String serviceId);
//
//	public Response getAllConsentRecordsByAccountIdAndSlrId(String accountId, String slr);
//
//	public Response getAllActiveConsentByAccountIdSlr(String accountId, String slr);
//
//	public Response getServiceActiveConsentByAccountIdSlr(String accountId, String slr, String serviceId);
//
//}
