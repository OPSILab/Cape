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
//package it.eng.opsi.cape.consentmanager.controller;
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
//package it.eng.opsi.cape.consentmanager.service;
//
//import java.io.UnsupportedEncodingException;
//import java.lang.reflect.Type;
//import java.text.SimpleDateFormat;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.NoSuchElementException;
//import java.util.concurrent.CompletableFuture;											  
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//import javax.ws.rs.Consumes;
//import javax.ws.rs.FormParam;
//import javax.ws.rs.GET;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.client.Invocation;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//import org.bson.types.ObjectId;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.springframework.stereotype.Service;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import io.jsonwebtoken.MalformedJwtException;
//import it.eng.opsi.cape.consentmanager.dao.ConsentDAO;
//import it.eng.opsi.cape.consentmanager.model.CapeProperty;
//import it.eng.opsi.cape.consentmanager.model.ErrorResponse;
//import it.eng.opsi.cape.consentmanager.model.ServiceLinkRecord;
//import it.eng.opsi.cape.consentmanager.model.audit.ConsentActionType;
//import it.eng.opsi.cape.consentmanager.model.audit.ConsentEventLog;
//import it.eng.opsi.cape.consentmanager.model.audit.EventLog;
//import it.eng.opsi.cape.consentmanager.model.audit.EventType;
//import it.eng.opsi.cape.consentmanager.model.consentRecord.CommonPart;
//import it.eng.opsi.cape.consentmanager.model.consentRecord.ConsentForm;
//import it.eng.opsi.cape.consentmanager.model.consentRecord.ConsentRecordSink;
//import it.eng.opsi.cape.consentmanager.model.consentRecord.ConsentRecordSource;
//import it.eng.opsi.cape.consentmanager.model.consentRecord.ConsentStatusRecord;
//import it.eng.opsi.cape.consentmanager.model.consentRecord.ConsentRecordStatusEnum;
//import it.eng.opsi.cape.consentmanager.model.consentRecord.Dataset;
//import it.eng.opsi.cape.consentmanager.model.consentRecord.RSDescription;
//import it.eng.opsi.cape.consentmanager.model.consentRecord.ResourceSet;
//import it.eng.opsi.cape.consentmanager.model.consentRecord.SinkRoleSpecificPart;
//import it.eng.opsi.cape.consentmanager.model.consentRecord.SinkUsageRule;
//import it.eng.opsi.cape.consentmanager.model.consentRecord.SourceRoleSpecificPart;
//import it.eng.opsi.cape.consentmanager.model.exception.AccountManagerException;
//import it.eng.opsi.cape.consentmanager.model.exception.AccountNotFoundException;
//import it.eng.opsi.cape.consentmanager.model.exception.ConsentManagerException;
//import it.eng.opsi.cape.consentmanager.model.exception.ConsentUtilsException;
//import it.eng.opsi.cape.consentmanager.model.exception.DataMappingNotFoundException;
//import it.eng.opsi.cape.consentmanager.model.exception.DatasetIdNotFoundException;
//import it.eng.opsi.cape.consentmanager.model.exception.PurposeIdNotFoundException;
//import it.eng.opsi.cape.consentmanager.model.exception.ServiceLinkRecordNotFoundException;
//import it.eng.opsi.cape.consentmanager.model.exception.SinkConsentRecordNotFoundException;
//import it.eng.opsi.cape.consentmanager.model.exception.SourceConsentRecordNotFoundException;
//import it.eng.opsi.cape.consentmanager.notarization.ConsentNotarizationManager;
//import it.eng.opsi.cape.consentmanager.notarization.model.ConsentNotarizationException;													   																				   
//import it.eng.opsi.cape.consentmanager.utils.CoberturaIgnore;
//import it.eng.opsi.cape.consentmanager.utils.DAOUtils;
//import it.eng.opsi.cape.consentmanager.utils.JWTUtils;
//import it.eng.opsi.cape.consentmanager.utils.PropertyManager;
//import it.eng.opsi.cape.serviceregistry.data.DataMapping;
//import it.eng.opsi.cape.serviceregistry.data.IsDescribedAt;
//import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis;
//import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
//import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;
//import it.eng.opsi.cape.serviceregistry.data.ServiceInstance;														 
//
//@Service("ConsentService")
//
//@Path("/v1")
//public class ConsentServiceImpl implements IConsentService {
//
//	private ConsentDAO dao = new ConsentDAO(PropertyManager.getProperty(CapeProperty.ACCOUNT_COLLECTION_NAME));
//	private static Boolean notarizationEnabled = Boolean
//	.valueOf(PropertyManager.getProperty(CapeProperty.CONSENT_NOTARIZATION_ENABLED));
//										 
//
//	@CoberturaIgnore
//	@Override
//	@POST
//	@Path("/findConsent/serviceId")
//	@Consumes({ MediaType.APPLICATION_JSON })
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response withDrawConsentByServiceid(@PathParam("serviceId") String serviceId) {
//		return null;
//	}
//
//	@CoberturaIgnore
//	@Override
//	@POST
//	@Path("/changeConsentRecordStatus/{accountId}/{resourceSetId}/{status}")
//	@Consumes({ MediaType.APPLICATION_JSON })
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response changeConsentRecordStatus(@PathParam("accountId") String accountId,
//			@PathParam("resourceSetId") String resourceSetId, @PathParam("status") ConsentRecordStatusEnum status) {
//
//		try {
//
//			ConsentRecordSource crSource = dao.findSourceConsentRecordByRes_id(resourceSetId, accountId);
//			ConsentRecordSink crSink = dao.findSinkConsentRecordByRes_id(resourceSetId, accountId);
//			String sourceId = crSource.getCommon_part().getSubject_id();
//			String sinkId = crSink.getCommon_part().getSubject_id();
//			String sourceConsentRecordId = crSource.getCommon_part().getSurrogate_id();
//			String sinkConsentRecordId = crSink.getCommon_part().getSurrogate_id();
//			ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
//
//			List<ConsentStatusRecord> sourceStatusList = crSource.getConsentStatusList();
//			List<ConsentStatusRecord> sinkStatusList = crSink.getConsentStatusList();
//			ConsentStatusRecord lastCSR = sourceStatusList.get(sourceStatusList.size() - 1);
//
//			Boolean validStatusSource = false;
//			ConsentActionType actionType;
//
//			if (lastCSR.getConsent_status().equals(ConsentRecordStatusEnum.ACTIVE)) {
//
//				if (status.equals(ConsentRecordStatusEnum.DISABLED)
//						|| status.equals(ConsentRecordStatusEnum.WITHDRAWN)) {
//
//					validStatusSource = true;
//
//				}
//			}
//			if ((lastCSR.getConsent_status().equals(ConsentRecordStatusEnum.DISABLED))) {
//				if (status.equals(ConsentRecordStatusEnum.ACTIVE) || status.equals(ConsentRecordStatusEnum.WITHDRAWN)) {
//					validStatusSource = true;
//				}
//			}
//			lastCSR = sinkStatusList.get(sinkStatusList.size() - 1);
//			Boolean validStatusSink = false;
//
//			if (lastCSR.getConsent_status().equals(ConsentRecordStatusEnum.ACTIVE)) {
//				if (status.equals(ConsentRecordStatusEnum.DISABLED)
//						|| status.equals(ConsentRecordStatusEnum.WITHDRAWN)) {
//					validStatusSink = true;
//				}
//			}
//			if ((lastCSR.getConsent_status().equals(ConsentRecordStatusEnum.DISABLED))) {
//				if (status.equals(ConsentRecordStatusEnum.ACTIVE) || status.equals(ConsentRecordStatusEnum.WITHDRAWN)) {
//					validStatusSink = true;
//				}
//			}
//
//			if (validStatusSource && validStatusSink) {
//
//				/*
//				 * Determine Consent Action Type
//				 */
//				switch (status) {
//				case DISABLED:
//					actionType = ConsentActionType.DISABLE;
//					break;
//				case WITHDRAWN:
//					actionType = ConsentActionType.WITHDRAW;
//					break;
//
//				default:
//					actionType = ConsentActionType.ACTIVATE;
//					break;
//				}
//
//				/*
//				 * Add the new sink and source Consent Status Records
//				 */
//
//				sinkStatusList.add(new ConsentStatusRecord(new ObjectId().toString(), "",
//						crSink.getCommon_part().getSurrogate_id(), sinkConsentRecordId, status, now, null));
//
//				sourceStatusList.add(new ConsentStatusRecord(new ObjectId().toString(), "",
//						crSource.getCommon_part().getSurrogate_id(), sourceConsentRecordId, status, now, null));
//
//				/*
//				 * Sign the updated Consent Record
//				 */
//				crSource.setConsentSignedToken(JWTUtils.createJWT(crSource));
//				crSink.setConsentSignedToken(JWTUtils.createJWT(crSink));
//
//				/*
//				 * Store the updated Consent Records and send them to Sink and Source
//				 */
//				List<Dataset> sinkDatasetList = crSink.getCommon_part().getRs_description().getResource_set()
//						.getDatasets();
//
//				DataMapping[] usedConcepts = sinkDatasetList.get(sinkDatasetList.size() - 1).getDataMappings().stream()
//						.toArray(DataMapping[]::new);
//				SinkUsageRule sinkUsageRule = crSink.getRole_specific_part().getUsageRules().get(0);
//
//				dao.updateConsentRecordSink(accountId, crSink);
//
//				/*
//				 * Notarize the Sink part of the Consent
//				 */
//				if (notarizationEnabled)
//					ConsentNotarizationManager.notarizeConsentRecord(crSink, accountId);
//
//
//				ConsentServiceUtils.sendConsentToSink(DAOUtils.obj2Json(crSink, ConsentRecordSink.class), sourceId,
//						crSink.getCommon_part().getRs_description().getResource_set().getDatasets().get(0).getId());
//
//				dao.updateConsentRecordSource(accountId, crSource);
//				ConsentServiceUtils.sendConsentToSource(DAOUtils.obj2Json(crSource, ConsentRecordSource.class), sinkId);
//
//				// Audit for Update Consent
//				ConsentServiceUtils.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, accountId,
//						LegalBasis.CONSENT, "Consent Status Record updated on " + DAOUtils.formatDateToLocalized(now),
//						sinkUsageRule, sinkId, sourceId, usedConcepts, usedConcepts, actionType, lastCSR.getConsent_status(),
//						crSink.getCommon_part().getCr_id()));
//
//				// Audit of consent sending
//				ConsentServiceUtils.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, accountId,
//						LegalBasis.CONSENT, "Consent Record sent on " + DAOUtils.formatDateToLocalized(now), sinkUsageRule, sinkId,
//						sourceId, usedConcepts, usedConcepts, ConsentActionType.SEND, lastCSR.getConsent_status(),
//						crSink.getCommon_part().getCr_id()));
//
//				return Response.status(Response.Status.OK).entity("{ \"Response\": \"Consent Record Status Changed\" }")
//						.build();
//
//			} else {
//				throw new ConsentUtilsException(
//						"Bad status changed from: " + lastCSR.getConsent_status() + "  to: " + status);
//			}
//
//		} catch (SourceConsentRecordNotFoundException e) {
//			return ConsentServiceUtils.handleNotFoundErrorResponse(e);
//		} catch (ConsentUtilsException | UnsupportedEncodingException e) {
//			return ConsentServiceUtils.handleBadRequestErrorResponse(e);
//		} catch (Exception e) {
//			return ConsentServiceUtils.handleInternalServerErrorResponse(e);
//		}
//
////	}
//
//	@Override
//	@GET
//	@Path("/fetchConsentForm/{accountId}/{sinkId}/{sourceId}/{purposeId}/{sourceDatasetId}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response fetchConsentForm(@PathParam("accountId") String accountId, @PathParam("sinkId") String sinkId,
//			@PathParam("sourceId") String sourceId, @PathParam("purposeId") String purposeId,
//			@PathParam("sourceDatasetId") String sourceDatasetId) {
//
//		
//
//		try {
//
//			ServiceEntry sink = ConsentServiceUtils.getServiceDescriptionFromRegistry(sinkId);
//			ServiceEntry source = ConsentServiceUtils.getServiceDescriptionFromRegistry(sourceId);
//
//			/*
//			 * *****************************************************************************
//			 * 
//			 * Create the Proposed Resource Set by matching the Data Mappings. Starting from
//			 * the input Sink Purpose Id, get the required datasets. For each sink required
//			 * dataset for the purpose, match its data mapping fields (by the conceptId) to
//			 * the ones coming from the Source Dataset (sourceDatasetId in input)
//			 * 
//			 **********************************************************************************/
//
//			matchedDataMapping = getMatchedDataMapping(purposeId, sourceDatasetId, sink, source);
//
//			Dataset dataset = new Dataset(sourceDatasetId, purposeId, matchedDataMapping,
//					ZonedDateTime.now(ZoneId.of("UTC")));
//			
//
//			// TODO Check if needed with Mydata 2.0 flow
//			dataset.setStatus(true);
//
//			List<Dataset> datasetLists = new ArrayList<Dataset>();
//			datasetLists.add(dataset);
//
//			// TODO Generate Resource Set Id (should be sourceUri:resourceKey)
//			String rsId = new ObjectId().toString();
//			ResourceSet proposedResourceSet = new ResourceSet(rsId, datasetLists);
//
//			/*
//			 * Create the Sink Usage Rule
//			 * 
//			 */
//			ProcessingBasis sinkPurpose = sink.getProcessingBases().stream()
//					.filter(basis -> basis.getPurposeId().equals(purposeId)).findFirst().get();
//
//			// Create the sink service description digest
//			// TODO Service description digest
//
//			SinkUsageRule sinkUsageRule = new SinkUsageRule(purposeId, sinkPurpose.getPurposeCategory(),
//					sinkPurpose.getLegalBasis(), sinkPurpose.getPurposeName(), sinkPurpose.getProcessingCategories(),
//					sinkPurpose.getPolicyRef(), sinkPurpose.getStorage(), sinkPurpose.getRecipients(),
//					sinkPurpose.getShareWith(), sinkPurpose.getObligations(), sinkPurpose.getCollectionMethod(),
//					sinkPurpose.getTermination());
//
//			/*
//			 * *****************************************************************************
//			 * Create the final Consent Form to be returned
//			 *******************************************************************************/
//			ServiceInstance sinkServiceInstance = sink.getServiceInstance();
//												   
//
//			ConsentForm consentForm = new ConsentForm(rsId, sourceId, sinkId, source.getName(), sink.getName(),
//					source.getHumanReadableDescription().get(0).getDescription(),
//					sink.getHumanReadableDescription().get(0).getDescription(), proposedResourceSet, sinkUsageRule,
//					sinkServiceInstance.getServiceProvider().getJurisdiction(), sinkServiceInstance.getDataController(),
//					"", sink.getServiceDescriptionVersion());
//
//			// TODO --- Save Consent Form in DB?
//			// dao.addConsentForm(accountId, DAOUtils.obj2Json(consentForm,
//			// ConsentForm.class));
//
//			return Response.status(Response.Status.OK)
//					.entity(DAOUtils.obj2Json(consentForm, ConsentForm.class).toString()).build();
//
//		} catch (Exception e) {
//			return ConsentServiceUtils.handleInternalServerErrorResponse(e);
//		}
//	}
//
//	private List<DataMapping> getMatchedDataMapping(String purposeId, String sourceDatasetId, ServiceEntry sink,
//			ServiceEntry source) throws DataMappingNotFoundException, DatasetIdNotFoundException, Exception {
//
//		List<DataMapping> matchingDataMapping = new ArrayList<DataMapping>();
//		List<String> requiredSinkDatasetIds = null;
//
//		/* Get the datasets required by the input Purpose Id */
//		try {
//			requiredSinkDatasetIds = sink.getProcessingBases().stream()
//					.filter(base -> base.getPurposeId().equals(purposeId)).findFirst().get().getRequiredDatasets();
//		} catch (NoSuchElementException e) {
//			throw new DataMappingNotFoundException("The required purpose Id: " + purposeId
//					+ " was not found in the processing bases description of the sink with ID: " + sink.getServiceId());
//		}
//
//		Map<String, IsDescribedAt> sinkDatasetMap = sink.getIsDescribedAt().stream()
//				.collect(Collectors.toMap(IsDescribedAt::getDatasetId, Function.identity()));
//
//		/* Get the sourceDataset according to the input sourceDatasetId */
//		IsDescribedAt sourceDataset = null;
//		try {
//			sourceDataset = source.getIsDescribedAt().stream().filter(d -> d.getDatasetId().equals(sourceDatasetId))
//					.findFirst().get();
//		} catch (NoSuchElementException e) {
//			throw new DataMappingNotFoundException("The required source Dataset Id: " + sourceDatasetId
//					+ " was not found in the description of the Source Service with Id: " + source.getServiceId());
//		}
//
//		/* Put all the source DataMapping for the source dataset in a map */
//		Map<String, DataMapping> sourceDataMappingMap = sourceDataset.getDataMapping().stream()
//				.collect(Collectors.toMap(DataMapping::getConceptId, Function.identity()));
//
//		/*
//		 ** Collect all the data mapping fields coming from each dataset required for the
//		 ** purpose and matching with the ones contained in the source dataset
//		 */
//
//		for (String sinkDatasetId : requiredSinkDatasetIds) {
//			IsDescribedAt sinkDataset = sinkDatasetMap.get(sinkDatasetId);
//
//			if (sinkDataset == null)
//				throw new DatasetIdNotFoundException("The Sink Dataset Id found in the purpose with id:" + purposeId
//						+ "does not match with any of the sink dataset");
//
//			for (DataMapping sinkDataMapping : sinkDataset.getDataMapping()) {
//
//				if (sourceDataMappingMap.containsKey(sinkDataMapping.getConceptId())) {
//					matchingDataMapping.add(sinkDataMapping);
//				} else if (sinkDataMapping.getRequired().equals(1))
//					throw new DataMappingNotFoundException(
//							"The required sink Concept: " + sinkDataMapping.getConceptId() + " for the purpose Id: "
//									+ purposeId + " was not found in the source dataset with Id:" + sourceDatasetId);
//			}
//
//		}
//
//		return matchingDataMapping;
//
//	}
//
//	@CoberturaIgnore
//	@Override
//	@POST
//	@Path("/verifySinkConsent")
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response verifySinkConsent(final String input) {
//
//		try {
//
//			JSONObject inputJson = new JSONObject(input);
//			String crId = inputJson.getString("cr_id");
//			String slrId = inputJson.getString("slr_id");
//			String surrogateId = inputJson.getString("surrogateId");
//			String crToken = inputJson.getString("crToken");
//			String accountId = inputJson.getString("accountId");
//
//			ConsentRecordSink record = dao.getConsentRecordSinkByConsentId(accountId, crId);
//			List<ConsentStatusRecord> crStatus = record.getConsentStatusList();
//
//			// JWT token checking of the input CR token with the stored one
//
//			JWTUtils.verifyJWT(crToken);
//
//			// check if CR has active CSR
//			// check if input slrId, cr_id and surrogateId matches with the retrieved
//			// ones
//
//			if (crId.equals(record.getCommon_part().getCr_id())
//					&& surrogateId.equals(record.getCommon_part().getSurrogate_id())
//					&& crStatus.get(crStatus.size() - 1).getConsent_status().equals(ConsentRecordStatusEnum.ACTIVE)) {
//				List<Dataset> datasets = record.getCommon_part().getRs_description().getResource_set().getDatasets();
//				List<DataMapping> mapping = datasets.get(datasets.size() - 1).getDataMappings();
//
//				String jsonMapping = DAOUtils.obj2Json(mapping, DAOUtils.dataMappingListType);
//				JSONObject result = new JSONObject();
//				result.put("datamapping", jsonMapping);
//				result.put("result", "success");
//				result.put("message", "The provided CR has been verified successfully");
//				return Response.status(Response.Status.OK).entity(result.toString()).build();
//
//			} else {
//
//				JSONObject result = new JSONObject();
//				result.put("result", "failed");
//				result.put("message", "The provided CR has no active status");
//				return Response.status(Response.Status.OK).entity(result.toString()).build();
//
//			}
//
//		} catch (JSONException e) {
//
//			return ConsentServiceUtils.handleBadRequestErrorResponse(e);
//
//		} catch (SinkConsentRecordNotFoundException e) {
//
//			System.out.println(e.getClass().toString() + ": " + e.getMessage());
//			JSONObject result = new JSONObject();
//			result.put("result", "failed");
//			result.put("message", "The provided CR Id does not match with any SLR");
//			return Response.status(Response.Status.OK).entity(result.toString()).build();
//
//		} catch (SecurityException | MalformedJwtException e) {
//
//			System.out.println(e.getClass().toString() + ": " + e.getMessage());
//			JSONObject result = new JSONObject();
//			result.put("result", "failed");
//			result.put("message", "The provided SLR JWT token is not valid");
//			return Response.status(Response.Status.OK).entity(result.toString()).build();
//
//		} catch (Exception e) {
//			return ConsentServiceUtils.handleInternalServerErrorResponse(e);
//		}
//
//	}
//
//	@CoberturaIgnore
//	private ServiceEntry findDataMappingById(String serviceId) throws ConsentManagerException, ConsentUtilsException {
//
//		Client client = ClientBuilder.newClient();
//		WebTarget webTarget = client.target(PropertyManager.getProperty(CapeProperty.SERVICE_REGISTRY_HOST)
//				+ "/api/v1/services/{id}/servicedatamapping").resolveTemplate("id", serviceId);
//		Invocation.Builder invocationBuilder = webTarget.request();
//		Response response = invocationBuilder.get();
//
//		int status = response.getStatus();
//		String res = response.readEntity(String.class);
//
//		if (status == 200) {
//
//			return DAOUtils.json2Obj(res, ServiceEntry.class);
//
//		} else {
//
//			throw new ConsentManagerException(
//					"There was an error while retrieving Service Data Mapping from Service Manager");
//		}
//	}
//
//	@CoberturaIgnore
//	private ServiceLinkRecord findSlrBySurrogateId(String userId, String serviceId)
//			throws ConsentManagerException, ConsentUtilsException {
//
//		Client client = ClientBuilder.newClient();
//		WebTarget webTarget = client
//				.target(PropertyManager.getProperty(CapeProperty.ACCOUNT_MANAGER_HOST)
//						+ "/api/internal/users/{userId}/services/{serviceId}/serviceLink")
//				.resolveTemplate("userId", userId).resolveTemplate("serviceId", serviceId);
//		Invocation.Builder invocationBuilder = webTarget.request();
//		Response response = invocationBuilder.get();
//
//		int status = response.getStatus();
//		String res = response.readEntity(String.class);
//
//		if (status == 200) {
//
//			return DAOUtils.json2Obj(res, ServiceLinkRecord.class);
//
//		} else {
//
//			throw new ConsentManagerException(
//					"There was an error while retrieving Service link record from consent Manager");
//		}
//	}
//
//	@CoberturaIgnore
//	@POST
//	@Path("/updateConsent/{accountId}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response updateConsent(String input, @PathParam("accountId") String accountId) {
//
//		try {
//
//			JSONObject inputBody = new JSONObject(input);
//			ConsentRecordSink crSink = DAOUtils.json2Obj(inputBody.getJSONObject("consent_record_sink").toString(),
//					ConsentRecordSink.class);
//			ConsentRecordSource crSource = DAOUtils
//					.json2Obj(inputBody.getJSONObject("consent_record_source").toString(), ConsentRecordSource.class);
//
//			ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
//
//			/*
//			 * Take the last Sink Dataset from the input
//			 */
//			ResourceSet sinkResourceSet = crSink.getCommon_part().getRs_description().getResource_set();
//			String sinkId = crSink.getCommon_part().getSubject_id();
//
//			List<Dataset> sinkDatasetList = sinkResourceSet.getDatasets();
//			Dataset sinkDatasetToAdd = sinkDatasetList.get(sinkDatasetList.size() - 1);
//			sinkDatasetToAdd.setCreated(now);
//			sinkDatasetToAdd.setStatus(true);
//			DataMapping[] usedConcepts = sinkDatasetToAdd.getDataMappings().stream().toArray(DataMapping[]::new);
//
//			List<SinkUsageRule> sinkUsageRules = crSink.getRole_specific_part().getUsageRules();
//			SinkUsageRule sinkUsageRule = !sinkUsageRules.isEmpty() ? sinkUsageRules.get(0) : null;
//
//			/*
//			 * Take the last Source Dataset from the input
//			 */
//			ResourceSet sourceResourceSet = crSource.getCommon_part().getRs_description().getResource_set();
//			String sourceId = crSource.getCommon_part().getSubject_id();
//
//			List<Dataset> sourceDatasetList = sourceResourceSet.getDatasets();
//			Dataset sourceDatasetToAdd = sourceDatasetList.get(sourceDatasetList.size() - 1);
//			sourceDatasetToAdd.setCreated(now);
//			sourceDatasetToAdd.setStatus(true);
//
//			/*
//			 * Set the last Dataset of the Source CR to disabled, and add the new one in the
//			 * list as active
//			 */
//
//			ConsentRecordSink crSinkFromDb = dao.getConsentRecordSinkByConsentId(accountId,
//					crSink.getCommon_part().getCr_id());
//			List<Dataset> sinkDatasetListfromDb = crSinkFromDb.getCommon_part().getRs_description().getResource_set()
//					.getDatasets();
//			Dataset lastSinkDataset = sinkDatasetListfromDb.get(sinkDatasetListfromDb.size() - 1);
//			lastSinkDataset.setStatus(false);
//
//	 
//			 /* Set the updated SinkUsageRule coming from the request
//			 * 
//			 */
//			SinkRoleSpecificPart sinkSpecificPart = crSinkFromDb.getRole_specific_part();
//			sinkSpecificPart.setUsageRules(crSink.getRole_specific_part().getUsageRules());
//
//			// Collect the used concepts of the old Dataset, to be used in the
//			// ConsentEventLog
//			DataMapping[] oldUsedConcepts = lastSinkDataset.getDataMappings().stream().toArray(DataMapping[]::new);
//
//			sinkDatasetListfromDb.add(sinkDatasetToAdd);
//
//			dao.updateConsentRecordSink(accountId, crSinkFromDb);
//
//			/*
//			 * Set the last Dataset of the Source CR to disabled, and add the new one in the
//			 * list as active
//			 */
//			ConsentRecordSource crSourceFromDb = dao.getConsentRecordSourceByDatasetId(accountId,
//					sourceDatasetToAdd.getId());
//
//			List<Dataset> sourceDatasetListFromDb = crSourceFromDb.getCommon_part().getRs_description()
//					.getResource_set().getDatasets();
//			Dataset lastSourceDataset = sourceDatasetListFromDb.get(sourceDatasetListFromDb.size() - 1);
//			lastSourceDataset.setStatus(false);
//			sourceDatasetListFromDb.add(sourceDatasetToAdd);
//
//			dao.updateConsentRecordSource(accountId, crSourceFromDb);
//
//             /*
//			 * Notarize the Sink part of the Consent
//			 */
//			if (notarizationEnabled)
//				ConsentNotarizationManager.notarizeConsentRecord(crSink, accountId);
//
//
//			List<ConsentStatusRecord> sinkStatusList = crSink.getConsentStatusList();
//				ConsentRecordStatusEnum lastSinkConsentStatus = sinkStatusList.get(sinkStatusList.size() - 1)
//					.getConsent_status();																				
//			// Audit for update consent
//			ConsentServiceUtils.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, accountId,
//					LegalBasis.CONSENT, "Consent updated on " + DAOUtils.formatDateToLocalized(now), sinkUsageRule, sinkId,
//					sourceId, usedConcepts, oldUsedConcepts, ConsentActionType.UPDATE, lastSinkConsentStatus,
//					crSinkFromDb.getCommon_part().getCr_id()));
//
//			/*
//			 * Send the updated Consent Records to Sink and Source
//			 */
//
//			ConsentServiceUtils.sendConsentToSink(DAOUtils.obj2Json(crSinkFromDb, ConsentRecordSink.class),
//					crSourceFromDb.getCommon_part().getSubject_id(), sinkDatasetToAdd.getId());
//
//			ConsentServiceUtils.sendConsentToSource(DAOUtils.obj2Json(crSourceFromDb, ConsentRecordSource.class),
//					crSinkFromDb.getCommon_part().getSubject_id());
//
//			// Audit of consent sending
//			ConsentServiceUtils.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, accountId,
//					LegalBasis.CONSENT, "Consent sent on " + DAOUtils.formatDateToLocalized(now), sinkUsageRule, sinkId, sourceId,
//					usedConcepts, oldUsedConcepts, ConsentActionType.SEND, lastSinkConsentStatus,
//					crSinkFromDb.getCommon_part().getCr_id()));
//
//			return Response.status(Response.Status.CREATED)
//					.entity("{ \"sink_consent_record\": " + DAOUtils.obj2Json(crSinkFromDb, ConsentRecordSink.class)
//							+ ", \"source_consent_record\": "
//							+ DAOUtils.obj2Json(crSourceFromDb, ConsentRecordSource.class) + "}")
//					.build();
//
//		} catch (SinkConsentRecordNotFoundException e) {
//			return ConsentServiceUtils.handleBadRequestErrorResponse(e);
//		} catch (AccountNotFoundException e) {
//			return ConsentServiceUtils.handleNotFoundErrorResponse(e);
//		} catch (Exception e) {
//			return ConsentServiceUtils.handleInternalServerErrorResponse(e);
//		}
//
//	}
//
//	// MOCKITO/JUNIT TESTED
//	@Override
//	@POST
//	@Path("/giveConsent/{accountId}")
//	@Consumes({ MediaType.APPLICATION_JSON })
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response giveConsent(String consentForm, @PathParam("accountId") String accountId) {
//
//		try {
//
//			ConsentForm consentFormObj = DAOUtils.json2Obj(consentForm, ConsentForm.class);
//			ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
//
//			String sinkId = consentFormObj.getSinkId();
//			String sourceId = consentFormObj.getSourceId();
//
//			/*
//			 * ***************************************************************************
//			 * Get sink and source SLRs, otherwise throw a ServiceLinkNotFoundException
//			 *****************************************************************************/
//
//			// TODO
//			// ServiceLinkRecord sinkSLR = findSlrBySurrogateId(accountId + "." + sinkId,
//			// sinkId);
//			// ServiceLinkRecord sourceSLR = findSlrBySurrogateId(accountId + "." +
//			// sourceId, sourceId);
//
//			ServiceLinkRecord sinkSLR = dao.getServiceLinkRecordByServiceId(accountId, sinkId);
//			ServiceLinkRecord sourceSLR = dao.getServiceLinkRecordByServiceId(accountId, sourceId);
//
//			ResourceSet proposedResourceSet = consentFormObj.getResourceSet();
//			Dataset proposedDataset = proposedResourceSet.getDatasets().get(0);
//			String datasetId = proposedDataset.getId();
//
//			SinkUsageRule proposedUsageRule = consentFormObj.getUsageRule();
//			DataMapping[] proposedConcepts = proposedDataset.getDataMappings().stream().toArray(DataMapping[]::new);
//
//			/*
//			 * Retrieve if already present, the Consents for accountId between sinkId and
//			 * sourceId by source datasetId
//			 */
//			List<Object> consentRecords = dao.getConsentRecordsByServicendDatasetId(accountId, sinkId, sourceId,
//					datasetId);
//			ConsentRecordStatusEnum lastConsentStatus = ConsentRecordStatusEnum.WITHDRAWN;
//
//			ConsentRecordSink crSink = null;
//			ConsentRecordSource crSource = null;
//
//			/*
//			 *****************************************************************************
//			 * 
//			 * 1. The Consent Record for the Sink-Source pair is already present and not
//			 * withdrawn.
//			 * 
//			 * 2.The Consent Record for the Sink-Source pair is new
//			 * 
//			 *******************************************************************/
//
//			/*
//			 * *****************************************************************************
//			 * 
//			 * 1. The Consent Record for the Sink-Source pair is already present and not
//			 * WITHDRAWN
//			 * 
//			 *****************************************************************************/
//
//			if (consentRecords != null) {
//
//				crSink = (ConsentRecordSink) consentRecords.get(0); // first is ConsentRecordSink
//				crSource = (ConsentRecordSource) consentRecords.get(1); // first is ConsentRecordSink
//				List<ConsentStatusRecord> sinkConsentStatusList = crSink.getConsentStatusList();
//				lastConsentStatus = sinkConsentStatusList.get(sinkConsentStatusList.size() - 1).getConsent_status();
//			}
//			/*
//			 ******************************************************************************
//			 *
//			 * 1a. Check Status of the existing Consent Record, if is Active return the
//			 * existing Sink-Source Records
//			 * 
//			 ******************************************************************************/
//
//			if (consentRecords != null && lastConsentStatus.equals(ConsentRecordStatusEnum.ACTIVE)) {
//				return Response.status(Response.Status.ACCEPTED)
//						.entity("{ \"sink_consent_record\": " + DAOUtils.obj2Json(crSink, ConsentRecordSink.class)
//								+ ", \"source_consent_record\": "
//								+ DAOUtils.obj2Json(crSource, ConsentRecordSource.class) + "}")
//						.build();
//
//			} else if (consentRecords != null && lastConsentStatus.equals(ConsentRecordStatusEnum.DISABLED)) {
//
//				/*
//				 *****************************************************************************
//				 * 
//				 * 1b. The Consent Record is present and disabled Add a new active Consent
//				 * Status Record to it
//				 * 
//				 *****************************************************************************/
//
//				String sinkConsentRecordId = crSink.getCommon_part().getCr_id();
//				String sourceConsentRecordId = crSource.getCommon_part().getCr_id();
//
//				/*
//				 * Add the Dataset of the input proposed Resource Set as the last active one of
//				 * the existent Sink and Source Consent Records
//				 */
//
//				ResourceSet sinkResourceSet = crSink.getCommon_part().getRs_description().getResource_set();
//				List<Dataset> sinkDatasetList = sinkResourceSet.getDatasets();
//				Dataset sinkLastDataset = sinkDatasetList.get(sinkDatasetList.size() - 1);
//				sinkLastDataset.setStatus(false);
//				sinkDatasetList.add(proposedDataset);
//
//				/*
//				 * Update with a new Consent Status Record
//				 */
//				String sinkConsentStatusRecordId = new ObjectId().toString();
//				crSink.getConsentStatusList().add(new ConsentStatusRecord(sinkConsentStatusRecordId, "",
//						sinkSLR.getSurrogateId(), sinkConsentRecordId, ConsentRecordStatusEnum.ACTIVE, now, null));
//				dao.updateConsentRecordSink(accountId, crSink);
//
//				ResourceSet sourceResourceSet = crSource.getCommon_part().getRs_description().getResource_set();
//				List<Dataset> sourceDatasetList = sourceResourceSet.getDatasets();
//				Dataset sourceLastDataset = sourceDatasetList.get(sourceDatasetList.size() - 1);
//				sourceLastDataset.setStatus(false);
//				sourceDatasetList.add(proposedDataset);
//
//				/*
//				 * Update with a new Consent Status Record
//				 */
//				String sourceConsentStatusRecordId = new ObjectId().toString();
//				crSource.getConsentStatusList().add(new ConsentStatusRecord(sourceConsentStatusRecordId, "",
//						sourceSLR.getSurrogateId(), sourceConsentRecordId, ConsentRecordStatusEnum.ACTIVE, now, null));
//				dao.updateConsentRecordSource(accountId, crSource);
//
//				// Audit for Update Consent
//				ConsentServiceUtils.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, accountId,
//						LegalBasis.CONSENT, "Consent updated on " + DAOUtils.formatDateToLocalized(now), proposedUsageRule, sinkId,
//						sourceId, proposedConcepts, null, ConsentActionType.UPDATE, lastConsentStatus,
//						crSink.getCommon_part().getCr_id()));
//
//				/*
//				 * Send the updated Consent Records to Sink and Source
//				 */
//
//				ConsentServiceUtils.sendConsentToSink(DAOUtils.obj2Json(crSink, ConsentRecordSink.class), sourceId,
//						proposedDataset.getId());
//
//				ConsentServiceUtils.sendConsentToSource(DAOUtils.obj2Json(crSource, ConsentRecordSource.class), sinkId);
//
//				// Audit of consent sending
//				ConsentServiceUtils.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, accountId,
//						LegalBasis.CONSENT, "Consent sent on " + DAOUtils.formatDateToLocalized(now), proposedUsageRule, sinkId,
//						sourceId, proposedConcepts, proposedConcepts, ConsentActionType.SEND, lastConsentStatus,
//						crSink.getCommon_part().getCr_id()));
//
//				return Response.status(Response.Status.CREATED)
//						.entity("{ \"sink_consent_record\": " + DAOUtils.obj2Json(crSink, ConsentRecordSink.class)
//								+ ", \"source_consent_record\": "
//								+ DAOUtils.obj2Json(crSource, ConsentRecordSource.class) + "}")
//						.build();
//
//			} else {
//
//				/*
//				 * ***************************************************************************
//				 * 2. The Consent Record for the Sink-Source pair is new
//				 ****************************************************************************/
//
//				// dobbiamo controllare se esiste già consent record con altro dataset
//				ConsentRecordSink crsiSecondDataset = dao.getConsentRecordSinkByDatasetId(accountId, datasetId);
//				ConsentRecordSource crsoSecondDataset = dao.getConsentRecordSourceByDatasetId(accountId, datasetId);
//				if (false /* crsiSecondDataset != null */) { // esiste un altro CR
//					// cambio lo stato del CR dell'altro dataset a WITHDRAW
//
//					ConsentStatusRecord sinkCSR = new ConsentStatusRecord(ConsentRecordStatusEnum.WITHDRAWN);
//					ConsentStatusRecord sourceCSR = new ConsentStatusRecord(ConsentRecordStatusEnum.WITHDRAWN);
//
//					List<ConsentStatusRecord> sinkStatusList = crsiSecondDataset.getConsentStatusList();
//					sinkStatusList.add(sinkCSR);
//					crsiSecondDataset.setConsentStatusList(sinkStatusList);
//
//					List<ConsentStatusRecord> sourceStatusList = crsoSecondDataset.getConsentStatusList();
//					sourceStatusList.add(sinkCSR);
//					crsoSecondDataset.setConsentStatusList(sourceStatusList);
//
//					// TODO Audit for Consent update
//					// ConsentServiceUtils.callAddEventLog(accountId, details);
//
//					String sinkSign = JWTUtils.createJWT(crsiSecondDataset);
//					String sourceSign = JWTUtils.createJWT(crsoSecondDataset);
//					crsiSecondDataset.setConsentSignedToken(sinkSign);
//					crsoSecondDataset.setConsentSignedToken(sourceSign);
//
//					// storeSinkConsentRecord(accountId, DAOUtils.obj2Json(crsiSecondDataset,
//					// ConsentRecordSink.class));
//					// storeSourceConsentRecord(accountId, DAOUtils.obj2Json(crsoSecondDataset,
//					// ConsentRecordSource.class));
//
//					dao.updateConsentRecordSink(accountId, crsiSecondDataset);
//					// URL USE ONLY FOR SELECT 4TH ITERATION. TO BE DELETED AS SOON AS POSSIBLE
//					// URL TO STORE CONSENT IN SERVICES
//					ConsentServiceUtils.sendConsentToSink(DAOUtils.obj2Json(crsiSecondDataset, ConsentRecordSink.class),
//							sourceId, crsiSecondDataset.getCommon_part().getRs_description().getResource_set()
//									.getDatasets().get(0).getId());
//
//					// END TO BE REMOVED
//
//					dao.updateConsentRecordSource(accountId, crsoSecondDataset);
//					// URL USE ONLY FOR SELECT 4TH ITERATION. TO BE DELETED AS SOON AS POSSIBLE
//					// URL TO STORE CONSENT IN SERVICES
//					ConsentServiceUtils.sendConsentToSource(
//							DAOUtils.obj2Json(crsoSecondDataset, ConsentRecordSource.class), sinkId);
//					// END TO BE REMOVED
//				}
//
//				/*
//				 * Let's create the new Consent Record
//				 ************************************/
//
//				ServiceEntry sinkService = ConsentServiceUtils.getServiceDescriptionFromRegistry(sinkId);
//				ServiceEntry sourceService = ConsentServiceUtils.getServiceDescriptionFromRegistry(sourceId);
//
//				/*
//				 * TODO Should we check that the input ConsentForm Resource Set contains the
//				 * matching concepts between Sink and Source (as done in the FetchConsentForm
//				 * service) ?
//				 */
//
//				RSDescription resourceSetDescription = new RSDescription();
//				resourceSetDescription.setResource_set(proposedResourceSet);
//
//				String sinkConsentRecordId = new ObjectId().toString();
//				String sourceConsentRecordId = new ObjectId().toString();
//				String sinkConsentStatusRecordId = new ObjectId().toString();
//				String sourceConsentStatusRecordId = new ObjectId().toString();
//
//				String nowString = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(now);
//
//				CommonPart sinkCommonPart = new CommonPart("2.0", sinkConsentRecordId, sinkSLR.getSurrogateId(),
//						resourceSetDescription, sinkSLR.get_id(), nowString, "", "", "_cape", sinkSLR.getServiceId(),
//						consentFormObj.getJurisdiction(), consentFormObj.getDataController(),
//						consentFormObj.getServiceDescriptionDigest(), consentFormObj.getServiceDescriptionVersion(),
//						"sink");
//
//				CommonPart sourceCommonPart = new CommonPart("2.0", sourceConsentRecordId, sourceSLR.getSurrogateId(),
//						resourceSetDescription, sourceSLR.get_id(), nowString, "", "", "_cape",
//						sourceSLR.getServiceId(), consentFormObj.getJurisdiction(), consentFormObj.getDataController(),
//						consentFormObj.getServiceDescriptionDigest(), consentFormObj.getServiceDescriptionVersion(),
//						"source");												  
//
//				SinkRoleSpecificPart sinkRoleSpecificPart = new SinkRoleSpecificPart(Arrays.asList(proposedUsageRule),
//						sourceConsentRecordId);
//
//				ConsentStatusRecord sinkConsentStatusRecord = new ConsentStatusRecord(sinkConsentStatusRecordId, "",
//						sinkSLR.getSurrogateId(), sinkConsentRecordId, ConsentRecordStatusEnum.ACTIVE, now, null);
//
//				ConsentStatusRecord sourceConsentStatusRecord = new ConsentStatusRecord(sourceConsentStatusRecordId, "",
//						sinkSLR.getSurrogateId(), sourceConsentRecordId, ConsentRecordStatusEnum.ACTIVE, now, null);
//
//				crSink = new ConsentRecordSink(sinkCommonPart, Arrays.asList(sinkConsentStatusRecord),
//						sinkRoleSpecificPart);
//				crSink.setConsentSignedToken(JWTUtils.createJWT(crSink));
//
//				SourceRoleSpecificPart sourceRoleSpecificPart = new SourceRoleSpecificPart();
//				crSource = new ConsentRecordSource(sourceCommonPart, Arrays.asList(sourceConsentStatusRecord),
//						sourceRoleSpecificPart);
//				crSource.setConsentSignedToken(JWTUtils.createJWT(crSource));
//
//				dao.addConsentRecordSink(accountId, crSink);
//
//	  /*
//				 * Notarize the Sink part of the Consent
//				 */
//	             if (notarizationEnabled)
//					ConsentNotarizationManager.notarizeConsentRecord(crSink, accountId);
//
//				ConsentServiceUtils.sendConsentToSink(DAOUtils.obj2Json(crSink, ConsentRecordSink.class), sourceId,
//						proposedResourceSet.getDatasets().get(0).getId());
//
//				dao.addConsentRecordSource(accountId, crSource);
//
//				ConsentServiceUtils.sendConsentToSource(DAOUtils.obj2Json(crSource, ConsentRecordSource.class), sinkId);
//
//				// Audit for give consent
//				ConsentServiceUtils
//						.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, accountId, LegalBasis.CONSENT,
//								"Consent given on " + DAOUtils.formatDateToLocalized(now), proposedUsageRule, sinkId,
//								sourceId, proposedConcepts, null, ConsentActionType.GIVE, null, sinkConsentRecordId));
//
//				// Audit of consent sending
//				ConsentServiceUtils.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, accountId,
//						LegalBasis.CONSENT, "Consent sent on " + DAOUtils.formatDateToLocalized(now), proposedUsageRule,
//						sinkId, sourceId, proposedConcepts, null, ConsentActionType.SEND, null, sinkConsentRecordId));
//
//				return Response.status(Response.Status.CREATED)
//						.entity("{ \"sink_consent_record\": " + DAOUtils.obj2Json(crSink, ConsentRecordSink.class)
//								+ ", \"source_consent_record\": "
//								+ DAOUtils.obj2Json(crSource, ConsentRecordSource.class) + "}")
//						.build();
//			}
//
//		} catch (AccountNotFoundException | ServiceLinkRecordNotFoundException e) {
//			return ConsentServiceUtils.handleNotFoundErrorResponse(e);
//		} catch (UnsupportedEncodingException e) {
//			return ConsentServiceUtils.handleBadRequestErrorResponse(e);
//		} catch (Exception e) {
//			return ConsentServiceUtils.handleInternalServerErrorResponse(e);
//		}
//
//	}
//
//	@CoberturaIgnore
//	@Override
//	@GET
//	@Path("/sinkConsentRecords/{accountId}/")
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response getSinkConsentRecordsByAccountId(@PathParam("accountId") String accountId) {
//
//		try {
//
//			List<ConsentRecordSink> consentRecordSink = dao.getSinkConsentRecords(accountId);
//			return Response.status(Response.Status.OK)
//					.entity(DAOUtils.obj2Json(consentRecordSink, new TypeToken<ArrayList<ConsentRecordSink>>() {
//					}.getType())).build();
//
//		} catch (ConsentUtilsException e) {
//			return ConsentServiceUtils.handleBadRequestErrorResponse(e);
//		} catch (SinkConsentRecordNotFoundException e) {
//			return ConsentServiceUtils.handleNotFoundErrorResponse(e);
//		} catch (Exception e) {
//			return ConsentServiceUtils.handleInternalServerErrorResponse(e);
//		}
//
//	}
//
//	@CoberturaIgnore
//	@Override
//	@GET
//	@Path("/sourceConsentRecords/{accountId}/")
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response getSourceConsentRecordsByAccountId(@PathParam("accountId") String accountId) {
//
//		try {
//
//			List<ConsentRecordSource> consentRecordSource = dao.getSourceConsentRecords(accountId);
//			return Response.status(Response.Status.OK)
//					.entity(DAOUtils.obj2Json(consentRecordSource, new TypeToken<ArrayList<ConsentRecordSource>>() {
//					}.getType())).build();
//
//		} catch (SourceConsentRecordNotFoundException e) {
//			return ConsentServiceUtils.handleNotFoundErrorResponse(e);
//		} catch (Exception e) {
//			return ConsentServiceUtils.handleInternalServerErrorResponse(e);
//		}
//
//	}
//
//	@CoberturaIgnore
//	@Override
//	@GET
//	@Path("/consents/{accountId}/")
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response getAllConsentRecordsByAccountId(@PathParam("accountId") String accountId) {
//
//		List<ConsentRecordSink> consentRecordSink;
//		List consentData = new ArrayList();
//		try {
//			consentRecordSink = dao.getSinkConsentRecords(accountId);
//
//			for (ConsentRecordSink crk : consentRecordSink) {
//
//				Map m = new HashMap();
//				ConsentRecordSource crs = dao.findSourceConsentRecordByRes_id(
//						crk.getCommon_part().getRs_description().getResource_set().getRs_id(), accountId);
//
//				String slr_id = crk.getCommon_part().getSlr_id();
//
//				ServiceEntry sinkService = ConsentServiceUtils
//						.getServiceDescriptionFromRegistry(crk.getCommon_part().getSubject_id());
//				ServiceEntry sourceService = ConsentServiceUtils
//						.getServiceDescriptionFromRegistry(crs.getCommon_part().getSubject_id());
//
//				m.put("sinkService", sinkService);
//				m.put("sourceService", sourceService);
//				m.put("rs_id", crk.getCommon_part().getRs_description().getResource_set().getRs_id());
//				m.put("account_id", accountId);
//				m.put("sink", crk);
//				m.put("source", crs);
//				consentData.add(m);
//
//			}
//
//			String array = DAOUtils.obj2Json(consentData, new TypeToken<ArrayList>() {
//			}.getType());
//
//			return Response.status(Response.Status.OK).entity(array).build();
//
//		} catch (Exception e) {
//			return ConsentServiceUtils.handleInternalServerErrorResponse(e);
//		}
//
//	}
//
//	@CoberturaIgnore
//	@Override
//	@GET
//	@Path("/consents/{accountId}/{slr}")
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response getAllConsentRecordsByAccountIdAndSlrId(@PathParam("accountId") String accountId,
//			@PathParam("slr") String slr) {
//
//		List<ConsentRecordSink> consentRecordSink;
//		List consentData = new ArrayList();
//		try {
//			consentRecordSink = dao.getSinkConsentRecords(accountId);
//
//			for (ConsentRecordSink crk : consentRecordSink) {
//
//				if (crk.getCommon_part().getSlr_id().equalsIgnoreCase(slr)) {
//					Map m = new HashMap();
//					ConsentRecordSource crs = dao.findSourceConsentRecordByRes_id(
//							crk.getCommon_part().getRs_description().getResource_set().getRs_id(), accountId);
//
//					String slr_id = crk.getCommon_part().getSlr_id();
//
//					ServiceEntry sinkService = ConsentServiceUtils
//							.getServiceDescriptionFromRegistry(crk.getCommon_part().getSubject_id());
//					ServiceEntry sourceService = ConsentServiceUtils
//							.getServiceDescriptionFromRegistry(crs.getCommon_part().getSubject_id());
//
//					m.put("sinkService", sinkService);
//					m.put("sourceService", sourceService);
//					m.put("rs_id", crk.getCommon_part().getRs_description().getResource_set().getRs_id());
//					m.put("account_id", accountId);
//					m.put("sink", crk);
//					m.put("source", crs);
//					consentData.add(m);
//
//				}
//
//			}
//
//			String array = new Gson().toJson(consentData);
//
//			return Response.status(Response.Status.OK).entity(array).build();
//
//		} catch (Exception e) {
//			return ConsentServiceUtils.handleInternalServerErrorResponse(e);
//		}
//
//	}
//
//	@CoberturaIgnore
//	@Override
//	@GET
//	@Path("/consents/active/{accountId}/{slr}")
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response getAllActiveConsentByAccountIdSlr(@PathParam("accountId") String accountId,
//			@PathParam("slr") String slr) {
//
//		List<ConsentRecordSink> consentRecordSink;
//		List consentData = new ArrayList();
//		try {
//			consentRecordSink = dao.getSinkConsentRecords(accountId);
//
//			for (ConsentRecordSink crk : consentRecordSink) {
//
//				if (crk.getCommon_part().getSlr_id().equalsIgnoreCase(slr)
//						&& crk.getConsentStatusList().get(crk.getConsentStatusList().size() - 1).getConsent_status()
//								.toString().equalsIgnoreCase("ACTIVE")) {
//					Map m = new HashMap();
//					ConsentRecordSource crs = dao.findSourceConsentRecordByRes_id(
//							crk.getCommon_part().getRs_description().getResource_set().getRs_id(), accountId);
//
//					String slr_id = crk.getCommon_part().getSlr_id();
//
//					ServiceEntry sinkService = ConsentServiceUtils
//							.getServiceDescriptionFromRegistry(crk.getCommon_part().getSubject_id());
//					ServiceEntry sourceService = ConsentServiceUtils
//							.getServiceDescriptionFromRegistry(crs.getCommon_part().getSubject_id());
//
//					m.put("sinkService", sinkService);
//					m.put("sourceService", sourceService);
//					m.put("rs_id", crk.getCommon_part().getRs_description().getResource_set().getRs_id());
//					m.put("account_id", accountId);
//					m.put("sink", crk);
//					m.put("source", crs);
//					consentData.add(m);
//
//				}
//
//			}
//
//			String array = new Gson().toJson(consentData);
//
//			return Response.status(Response.Status.OK).entity(array).build();
//
//		} catch (Exception e) {
//			return ConsentServiceUtils.handleInternalServerErrorResponse(e);
//		}
//
//	}
//
//	@CoberturaIgnore
//	@Override
//	@GET
//	@Path("/consents/active/{accountId}/{slr}/{serviceId}")
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response getServiceActiveConsentByAccountIdSlr(@PathParam("accountId") String accountId,
//			@PathParam("slr") String slr, @PathParam("serviceId") String serviceId) {
//
//		List<ConsentRecordSink> consentRecordSink;
//		Map consent = new HashMap();
//		try {
//			consentRecordSink = dao.getSinkConsentRecords(accountId);
//
//			for (ConsentRecordSink crk : consentRecordSink) {
//
//				if (crk.getCommon_part().getSlr_id().equalsIgnoreCase(slr)
//						&& crk.getConsentStatusList().get(crk.getConsentStatusList().size() - 1).getConsent_status()
//								.toString().equalsIgnoreCase("ACTIVE")) {
//
//					ConsentRecordSource crs = dao.findSourceConsentRecordByRes_id(
//							crk.getCommon_part().getRs_description().getResource_set().getRs_id(), accountId);
//
//					if (crs.getCommon_part().getSubject_id().equalsIgnoreCase(serviceId)) {
//						consent.put("rs_id", crk.getCommon_part().getRs_description().getResource_set().getRs_id());
//						consent.put("account_id", accountId);
//						consent.put("sink", crk);
//					}
//				}
//			}
//
//			String array = new Gson().toJson(consent);
//
//			return Response.status(Response.Status.OK).entity(array).build();
//
//		} catch (Exception e) {
//			return ConsentServiceUtils.handleInternalServerErrorResponse(e);
//		}
//
//	}
//
//	/*
//	 * @Override
//	 * 
//	 * @GET
//	 * 
//	 * @Path("/consentStatus/{accountId}/{rsId}/statuses")
//	 * 
//	 * @Produces({ MediaType.APPLICATION_JSON }) public Response
//	 * getConsentStatusRecords(@PathParam("accountId") String
//	 * accountId, @PathParam("rsId") String rsId) {
//	 * 
//	 * // try { // // List<ConsentStatusRecord> statusRecords = //
//	 * null;//dao.getServiceLinkStatusRecords(accountId, slrId); // // return
//	 * Response.status(Response.Status.OK) //
//	 * .entity(DAOUtils.obj2Json(statusRecords, new //
//	 * TypeToken<ArrayList<ConsentStatusRecord>>() { // }.getType())).build(); // //
//	 * } catch (ConsentUtilsException e) { // // e.printStackTrace(); //
//	 * ErrorResponse error = new //
//	 * ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()), //
//	 * e.getClass().getSimpleName(), e.getMessage()); // // return //
//	 * Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
//	 * // // } catch (ConsentStatusRecordNotFoundException e) { //
//	 * System.out.println(e.getMessage()); // ErrorResponse error = new //
//	 * ErrorResponse(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()), //
//	 * e.getClass().getSimpleName(), e.getMessage()); // // return //
//	 * Response.status(Response.Status.NOT_FOUND).entity(error.toJson()).build(); //
//	 * // } catch (Exception e) { // e.printStackTrace(); // ErrorResponse error =
//	 * new ErrorResponse( //
//	 * String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()), //
//	 * e.getClass().getSimpleName(), // e.getMessage()); // // return //
//	 * Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error.toJson())
//	 * .build(); // } return null;
//	 * 
//	 * }
//	 */
//
//	/*
//	 * //dashboard //recupero l'array di tutti i sinkConsentRecords //scorro questo
//	 * array //per ogni elmento di questo array //stampo "Consenso" //recupero
//	 * l'slrid (del sink) dal sink consent record corrente //richiamo un'api che
//	 * ritorna tutto il servizio (sink) passandogli l'slrid e l'accountid //stampo
//	 * il nome del sink //recupero l'rs_id //richiamo un'api che ritorna un source
//	 * Consent record passandogli questo rs_id e l'accountId //dal source Consent
//	 * record appena ricavato prendo il source slrid //richiamo un'api che ritorna
//	 * tutto il servizio (source) passandogli l'slrid e l'accountid //stampo il nome
//	 * del source //stampo l'array degli stati (ad esempio prendendelo dal sink
//	 * consent record corrente //passo al prossimo sink consent record corrente
//	 */
//	/*
//	 * @Override
//	 * 
//	 * @GET
//	 * 
//	 * @Path("/testmongo/{accountId}/{sinkId}/{sourceId}/{datasetId}")
//	 * 
//	 * @Produces(MediaType.APPLICATION_JSON) public Response
//	 * testMongo(@PathParam("accountId") String accountId, @PathParam("sinkId")
//	 * String sinkId,
//	 * 
//	 * @PathParam("sourceId") String sourceId, @PathParam("datasetId") String
//	 * datasetId) { try { ConsentRecordSink crs =
//	 * dao.getConsentRecordSinkByDatasetId(accountId, datasetId); return
//	 * Response.status(Response.Status.OK) .entity(DAOUtils.obj2Json(crs,
//	 * ConsentRecordSink.class).toString()).build(); } catch (Exception e) { // TODO
//	 * Auto-generated catch block e.printStackTrace(); ErrorResponse error = new
//	 * ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
//	 * e.getClass().getSimpleName(), e.getMessage()); return
//	 * Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
//	 * } }
//	 * 
//	 * private void traceAuditLog(String accountId, JSONObject details) {
//	 * 
//	 * Client client = ClientBuilder.newClient(); WebTarget webTarget =
//	 * client.target("http://localhost:8080/auditlog-manager/api/v1/internal/addlog"
//	 * );
//	 * 
//	 * JSONObject payload = new JSONObject(); payload.put("username", accountId);
//	 * payload.put("type", "Authorization-management"); payload.put("objectJson",
//	 * details.toString());
//	 * 
//	 * Invocation.Builder invocationBuilder =
//	 * webTarget.request(MediaType.APPLICATION_JSON); Response response =
//	 * invocationBuilder.post(Entity.entity(payload.toString(),
//	 * MediaType.APPLICATION_JSON));
//	 * 
//	 * }
//	 */
//
//}
