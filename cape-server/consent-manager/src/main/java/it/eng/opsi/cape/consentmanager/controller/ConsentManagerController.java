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
package it.eng.opsi.cape.consentmanager.controller;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.eng.opsi.cape.consentmanager.ApplicationProperties;
import it.eng.opsi.cape.consentmanager.model.AuthorisationTokenPayload;
import it.eng.opsi.cape.consentmanager.model.AuthorisationTokenPayloadPopKid;
import it.eng.opsi.cape.consentmanager.model.AuthorisationTokenResponse;
import it.eng.opsi.cape.consentmanager.model.ChangeConsentStatusRequest;
import it.eng.opsi.cape.consentmanager.model.ChangeConsentStatusRequest.ChangeConsentStatusRequestFrom;
import it.eng.opsi.cape.consentmanager.model.CommonPart;
import it.eng.opsi.cape.consentmanager.model.ConsentForm;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordPayload;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordRoleEnum;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordSigned;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordSignedPair;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordSinkRoleSpecificPart;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordSourceRoleSpecificPart;
import it.eng.opsi.cape.consentmanager.model.ConsentRecordStatusEnum;
import it.eng.opsi.cape.consentmanager.model.ConsentStatusRecordPayload;
import it.eng.opsi.cape.consentmanager.model.ConsentStatusRecordSigned;
import it.eng.opsi.cape.consentmanager.model.Dataset;
import it.eng.opsi.cape.consentmanager.model.Policy;
import it.eng.opsi.cape.consentmanager.model.RSDescription;
import it.eng.opsi.cape.consentmanager.model.ResourceSet;
import it.eng.opsi.cape.consentmanager.model.SinkUsageRules;
import it.eng.opsi.cape.consentmanager.model.ThirdPartyReuseConsentSignResponse;
import it.eng.opsi.cape.consentmanager.model.WithinServiceConsentSignResponse;
import it.eng.opsi.cape.consentmanager.model.audit.ConsentActionType;
import it.eng.opsi.cape.consentmanager.model.audit.ConsentEventLog;
import it.eng.opsi.cape.consentmanager.model.audit.EventType;
import it.eng.opsi.cape.consentmanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.consentmanager.model.linking.ServiceLinkRecordPayload;
import it.eng.opsi.cape.consentmanager.model.linking.ServiceLinkStatusEnum;
import it.eng.opsi.cape.consentmanager.model.linking.ServiceLinkStatusRecordPayload;
import it.eng.opsi.cape.consentmanager.model.linking.ServicePopKey;
import it.eng.opsi.cape.consentmanager.repository.ConsentFormRepository;
import it.eng.opsi.cape.consentmanager.repository.ConsentRecordRepository;
import it.eng.opsi.cape.consentmanager.service.ClientService;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.ChangeConsentStatusException;
import it.eng.opsi.cape.exception.ConsentManagerException;
import it.eng.opsi.cape.exception.ConsentRecordNotFoundException;
import it.eng.opsi.cape.exception.ConsentStatusNotValidException;
import it.eng.opsi.cape.exception.DataMappingNotFoundException;
import it.eng.opsi.cape.exception.DatasetIdNotFoundException;
import it.eng.opsi.cape.exception.ResourceSetIdNotFoundException;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkStatusNotValidException;
import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.IsDescribedAt;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis;
import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;
import it.eng.opsi.cape.serviceregistry.data.ServiceInstance;
import lombok.extern.slf4j.Slf4j;

@OpenAPIDefinition(tags = {
		@Tag(name = "Consent Record", description = "Consent Manager APIs to query CaPe Consent Records."),
		@Tag(name = "Consenting", description = "Consent Manager APIs to perform CaPe Consenting operations.") }, info = @Info(title = "CaPe API - Consent Manager", description = "CaPe APIs used to manage CaPe Consent Form, Consent Records and consenting operations", version = "2.0"))
@RestController
@RequestMapping("/api/v2")
@Slf4j
public class ConsentManagerController implements IConsentManagerController {

	private final ApplicationProperties appProperty;
	private final String operatorId;

	private final String consentManagerHost;
	private long consentExpiration;

	@Autowired
	ConsentFormRepository consentFormRepo;

	@Autowired
	ConsentRecordRepository consentRecordRepo;

	@Autowired
	ClientService clientService;

	@Autowired
	public ConsentManagerController(ApplicationProperties appProperty) {
		this.appProperty = appProperty;
		this.operatorId = this.appProperty.getCape().getOperatorId();
		this.consentManagerHost = this.appProperty.getCape().getConsentManager().getHost();
		this.consentExpiration = this.appProperty.getCape().getAuthToken().getExp();
	}

	@Operation(summary = "Generate Consent Form for input Surrogate Id, ServiceId Id, Purpose Id and optionally sourceDatasetId and sourceServiceId (in 3rd party consenting case)", description = "PurposeId must match with one of the purposes present in the (Sink) Service Description. In case of 3rd party consenting, sourceDatasetId and sourceServiceId parameters must be both present and serviceId parameter represents the Sink Service Id. The fetched Consent Form will be used in the Give Consent API.", tags = {
			"Consenting" }, responses = {
					@ApiResponse(description = "Returns the generated Consent Form containing the Resource Set generated either by matching Sink and Source datasets' data mappings/concepts (3-party consenting case) or directly from the datasets required by the selected Purpose of the service (Within Service case).", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsentForm.class))) })
	@GetMapping(value = "/users/{surrogateId}/service/{serviceId}/purpose/{purposeId}/consentForm", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ConsentForm> fetchConsentFormFromService(@PathVariable String surrogateId,
			@PathVariable String serviceId, @PathVariable String purposeId,
			@RequestParam(required = false) String sourceDatasetId,
			@RequestParam(required = false) String sourceServiceId) throws ConsentManagerException,
			ServiceLinkRecordNotFoundException, ServiceDescriptionNotFoundException, DataMappingNotFoundException,
			DatasetIdNotFoundException, AccountNotFoundException, ServiceLinkStatusNotValidException {

		ConsentForm consentForm = null;
		List<Dataset> resourceSetDatasets = null;

		/*
		 * If SourceId is present the Consenting is supposed to be between a Sink and a
		 * Source (Third Party Re-use case).
		 */

		/*******************************************************************
		 * *** 1. Consenting Case (within Service) ****
		 * 
		 * 
		 *******************************************************************/
		if (StringUtils.isBlank(sourceServiceId) && StringUtils.isBlank(sourceDatasetId)) {

			/*
			 * Check if there is an active Service Link Record for the Service and Account
			 * corresponding to the input SurrogateId
			 */
			ServiceLinkRecordDoubleSigned existingSlr = clientService
					.callGetServiceLinkRecordBySurrogateIdAndServiceId(surrogateId, serviceId);
			ServiceLinkStatusRecordPayload existingSsr = existingSlr.getServiceLinkStatuses()
					.get(existingSlr.getServiceLinkStatuses().size() - 1).getPayload();
			if (!existingSsr.getServiceLinkStatus().equals(ServiceLinkStatusEnum.ACTIVE))
				throw new ServiceLinkStatusNotValidException(
						"The Service Link Record found for Surrogate Id: " + surrogateId + " and Service Id: "
								+ serviceId + " is in Removed state, try to active it first");

			/*
			 * TODO Check if there is an already Active Consent Record throw new
			 * ConsentAlreadyPresentException();
			 */

			// Get Service Description from Service Registry
			ServiceEntry serviceDescription = clientService.getServiceDescriptionFromRegistry(serviceId);

			// In the within Service case, the Resource Set Dataset is directly the ones
			// required by the Purpose
			resourceSetDatasets = getWithinServiceDatasets(purposeId, serviceDescription);

			/********************************************************************
			 * Check if there is an existing Consent Form for the input SurrogateId -
			 * ServiceId - PurposeId for which a Consent Record has been given
			 ********************************************************************/
			List<ConsentForm> existingConsentForms = consentFormRepo
					.findBySurrogateIdAndSinkIdAndSourceIdAndUsageRules_purposeId(surrogateId, serviceId,
							sourceServiceId, purposeId);

			for (ConsentForm existingConsentForm : existingConsentForms) {
				Optional<ConsentRecordSigned[]> crPair = consentRecordRepo
						.findByPayload_commonPart_rsDescription_resourceSet_rsId(
								existingConsentForm.getResourceSet().getRsId());

				if (!crPair.isPresent())
					return ResponseEntity.ok(existingConsentForm);
			}

			/*
			 * Otherwise generate a new Resource Set Id (serviceDomain:uniqueResourceKey)
			 */
			String rsId = serviceDescription.getServiceInstance().getServiceUrls().getDomain() + ":"
					+ new ObjectId().toString();
			ResourceSet proposedResourceSet = new ResourceSet(rsId, resourceSetDatasets);

			/*
			 * Create the Sink Usage Rule
			 * 
			 */
			ProcessingBasis servicePurpose = serviceDescription.getProcessingBases().stream()
					.filter(basis -> basis.getPurposeId().equals(purposeId)).findFirst().get();

			SinkUsageRules serviceUsageRule = new SinkUsageRules(purposeId, servicePurpose.getPurposeCategory(),
					servicePurpose.getDescription(), servicePurpose.getLegalBasis(), servicePurpose.getPurposeName(),
					servicePurpose.getProcessingCategories(), new Policy(servicePurpose.getPolicyRef(), "", ""),
					servicePurpose.getStorage(), servicePurpose.getRecipients(), servicePurpose.getShareWith(),
					servicePurpose.getObligations(), servicePurpose.getCollectionMethod(),
					servicePurpose.getTermination());

			/******************************************************************************
			 * Create the final Consent Form to be returned
			 *******************************************************************************/
			ServiceInstance serviceInstance = serviceDescription.getServiceInstance();

			consentForm = new ConsentForm(rsId, surrogateId, serviceId, serviceDescription.getName(),
					serviceDescription.getHumanReadableDescription(), proposedResourceSet, serviceUsageRule,
					serviceInstance.getServiceProvider().getJurisdiction(), serviceInstance.getDataController(),
					serviceDescription.getServiceDescriptionVersion(),
					serviceDescription.getServiceDescriptionSignature(),
					serviceDescription.getServiceInstance().getServiceProvider().getBusinessId());

		} else

		{

			/********************************************************************
			 * *** 2. Third Party Re-use Case (Sink-Source Services) ****
			 * 
			 * The input serviceId represents the sinkId
			 * 
			 * The input datasetId represents the Source dataset, to which Data concepts
			 * contained in the purpose required dataset must match.
			 * 
			 *******************************************************************/

			/*******************************************************************
			 * Check if there is a Service Link Record for the Services (Sink and Source)
			 * and the Account corresponding to the input SurrogateId
			 *
			 *******************************************************************/
			ServiceLinkRecordDoubleSigned existingSinkSlr = clientService
					.callGetServiceLinkRecordBySurrogateIdAndServiceId(surrogateId, serviceId);
			ServiceLinkStatusRecordPayload existingSinkSsr = existingSinkSlr.getServiceLinkStatuses()
					.get(existingSinkSlr.getServiceLinkStatuses().size() - 1).getPayload();
			if (!existingSinkSsr.getServiceLinkStatus().equals(ServiceLinkStatusEnum.ACTIVE))
				throw new ServiceLinkRecordNotFoundException("No active Service Link Record found for Surrogate Id: "
						+ surrogateId + "and Service Id: " + serviceId);

			/********************************************************************
			 * Get from Account Manager the accountId corresponding to the input Surrogate
			 * Id Used to get the Source Slr by accountId (we have in input only the
			 * Surrogate Id of the Sink service) and then to retrieve the Source Surrogate
			 * Id
			 ********************************************************************/
			String sinkSlrId = existingSinkSlr.getPayload().getSlrId();
			String accountId = clientService.callGetAccountIdFromSlrIdAndSurrogateId(sinkSlrId, surrogateId);

			ServiceLinkRecordDoubleSigned existingSourceSlr = clientService
					.callGetServiceLinkRecordByAccountIdAndServiceId(accountId, sourceServiceId);
			ServiceLinkStatusRecordPayload existingSourceSsr = existingSourceSlr.getServiceLinkStatuses()
					.get(existingSourceSlr.getServiceLinkStatuses().size() - 1).getPayload();

			if (!existingSourceSsr.getServiceLinkStatus().equals(ServiceLinkStatusEnum.ACTIVE))
				throw new ServiceLinkRecordNotFoundException("No active Service Link Record found for Account Id: "
						+ accountId + "and Source Service Id: " + serviceId);

			String sourceSurrogateId = existingSourceSlr.getPayload().getSurrogateId();

			/*
			 * TODO Check if there is an already Active Consent Record throw new
			 * ConsentAlreadyPresentException(); ???
			 */

			/********************************************************************
			 * 
			 * 
			 * Create the Proposed Resource Set by matching the Data Mappings. Starting from
			 * the input Sink Purpose Id, get the required datasets. For each sink required
			 * dataset for the purpose, match its data mapping fields (by the conceptId) to
			 * the ones coming from the Source Dataset (sourceDatasetId in input)
			 * 
			 **********************************************************************************/

			ServiceEntry sinkDescription = clientService.getServiceDescriptionFromRegistry(serviceId);
			ServiceEntry sourceDescription = clientService.getServiceDescriptionFromRegistry(sourceServiceId);

			resourceSetDatasets = getThirdPartyReuseMatchedDatasets(purposeId, sourceDatasetId, sinkDescription,
					sourceDescription);

			/*
			 * Check if there is an existing Consent Form for the input SurrogateId -
			 * ServiceId - PurposeId for which a Consent Record has been given
			 */
			List<ConsentForm> existingConsentForms = consentFormRepo
					.findBySurrogateIdAndSinkIdAndSourceIdAndUsageRules_purposeId(surrogateId, serviceId,
							sourceServiceId, purposeId);

			for (ConsentForm existingConsentForm : existingConsentForms) {
				Optional<ConsentRecordSigned[]> crPair = consentRecordRepo
						.findByPayload_commonPart_rsDescription_resourceSet_rsId(
								existingConsentForm.getResourceSet().getRsId());

				if (!crPair.isPresent())
					return ResponseEntity.ok(existingConsentForm);
			}

			/*
			 * Otherwise generate a new Resource Set Id (serviceUri:uniqueResourceKey)
			 */
			String rsId = sourceDescription.getServiceInstance().getServiceUrls().getDomain() + ":"
					+ new ObjectId().toString();
			ResourceSet proposedResourceSet = new ResourceSet(rsId, resourceSetDatasets);

			/*
			 * Create the Sink Usage Rule
			 * 
			 */
			ProcessingBasis sinkPurpose = sinkDescription.getProcessingBases().stream()
					.filter(basis -> basis.getPurposeId().equals(purposeId)).findFirst().get();

			SinkUsageRules sinkUsageRule = new SinkUsageRules(purposeId, sinkPurpose.getPurposeCategory(),
					sinkPurpose.getDescription(), sinkPurpose.getLegalBasis(), sinkPurpose.getPurposeName(),
					sinkPurpose.getProcessingCategories(), new Policy(sinkPurpose.getPolicyRef(), "", ""),
					sinkPurpose.getStorage(), sinkPurpose.getRecipients(), sinkPurpose.getShareWith(),
					sinkPurpose.getObligations(), sinkPurpose.getCollectionMethod(), sinkPurpose.getTermination());

			/*******************************************************************************
			 *
			 * Create the final Consent Form to be returned
			 *******************************************************************************/
			ServiceInstance sinkServiceInstance = sinkDescription.getServiceInstance();

			consentForm = new ConsentForm(rsId, surrogateId, sourceSurrogateId, sourceServiceId, serviceId,
					sourceDescription.getName(), sinkDescription.getName(),
					sourceDescription.getServiceInstance().getServiceUrls().getLibraryDomain(),
					sinkDescription.getServiceInstance().getServiceUrls().getLibraryDomain(),
					sourceDescription.getHumanReadableDescription(), sinkDescription.getHumanReadableDescription(),
					proposedResourceSet, sinkUsageRule, sinkServiceInstance.getServiceProvider().getJurisdiction(),
					sinkServiceInstance.getDataController(), sinkDescription.getServiceDescriptionVersion(),
					sinkDescription.getServiceDescriptionSignature(),
					sinkDescription.getServiceInstance().getServiceProvider().getBusinessId());

		}

		/*
		 * Save Consent Form in DB ( To check its RsId when giving consent and to expose
		 * it as consent_proposal_url =
		 */
		consentFormRepo.save(consentForm);

		return ResponseEntity.ok(consentForm);

	}

	@Operation(summary = "Get the list of Data Consent Form for surrogateId, sinkId and sourceId (if any) in input.", tags = {
			"Consenting" }, responses = {
					@ApiResponse(description = "Returns the generated Consent Form containing the Resource Set generated either by matching Sink and Source datasets' mappings (3-party reuse) or from the datasets of the service (Within Service)", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsentForm.class))) })
	@GetMapping(value = "/services/{serviceId}/purposes/{purposeId}/matchingDatasets", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<DataMapping>> getMatchingDatasets(@PathVariable String serviceId,
			@PathVariable String purposeId, @RequestParam(required = false) String sourceDatasetId,
			@RequestParam(required = false) String sourceServiceId) throws DataMappingNotFoundException,
			DatasetIdNotFoundException, ConsentManagerException, ServiceDescriptionNotFoundException {

		Dataset result;
		// Get Service Description from Service Registry
		ServiceEntry serviceDescription = clientService.getServiceDescriptionFromRegistry(serviceId);

		/*
		 * *** 2. Third Party Re-use Case (Sink-Source Services) ****
		 * 
		 * The input serviceId represents the sinkId
		 * 
		 * The input datasetId represents the Source dataset, to which Data concepts
		 * contained in the required dataset of the purpose must match.
		 * 
		 *****************************************************************/
		if (StringUtils.isNotBlank(sourceServiceId)) {

			// Get Source Service Description from Service Registry
			ServiceEntry sourceServiceDescription = clientService.getServiceDescriptionFromRegistry(sourceServiceId);
			result = getThirdPartyReuseMatchedDatasets(purposeId, sourceDatasetId, serviceDescription,
					sourceServiceDescription).get(0);

			/*
			 * *** Consenting Case (within Service) ****
			 * 
			 * 
			 **********************************************/
		} else {

			result = getWithinServiceDatasets(purposeId, serviceDescription).get(0);
		}

		return ResponseEntity.ok(result.getDataMappings());
	}

	private List<Dataset> getWithinServiceDatasets(String purposeId, ServiceEntry service)
			throws DataMappingNotFoundException {

		List<Dataset> resourceSetDatasets;
		List<String> requiredSinkDatasetIds;
		String matchingPurposeName;

		try {
			ProcessingBasis matchingBasis = service.getProcessingBases().stream()
					.filter(base -> base.getPurposeId().equals(purposeId)).findFirst().get();
			requiredSinkDatasetIds = matchingBasis.getRequiredDatasets();
			matchingPurposeName = matchingBasis.getPurposeName();

		} catch (NoSuchElementException e) {
			throw new DataMappingNotFoundException("The required purpose Id: " + purposeId
					+ " was not found in the processing bases description of the sink with ID: "
					+ service.getServiceId());
		}

		Map<String, IsDescribedAt> sinkDatasetMap = service.getIsDescribedAt().stream()
				.collect(Collectors.toMap(IsDescribedAt::getDatasetId, Function.identity()));

		resourceSetDatasets = new ArrayList<Dataset>();
		requiredSinkDatasetIds.forEach(
				sinkDatasetId -> resourceSetDatasets.add(new Dataset(sinkDatasetId, purposeId, matchingPurposeName,
						sinkDatasetMap.get(sinkDatasetId).getDataMapping(), ZonedDateTime.now(ZoneId.of("UTC")))));

		return resourceSetDatasets;

	}

	private List<Dataset> getThirdPartyReuseMatchedDatasets(String purposeId, String sourceDatasetId, ServiceEntry sink,
			ServiceEntry source) throws DataMappingNotFoundException, DatasetIdNotFoundException {

		List<DataMapping> matchingDataMapping = new ArrayList<DataMapping>();
		List<String> requiredSinkDatasetIds = null;
		String matchingPurposeName;
		/* Get the datasets required by the input Purpose Id */
		try {
			ProcessingBasis matchingBasis = sink.getProcessingBases().stream()
					.filter(base -> base.getPurposeId().equals(purposeId)).findFirst().get();
			requiredSinkDatasetIds = matchingBasis.getRequiredDatasets();
			matchingPurposeName = matchingBasis.getPurposeName();

		} catch (NoSuchElementException e) {
			throw new DataMappingNotFoundException("The required purpose Id: " + purposeId
					+ " was not found in the processing bases description of the sink with ID: " + sink.getServiceId());
		}

		Map<String, IsDescribedAt> sinkDatasetMap = sink.getIsDescribedAt().stream()
				.collect(Collectors.toMap(IsDescribedAt::getDatasetId, Function.identity()));

		/* Get the sourceDataset according to the input sourceDatasetId */
		IsDescribedAt sourceDataset = null;
		try {
			sourceDataset = source.getIsDescribedAt().stream().filter(d -> d.getDatasetId().equals(sourceDatasetId))
					.findFirst().get();
		} catch (NoSuchElementException e) {
			throw new DataMappingNotFoundException("The required source Dataset Id: " + sourceDatasetId
					+ " was not found in the description of the Source Service with Id: " + source.getServiceId());
		}

		/* Put all the source DataMapping for the source dataset in a map */
		Map<String, DataMapping> sourceDataMappingMap = sourceDataset.getDataMapping().stream()
				.collect(Collectors.toMap(DataMapping::getConceptId, Function.identity()));

		/*
		 ** Collect all the data mapping fields coming from each dataset required for the
		 ** purpose and matching with the ones contained in the source dataset
		 */

		for (String sinkDatasetId : requiredSinkDatasetIds) {
			IsDescribedAt sinkDataset = sinkDatasetMap.get(sinkDatasetId);

			if (sinkDataset == null)
				throw new DatasetIdNotFoundException("The Sink Dataset Id found in the purpose with id: " + purposeId
						+ " does not match with any of the sink dataset");

			for (DataMapping sinkDataMapping : sinkDataset.getDataMapping()) {

				if (sourceDataMappingMap.containsKey(sinkDataMapping.getConceptId())) {
					matchingDataMapping.add(sinkDataMapping);
				} else if (sinkDataMapping.getRequired())
					throw new DataMappingNotFoundException(
							"The required sink Concept: " + sinkDataMapping.getConceptId() + " for the purpose Id: "
									+ purposeId + " was not found in the source dataset with Id: " + sourceDatasetId);
			}

		}

		return Arrays.asList(new Dataset(sourceDatasetId, purposeId, matchingPurposeName, matchingDataMapping,
				ZonedDateTime.now(ZoneId.of("UTC"))));

	}

	@Operation(summary = "Give the Consent for the input ConsentForm", description = "Return the newly generated Consent Record signed with the Acccount private key.", tags = {
			"Consenting" }, responses = {
					@ApiResponse(description = "Returns 201 Created with the created Consent Record Signed.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsentRecordSigned.class))) })
	@PostMapping(value = "/users/{surrogateId}/consents", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ConsentRecordSigned> giveConsentFromService(@PathVariable String surrogateId,
			@RequestBody @Valid ConsentForm consentForm)
			throws ResourceSetIdNotFoundException, ConsentManagerException, ServiceLinkRecordNotFoundException,
			AccountNotFoundException, ConsentRecordNotFoundException, ConsentStatusNotValidException,
			ServiceLinkStatusNotValidException, ServiceDescriptionNotFoundException, ChangeConsentStatusException {

		/*
		 * Check Resource Id of the input Consent Form (is its Id)
		 */
		consentFormRepo.findById(consentForm.getResourceSet().getRsId())
				.orElseThrow(() -> new ResourceSetIdNotFoundException(
						"The Resource Set Id in the input Consent Forms was not found"));

		/*****************************************************
		 * If there is a sourceId, we are in the 3rd-party consenting case, then the
		 * serviceId is the Sink Id and surrogateId is the one of Sink Service's User
		 ******************************************************/
		String serviceId = consentForm.getSinkId();
		String sourceId = consentForm.getSourceId();

		ResourceSet proposedResourceSet = consentForm.getResourceSet();
		Dataset proposedDataset = proposedResourceSet.getDatasets().get(0);
		String datasetId = proposedDataset.getId();
		SinkUsageRules proposedUsageRules = consentForm.getUsageRules();
		DataMapping[] proposedConcepts = proposedDataset.getDataMappings().stream().toArray(DataMapping[]::new);

		/******************************************************
		 * Check if there is an active Service Link Record for the Service and Account
		 * corresponding to the input SurrogateId
		 ******************************************************/
		ServiceLinkRecordDoubleSigned existingSlr = clientService
				.callGetServiceLinkRecordBySurrogateIdAndServiceId(surrogateId, serviceId);
		ServiceLinkRecordPayload existingSlrPayload = existingSlr.getPayload();
		String slrId = existingSlr.getPayload().getSlrId();
		ServiceLinkStatusRecordPayload existingSsr = existingSlr.getServiceLinkStatuses()
				.get(existingSlr.getServiceLinkStatuses().size() - 1).getPayload();

		if (!existingSsr.getServiceLinkStatus().equals(ServiceLinkStatusEnum.ACTIVE))
			throw new ServiceLinkRecordNotFoundException("No active Service Link Record found for Surrogate Id: "
					+ surrogateId + "and Service Id: " + serviceId);

		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
		String accountId = clientService.callGetAccountIdFromSlrIdAndSurrogateId(slrId, surrogateId);

		/***********************************************************************************
		 * Check if there is an already Active Consent Record for (Sink) Service and
		 * selected purposeId (take it from one of Resource Set's dataset).
		 * 
		 * In case return the existing Consent Record
		 **********************************************************************************/

		List<ConsentRecordSigned> existingCrList = consentRecordRepo
				.findByPayload_commonPart_surrogateIdAndPayload_commonPart_rsDescription_resourceSet_datasets_purposeIdOrderByPayload_commonPart_iatDesc(
						surrogateId, proposedResourceSet.getDatasets().stream().findFirst().get().getPurposeId());

		if (!existingCrList.isEmpty()) {

			ConsentRecordSigned existingCr = existingCrList.get(0);

			if (!existingCr.isWithdrawn()) {

				/************************************
				 * If The existing Consent Record is disabled, activate it and vice-versa, Also
				 * updating Resource Set and Usage Rules with the proposed ones in input
				 ************************************/
				proposedResourceSet
						.setRsId(existingCr.getPayload().getCommonPart().getRsDescription().getResourceSet().getRsId());

				existingCr = this.changeConsentStatus(surrogateId, slrId, existingCr.get_id().toString(),
						new ChangeConsentStatusRequest(
								existingCr.isDisabled() ? ConsentRecordStatusEnum.Active
										: ConsentRecordStatusEnum.Disabled,
								proposedResourceSet, proposedUsageRules, ChangeConsentStatusRequestFrom.SERVICE))
						.getBody();

				return ResponseEntity.ok(existingCr);
			}
		}

		/******************************************************
		 * Start creating Consent Record and Consent Status Record for Service/Sink
		 ******************************************************/
		String consentRecordId = new ObjectId().toString();
		String consentStatusRecordId = new ObjectId().toString();
		RSDescription resourceSetDescription = new RSDescription(proposedResourceSet);

		/******************************************************
		 * *** 1. Consenting Case (within Service) ****
		 * 
		 * 
		 **********************************************/
		if (StringUtils.isBlank(sourceId)) {

			/*
			 * The Resource Set id is used as Consent Pair Id (if needed).
			 */
			CommonPart consentCommonPart = new CommonPart(appProperty.getCape().getVersion(), consentRecordId,
					existingSlrPayload.getSurrogateId(), resourceSetDescription, slrId, now, now, operatorId, serviceId,
					consentForm.getSinkName(), consentForm.getSinkHumanReadableDescriptions(), serviceId,
					consentForm.getSinkName(), consentForm.getSinkHumanReadableDescriptions(),
					consentForm.getJurisdiction(), consentForm.getDataController(),
					consentForm.getServiceDescriptionVersion(), consentForm.getServiceDescriptionSignature(),
					consentForm.getServiceProviderBusinessId(), ConsentRecordRoleEnum.SINK,
					ConsentRecordStatusEnum.Active);

			ConsentRecordSinkRoleSpecificPart sinkRolePart = new ConsentRecordSinkRoleSpecificPart(proposedUsageRules);
			ConsentRecordPayload crPayload = new ConsentRecordPayload(consentCommonPart, sinkRolePart, null);

			ConsentStatusRecordPayload csrPayload = new ConsentStatusRecordPayload(consentStatusRecordId,
					appProperty.getCape().getVersion(), surrogateId, consentRecordId, ConsentRecordStatusEnum.Active,
					proposedResourceSet, proposedUsageRules, now, "none");

			/*
			 * Call Account Manager to Sign Consent Record and Consent Status Record
			 */
			WithinServiceConsentSignResponse consentSignResponse = clientService.callSignWithinServiceConsent(
					surrogateId, slrId, crPayload, csrPayload, new ArrayList<ConsentStatusRecordSigned>());
			ConsentRecordSigned signedCr = consentSignResponse.getSignedCr();
			ConsentStatusRecordSigned signedCsr = consentSignResponse.getSignedCsr();

			/*
			 * Store Signed CR and CSR
			 */
			signedCr.setAccountId(accountId);
			signedCr.set_id(new ObjectId(signedCr.getPayload().getCommonPart().getCrId()));

			// TODO Send CR to Service instead of returning in the API?
			// Set notification to SDK Successful
//			signedCr.setSentToSdkSuccessfully(true);
			consentRecordRepo.insert(signedCr);

			// Audit for give consent
			clientService.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, accountId, LegalBasis.CONSENT,
					"Consent given", proposedUsageRules, serviceId, serviceId, proposedConcepts, null,
					ConsentActionType.GIVE, null, consentRecordId));

			// Audit of consent sending
			clientService.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, accountId, LegalBasis.CONSENT,
					"Consent sent", proposedUsageRules, serviceId, serviceId, proposedConcepts, null,
					ConsentActionType.SEND, null, consentRecordId));

			return ResponseEntity.created(UriComponentsBuilder
					.fromHttpUrl(consentManagerHost + "/api/v2/accounts/{accountId}/consents/{consentRecordId}")
					.build(accountId, consentRecordId)).body(signedCr);

		} else {
			/******************************************************
			 * *** 2. Third Party Re-use Case (Sink-Source Services) ****
			 * 
			 * The input serviceId represents the sinkId
			 * 
			 * The input datasetId represents the Source dataset, to which Data concepts
			 * contained in the required dataset of the purpose must match.
			 * 
			 *****************************************************************/

			/*
			 * The Resource Set id is used as Consent Pair Id (if needed)
			 */
			CommonPart consentCommonPart = new CommonPart(appProperty.getCape().getVersion(), consentRecordId,
					existingSlrPayload.getSurrogateId(), resourceSetDescription, slrId, now, now, operatorId, serviceId,
					consentForm.getSinkName(), consentForm.getSinkHumanReadableDescriptions(), sourceId,
					consentForm.getSourceName(), consentForm.getSourceHumanReadableDescriptions(),
					consentForm.getJurisdiction(), consentForm.getDataController(),
					consentForm.getServiceDescriptionVersion(), consentForm.getServiceDescriptionSignature(),
					consentForm.getServiceProviderBusinessId(), ConsentRecordRoleEnum.SINK,
					ConsentRecordStatusEnum.Active);

			ConsentRecordSinkRoleSpecificPart sinkRolePart = new ConsentRecordSinkRoleSpecificPart(proposedUsageRules);
			ConsentRecordPayload crPayload = new ConsentRecordPayload(consentCommonPart, sinkRolePart, null);

			ConsentStatusRecordPayload csrPayload = new ConsentStatusRecordPayload(consentStatusRecordId,
					appProperty.getCape().getVersion(), surrogateId, consentRecordId, ConsentRecordStatusEnum.Active,
					proposedResourceSet, proposedUsageRules, now, "none");

			/*
			 * Check if there is an active Service Link Record for the Service and Account
			 * corresponding to the Source SurrogateId present in the Consent Form
			 */

			String sourceSurrogateId = consentForm.getSourceSurrogateId();
			ServiceLinkRecordDoubleSigned sourceSlr = clientService
					.callGetServiceLinkRecordBySurrogateIdAndServiceId(sourceSurrogateId, sourceId);
			ServiceLinkRecordPayload sourceSlrPayload = sourceSlr.getPayload();
			String sourceSlrId = sourceSlrPayload.getSlrId();
			ServiceLinkStatusRecordPayload sourceSsr = sourceSlr.getServiceLinkStatuses()
					.get(sourceSlr.getServiceLinkStatuses().size() - 1).getPayload();

			if (!sourceSsr.getServiceLinkStatus().equals(ServiceLinkStatusEnum.ACTIVE))
				throw new ServiceLinkRecordNotFoundException("No active Service Link Record found for Surrogate Id: "
						+ surrogateId + "and Service Id: " + sourceId);

			/******************************************************
			 * Start creating Consent Record and Consent Status Record for Source Service
			 ******************************************************/

			String sourceConsentRecordId = new ObjectId().toString();
			String sourceConsentStatusRecordId = new ObjectId().toString();

			CommonPart sourceConsentCommonPart = new CommonPart(appProperty.getCape().getVersion(),
					sourceConsentRecordId, sourceSurrogateId, resourceSetDescription, sourceSlrId, now, now, operatorId,
					serviceId, consentForm.getSinkName(), consentForm.getSinkHumanReadableDescriptions(), sourceId,
					consentForm.getSourceName(), consentForm.getSourceHumanReadableDescriptions(),
					consentForm.getJurisdiction(), consentForm.getDataController(),
					consentForm.getServiceDescriptionVersion(), consentForm.getServiceDescriptionSignature(),
					consentForm.getServiceProviderBusinessId(), ConsentRecordRoleEnum.SOURCE,
					ConsentRecordStatusEnum.Active);

			/*
			 * Set the Sink and Source Cr Pair ids with the Resource Set Id, this will
			 * identify the Consent Records as part of a Cr pair being a third party reuse
			 * case
			 */
			consentCommonPart.setCrPairId(proposedResourceSet.getRsId());
			sourceConsentCommonPart.setCrPairId(proposedResourceSet.getRsId());

			ConsentRecordSourceRoleSpecificPart sourceRolePart = new ConsentRecordSourceRoleSpecificPart(
					existingSlrPayload.getPopKey(), sourceSlrPayload.getOperatorKey());
			ConsentRecordPayload sourceCrPayload = new ConsentRecordPayload(sourceConsentCommonPart, sourceRolePart,
					null);

			ConsentStatusRecordPayload sourceCsrPayload = new ConsentStatusRecordPayload(sourceConsentStatusRecordId,
					appProperty.getCape().getVersion(), sourceSlrPayload.getSurrogateId(), sourceConsentRecordId,
					ConsentRecordStatusEnum.Active, proposedResourceSet, proposedUsageRules, now, "none");

			/*
			 * Set the Source Consent Record Id in the Role Specific Part of the Sink
			 * Consent Record
			 */

			sinkRolePart.setSourceCrId(sourceConsentRecordId);

			/******************************************************
			 * Call Account Manager to Sign Sink and Source Consent Record and Consent
			 * Status Record
			 ******************************************************/

			ThirdPartyReuseConsentSignResponse consentSignResponse = clientService.callSignThirdPartyReuseConsent(
					surrogateId, slrId, sourceSlrPayload.getSlrId(), crPayload, csrPayload, sourceCrPayload,
					sourceCsrPayload, new ArrayList<ConsentStatusRecordSigned>(),
					new ArrayList<ConsentStatusRecordSigned>());

			ConsentRecordSigned signedSinkCr = consentSignResponse.getSink().getSignedCr();
			ConsentStatusRecordSigned signedSinkCsr = consentSignResponse.getSink().getSignedCsr();

			ConsentRecordSigned signedSourceCr = consentSignResponse.getSource().getSignedCr();
			ConsentStatusRecordSigned signedSourceCsr = consentSignResponse.getSource().getSignedCsr();

			/*
			 * Store Signed CRs (relative CSRs has been added into their own
			 * ConsentStatusList by the Account Manager in the signThirdPartyReuseConsent
			 */
			signedSinkCr.setAccountId(accountId);
			signedSinkCr.set_id(new ObjectId(signedSinkCr.getPayload().getCommonPart().getCrId()));
			consentRecordRepo.insert(signedSinkCr);

			signedSourceCr.setAccountId(accountId);
			signedSourceCr.set_id(new ObjectId(signedSourceCr.getPayload().getCommonPart().getCrId()));
			consentRecordRepo.insert(signedSourceCr);

			/******************************************************
			 * Send the signed Consent Record (with CSR) to the Source Service (Sink will
			 * receive its with the response of this API)
			 ******************************************************/
			try {
				clientService.sendConsentRecordToService(consentForm.getSourceLibraryDomainUrl(), signedSourceCr);

				// TODO Send CR also to Sink Service instead of returning in the API?

				// Set notification to SDK Successful
//				signedSinkCr.setSentToSdkSuccessfully(true);
//				signedSourceCr.setSentToSdkSuccessfully(true);

				consentRecordRepo.insert(signedSinkCr);
				consentRecordRepo.insert(signedSourceCr);
			} catch (Exception e) {

				throw new ConsentManagerException(
						"There was an error while sending the Consent Record to the Source Service: " + e.getMessage());
			}
			/******************************************************
			 * Auditing
			 ******************************************************/

			// Audit for give consent
			clientService.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, accountId, LegalBasis.CONSENT,
					"Consent given", proposedUsageRules, serviceId, sourceId, proposedConcepts, null,
					ConsentActionType.GIVE, null, sourceConsentRecordId));

			// Audit of consent sending
			clientService.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, accountId, LegalBasis.CONSENT,
					"Consent sent", proposedUsageRules, serviceId, sourceId, proposedConcepts, null,
					ConsentActionType.SEND, null, sourceConsentRecordId));

			/******************************************************
			 * Return the Sink Consent Records to the caller (supposed to be the Sink from
			 * which Consenting started)
			 ******************************************************/
			return ResponseEntity.created(UriComponentsBuilder
					.fromHttpUrl(consentManagerHost + "/api/v2/accounts/{accountId}/consents/{consentRecordId}")
					.build(accountId, consentRecordId)).body(signedSinkCr);
		}

	}

	@Operation(summary = "Get the list of signed Consent Records for the input Account Id. Optionally can be filtered by ConsentId, ServiceId, Source Service Id, Dataset Id, Consent Status, Purpose Id, Name or Category and Processing Category.", description = "Results can be sorted by the value of the iat timestamp of Consent Record (DESC by default).", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records belonging to the input Account Id.", responseCode = "200") })
	@GetMapping(value = "/accounts/{accountId}/consents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByAccountIdAndQuery(
			@PathVariable String accountId, @RequestParam(required = false) String consentId,
			@RequestParam(required = false) String serviceId, @RequestParam(required = false) String sourceServiceId,
			@RequestParam(required = false) String datasetId,
			@RequestParam(required = false) ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) List<ProcessingCategory> processingCategories,
			@RequestParam(defaultValue = "DESC") Sort.Direction iatSort) {

		List<ConsentRecordSigned> result = null;

		if (StringUtils.isNotBlank(consentId) || StringUtils.isNotBlank(serviceId)
				|| StringUtils.isNotBlank(sourceServiceId) || StringUtils.isNotBlank(datasetId) || status != null
				|| purposeCategory != null || processingCategories != null || iatSort != null)
			result = consentRecordRepo.findByAccountIdAndQuery(accountId, consentId, serviceId, sourceServiceId,
					datasetId, status, purposeId, purposeName, purposeCategory, processingCategories, iatSort);
		else
			result = consentRecordRepo.findByAccountId(accountId);

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Get the signed Consent Record for the input Account Id and Consent Record Id.", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the matching signed Consent Record belonging to the input Account Id and with input CrId.", responseCode = "200") })
	@GetMapping(value = "/accounts/{accountId}/consents/{consentId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ConsentRecordSigned> getConsentRecordByAccountIdAndCrId(@PathVariable String accountId,
			@PathVariable String consentId) throws ConsentRecordNotFoundException {

		return ResponseEntity.ok(consentRecordRepo.findByAccountIdAndPayload_commonPart_crId(accountId, consentId)
				.orElseThrow(() -> new ConsentRecordNotFoundException("No Consent Record found for Account Id: "
						+ accountId + " and Consent Record Id: " + consentId)));
	}

	@Operation(summary = "Get the list of signed Consent Records for the input Account Id and Service Link Record Id. In 3rd party consenting case, the input Slr Id can be relative to a linked service acting in the consent either as sink or source.", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records belonging to the input Account Id and with input Slr Id.", responseCode = "200") })
	@GetMapping(value = "/accounts/{accountId}/servicelinks/{slrId}/consents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByAccountIdAndSlrId(
			@PathVariable String accountId, @PathVariable String slrId) {

		return ResponseEntity.ok(consentRecordRepo.findByAccountIdAndPayload_commonPart_slrId(accountId, slrId));
	}

	@Operation(summary = "Get the list of signed Consent Record pairs (sink, source) for the input Account Id. Optionally can be filtered by ConsentId, ServiceId, Source Service Id, Dataset Id, Consent Status, Purpose Id, Name or Category and Processing Category.", description = "Results can be sorted by the value of the iat timestamp of Consent Record (DESC by default).", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of pairs of signed Consent Records belonging to the input Account Id.", responseCode = "200") })

	@GetMapping(value = "/accounts/{accountId}/consents/pair", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsByAccountIdAndQuery(
			@PathVariable String accountId, @RequestParam(required = false) String consentId,
			@RequestParam(required = false) String serviceId, @RequestParam(required = false) String sourceServiceId,
			@RequestParam(required = false) String datasetId,
			@RequestParam(required = false) ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) List<ProcessingCategory> processingCategories,
			@RequestParam(defaultValue = "DESC") Sort.Direction iatSort)
			throws ConsentRecordNotFoundException, ConsentManagerException {

		List<ConsentRecordSigned> existingConsents = null;

		if (StringUtils.isNotBlank(consentId) || StringUtils.isNotBlank(serviceId)
				|| StringUtils.isNotBlank(sourceServiceId) || StringUtils.isNotBlank(datasetId) || status != null
				|| purposeCategory != null || processingCategories != null || iatSort != null)
			existingConsents = consentRecordRepo.findByAccountIdAndQuery(accountId, consentId, serviceId,
					sourceServiceId, datasetId, status, purposeId, purposeName, purposeCategory, processingCategories,
					iatSort);
		else
			existingConsents = consentRecordRepo.findByAccountId(accountId);

		List<ConsentRecordSignedPair> result = new ArrayList<ConsentRecordSignedPair>(existingConsents.size());
		HashSet<String> processedCrPairIds = new HashSet<String>(existingConsents.size());

		for (ConsentRecordSigned consent : existingConsents) {

			CommonPart consentCommonPart = consent.getPayload().getCommonPart();
			if (StringUtils.isNotBlank(consentCommonPart.getCrPairId())) {
				if (!processedCrPairIds.contains(consentCommonPart.getCrPairId())) {
					result.add(getConsentRecordPairByCrPairId(accountId, consentCommonPart.getCrPairId()));
					processedCrPairIds.add(consentCommonPart.getCrPairId());
				}

			} else
				result.add(new ConsentRecordSignedPair(consent, null));
		}

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Get the list of signed Consent Record pairs (sink, source) for the input Account Id and SlrId.", description = "In 3rd party consenting case, the input Slr Id can be relative to a linked service acting in the consent either as sink or source.", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of pairs of signed Consent Records belonging to the input Account Id and SlrId.", responseCode = "200") })
	@GetMapping(value = "/accounts/{accountId}/servicelinks/{slrId}/consents/pair", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsByAccountIdAndSlrId(
			@PathVariable String accountId, @PathVariable String slrId)
			throws ConsentRecordNotFoundException, ConsentManagerException {

		List<ConsentRecordSigned> existingConsents = consentRecordRepo
				.findByAccountIdAndPayload_commonPart_slrId(accountId, slrId);

		List<ConsentRecordSignedPair> result = new ArrayList<ConsentRecordSignedPair>(existingConsents.size());
		HashSet<String> processedCrPairIds = new HashSet<String>(existingConsents.size());

		for (ConsentRecordSigned consent : existingConsents) {

			CommonPart consentCommonPart = consent.getPayload().getCommonPart();
			if (StringUtils.isNotBlank(consentCommonPart.getCrPairId())) {
				if (!processedCrPairIds.contains(consentCommonPart.getCrPairId())) {
					result.add(getConsentRecordPairByCrPairId(accountId, consentCommonPart.getCrPairId()));
					processedCrPairIds.add(consentCommonPart.getCrPairId());
				}

			} else
				result.add(new ConsentRecordSignedPair(consent, null));
		}

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Get the list of signed Consent Records for the input Surrogate Id. Optionally can be filtered by ServiceId, Source Service Id, Dataset Id, Consent Status, Purpose Id, Name or Category and Processing Category.", description = "In 3rd party consenting case, the input Surrogate Id can be relative to a linked service acting in the consent either as sink or source. Results can be sorted by the value of the iat timestamp of Consent Record (DESC by default).", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records belonging to the User linked with the input Surrogate Id.", responseCode = "200") })

	@GetMapping(value = "/users/{surrogateId}/consents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsBySurrogateIdAndQuery(
			@PathVariable String surrogateId, @RequestParam(required = false) String serviceId,
			@RequestParam(required = false) String sourceServiceId, @RequestParam(required = false) String datasetId,
			@RequestParam(required = false) ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) List<ProcessingCategory> processingCategories,
			@RequestParam(defaultValue = "DESC") Sort.Direction iatSort) {

		List<ConsentRecordSigned> result = null;

		if (StringUtils.isNotBlank(serviceId) || StringUtils.isNotBlank(sourceServiceId)
				|| StringUtils.isNotBlank(datasetId) || status != null || purposeCategory != null
				|| processingCategories != null || iatSort != null)
			result = consentRecordRepo.findBySurrogateIdAndQuery(surrogateId, serviceId, sourceServiceId, datasetId,
					status, purposeId, purposeName, purposeCategory, processingCategories, iatSort);
		else
			result = consentRecordRepo.findByPayload_commonPart_surrogateId(surrogateId);

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Get the list of signed Consent Record pairs (sink, source) for the input Surrogate Id. Optionally can be filtered by ServiceId, Source Service Id, Dataset Id, Consent Status, Purpose Id, Name or Category and Processing Category.", description = "In 3rd party consenting case, the input Surrogate Id can be relative to a linked service acting in the consent either as sink or source. Results can be sorted by the value of the iat timestamp of Consent Record (DESC by default).", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of pairs of signed Consent Records belonging to the input Surrogate Id (for service linked and acting in the consent either as sink or source).", responseCode = "200") })
	@GetMapping(value = "/users/{surrogateId}/consents/pair", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordsPairsBySurrogateIdAndQuery(
			@PathVariable String surrogateId, @RequestParam(required = false) String serviceId,
			@RequestParam(required = false) String sourceServiceId, @RequestParam(required = false) String datasetId,
			@RequestParam(required = false) ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) List<ProcessingCategory> processingCategories,
			@RequestParam(defaultValue = "DESC") Sort.Direction iatSort)
			throws ConsentRecordNotFoundException, ConsentManagerException {

		List<ConsentRecordSigned> existingConsents = null;

		if (StringUtils.isNotBlank(serviceId) || StringUtils.isNotBlank(sourceServiceId)
				|| StringUtils.isNotBlank(datasetId) || status != null || purposeCategory != null
				|| processingCategories != null || iatSort != null)
			existingConsents = consentRecordRepo.findBySurrogateIdAndQuery(surrogateId, serviceId, sourceServiceId,
					datasetId, status, purposeId, purposeName, purposeCategory, processingCategories, iatSort);
		else
			existingConsents = consentRecordRepo.findByPayload_commonPart_surrogateId(surrogateId);

		List<ConsentRecordSignedPair> result = new ArrayList<ConsentRecordSignedPair>(existingConsents.size());
		HashSet<String> processedCrPairIds = new HashSet<String>(existingConsents.size());

		for (ConsentRecordSigned consent : existingConsents) {

			CommonPart consentCommonPart = consent.getPayload().getCommonPart();
			if (StringUtils.isNotBlank(consentCommonPart.getCrPairId())) {
				if (!processedCrPairIds.contains(consentCommonPart.getCrPairId())) {
					result.add(getConsentRecordPairByCrPairId(consent.getAccountId(), consentCommonPart.getCrPairId()));
					processedCrPairIds.add(consentCommonPart.getCrPairId());
				}

			} else
				result.add(new ConsentRecordSignedPair(consent, null));
		}

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Get the signed Consent Record for the input SurrogateId and CrId.", description = "In 3rd party consenting case, the input Surrogate Id can be relative to a linked service acting in the consent either as sink or source.", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the signed Consent Record belonging to the input Account Id  and SlrId (for service linked and actin in the consent either as sink or source).", responseCode = "200") })
	@GetMapping(value = "/users/{surrogateId}/consents/{crId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ConsentRecordSigned> getConsentRecordBySurrogateIdAndCrId(@PathVariable String surrogateId,
			@PathVariable String crId) throws ConsentRecordNotFoundException, ConsentManagerException {

		ConsentRecordSigned consent = consentRecordRepo.findById(crId).orElseThrow(
				() -> new ConsentRecordNotFoundException("The Consent Record with Cr Id: " + crId + " was not found"));

		return ResponseEntity.ok(consent);

	}

	@Operation(summary = "Get the signed Consent Record Pair for the input SurrogateId and CrId.", description = "In 3rd party consenting case, the input Surrogate Id can be relative to a linked service acting in the consent either as sink or source.", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the signed Consent Record pair belonging to the input Surrogate Id and with Cr Id.", responseCode = "200") })
	@GetMapping(value = "/users/{surrogateId}/consents/{crId}/pair", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ConsentRecordSignedPair> getConsentRecordPairBySurrogateIdAndCrId(
			@PathVariable String surrogateId, @PathVariable String crId)
			throws ConsentRecordNotFoundException, ConsentManagerException {

		ConsentRecordSigned consent = consentRecordRepo.findById(crId).orElseThrow(
				() -> new ConsentRecordNotFoundException("The Consent Record with Cr Id: " + crId + " was not found"));

		CommonPart commonPart = consent.getPayload().getCommonPart();

		ConsentRecordSigned pairedCr = getPairedConsentRecordByCrPairId(surrogateId, commonPart.getCrPairId(),
				commonPart.getRole(), ChangeConsentStatusRequestFrom.SERVICE);

		ConsentRecordSignedPair result = commonPart.getRole().equals(ConsentRecordRoleEnum.SINK)
				? new ConsentRecordSignedPair(consent, pairedCr)
				: new ConsentRecordSignedPair(pairedCr, consent);

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Get the list of paired (sink, source) signed Consent Records for the input Surrogate Id and SlrId.", description = "In 3rd party consenting case, the input Surrogate and Slr Ids can be relative to a linked service acting in the consent either as sink or source.", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of pairs of signed Consent Records belonging to the input Account Id and and SlrId (for service linked and actin in the consent either as sink or source).", responseCode = "200") })
	@GetMapping(value = "/users/{surrogateId}/servicelinks/{slrId}/consents/pair", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsBySurrogateIdAndSlrId(
			@PathVariable String surrogateId, @PathVariable String slrId)
			throws ConsentRecordNotFoundException, ConsentManagerException {

		List<ConsentRecordSigned> existingConsents = consentRecordRepo
				.findByPayload_commonPart_surrogateIdAndPayload_commonPart_slrId(surrogateId, slrId);

		List<ConsentRecordSignedPair> result = new ArrayList<ConsentRecordSignedPair>(existingConsents.size());
		HashSet<String> processedCrPairIds = new HashSet<String>(existingConsents.size());

		for (ConsentRecordSigned consent : existingConsents) {

			CommonPart consentCommonPart = consent.getPayload().getCommonPart();
			if (StringUtils.isNotBlank(consentCommonPart.getCrPairId())) {
				if (!processedCrPairIds.contains(consentCommonPart.getCrPairId())) {
					result.add(getConsentRecordPairByCrPairId(consent.getAccountId(), consentCommonPart.getCrPairId()));
					processedCrPairIds.add(consentCommonPart.getCrPairId());
				}

			} else
				result.add(new ConsentRecordSignedPair(consent, null));
		}

		return ResponseEntity.ok(result);

	}

	private ConsentRecordSigned getPairedConsentRecordByCrPairId(String accountOrSurrogateId, String crPairId,
			ConsentRecordRoleEnum role, ChangeConsentStatusRequestFrom requestFrom)
			throws ConsentRecordNotFoundException, ConsentManagerException {

		ConsentRecordSigned[] crPair;

		if (requestFrom.equals(ChangeConsentStatusRequestFrom.OPERATOR))
			crPair = consentRecordRepo.findByAccountIdAndPayload_commonPart_crPairId(accountOrSurrogateId, crPairId)
					.orElseThrow(() -> new ConsentRecordNotFoundException(
							"No Consent Record pair was found for crPairId: " + crPairId));
		else
			crPair = consentRecordRepo.findByPayload_commonPart_crPairId(crPairId)
					.orElseThrow(() -> new ConsentRecordNotFoundException(
							"No Consent Record pair was found for crPairId: " + crPairId));

		if (crPair.length != 2)
			throw new ConsentManagerException("The found Consent Record Pair is incorrect");

		ConsentRecordSigned otherCr = Arrays.asList(crPair).stream()
				.filter(pair -> pair.getPayload().getCommonPart().getRole()
						.equals(role.equals(ConsentRecordRoleEnum.SINK) ? ConsentRecordRoleEnum.SOURCE
								: ConsentRecordRoleEnum.SINK))
				.findFirst().get();

		if (otherCr == null)
			throw new ConsentRecordNotFoundException(
					"No paired Consent Record found for the crPairId: " + crPairId + " with role: " + role);
		return otherCr;

	}

	private ConsentRecordSignedPair getConsentRecordPairByCrPairId(String accountId, String crPairId)
			throws ConsentRecordNotFoundException, ConsentManagerException {

		ConsentRecordSigned[] crPair = consentRecordRepo
				.findByAccountIdAndPayload_commonPart_crPairId(accountId, crPairId)
				.orElseThrow(() -> new ConsentRecordNotFoundException(
						"No Consent Record pair was found for crPairId: " + crPairId));

		if (crPair.length != 2)
			throw new ConsentManagerException("The found Consent Record Pair is incorrect");

		ConsentRecordSigned sink = null, source = null;
		for (ConsentRecordSigned consent : crPair) {
			if (consent.getPayload().getCommonPart().getRole().equals(ConsentRecordRoleEnum.SINK))
				sink = consent;
			else
				source = consent;
		}

		return new ConsentRecordSignedPair(sink, source);

	}

	@Operation(summary = "Get the list of signed Consent Records for the input Service Id. Optionally can be filtered by Source Service Id, Dataset Id, Consent Status, Purpose Id, Name or Category and Processing Category.", description = "Results can be sorted by the value of the iat timestamp of Consent Record (DESC by default).", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records belonging to the Users linked with the input Service Id.", responseCode = "200") })
	@GetMapping(value = "/services/{serviceId}/consents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByServiceIdAndQuery(
			@PathVariable String serviceId, @RequestParam(required = false) String sourceServiceId,
			@RequestParam(required = false) String datasetId,
			@RequestParam(required = false) ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) List<ProcessingCategory> processingCategories,
			@RequestParam(defaultValue = "DESC") Sort.Direction iatSort) {

		List<ConsentRecordSigned> result = null;

		if (StringUtils.isNotBlank(sourceServiceId) || StringUtils.isNotBlank(datasetId) || status != null
				|| purposeCategory != null || processingCategories != null || iatSort != null)
			result = consentRecordRepo.findByServiceIdAndQuery(serviceId, sourceServiceId, datasetId, status, purposeId,
					purposeName, purposeCategory, processingCategories, iatSort);
		else
			result = consentRecordRepo.findByPayload_commonPart_subjectId(serviceId);

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Get the list of signed Consent Record pairs (sink, source) for the input Service Id. Optionally can be filtered by Source Service Id, Dataset Id, Consent Status, Purpose Id, Name or Category and Processing Category.", description = "Results can be sorted by the value of the iat timestamp of Consent Record (DESC by default).", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Record pairs belonging to the Users linked with the input Service Id.", responseCode = "200") })
	@GetMapping(value = "/services/{serviceId}/consents/pair", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsByServiceIdAndQuery(
			@PathVariable String serviceId, @RequestParam(required = false) String sourceServiceId,
			@RequestParam(required = false) String datasetId,
			@RequestParam(required = false) ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) List<ProcessingCategory> processingCategories,
			@RequestParam(defaultValue = "DESC") Sort.Direction iatSort)
			throws ConsentRecordNotFoundException, ConsentManagerException {

		List<ConsentRecordSigned> existingConsents = null;
		if (sourceServiceId != null || datasetId != null || status != null || purposeCategory != null
				|| processingCategories != null)
			existingConsents = consentRecordRepo.findByServiceIdAndQuery(serviceId, sourceServiceId, datasetId, status,
					purposeId, purposeName, purposeCategory, processingCategories, iatSort);

		List<ConsentRecordSignedPair> result = new ArrayList<ConsentRecordSignedPair>(existingConsents.size());
		HashSet<String> processedCrPairIds = new HashSet<String>(existingConsents.size());

		for (ConsentRecordSigned consent : existingConsents) {

			CommonPart consentCommonPart = consent.getPayload().getCommonPart();
			if (StringUtils.isNotBlank(consentCommonPart.getCrPairId())) {
				if (!processedCrPairIds.contains(consentCommonPart.getCrPairId())) {
					result.add(getConsentRecordPairByCrPairId(consent.getAccountId(), consentCommonPart.getCrPairId()));
					processedCrPairIds.add(consentCommonPart.getCrPairId());
				}

			} else
				result.add(new ConsentRecordSignedPair(consent, null));
		}

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Get the list of signed Consent Records involving Services managed by the input Service Provider Business Id. Optionally can be filtered by Surrogate Id, Service Id, Source Service Id, Dataset Id, Consent Status, Purpose Id, Name or Category and Processing Category.", description = "In 3rd party consenting case, the optional Surrogate Id can be relative to a linked service acting in the consent either as sink or source. Results can be sorted by the value of the iat timestamp of Consent Record (DESC by default).", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records belonging to the Users linked with services provided by the input Business Id.", responseCode = "200") })
	@GetMapping(value = "/dataControllers/{businessId}/consents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSigned>> getConsentRecordsByServiceProviderBusinessIdAndQuery(
			@PathVariable String businessId, @RequestParam(required = false) String surrogateId,
			@RequestParam(required = false) String serviceId, @RequestParam(required = false) String sourceServiceId,
			@RequestParam(required = false) String datasetId,
			@RequestParam(required = false) ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) List<ProcessingCategory> processingCategories,
			@RequestParam(defaultValue = "DESC") Sort.Direction iatSort) {

		List<ConsentRecordSigned> result = null;

		if (surrogateId != null || serviceId != null || sourceServiceId != null || datasetId != null || status != null
				|| purposeCategory != null || processingCategories != null || status != null || purposeCategory != null
				|| processingCategories != null)
			result = consentRecordRepo.findByBusinessIdAndQuery(businessId, surrogateId, serviceId, sourceServiceId,
					datasetId, status, purposeId, purposeName, purposeCategory, processingCategories, iatSort);
		else
			result = consentRecordRepo.findByPayload_commonPart_serviceProviderBusinessId(businessId);

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Get the list of signed Consent Record pairs (sink, source) involving Services managed by the input Service Provider Business Id. Optionally can be filtered by Surrogate Id, Service Id, Source Service Id, Dataset Id, Consent Status, Purpose Id, Name or Category and Processing Category.", description = "In 3rd party consenting case, the optional Surrogate Id can be relative to a linked service acting in the consent either as sink or source. Results can be sorted by the value of the iat timestamp of Consent Record (DESC by default).", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Records belonging to the Users linked with services provided by the input Business Id.", responseCode = "200") })
	@GetMapping(value = "/dataControllers/{businessId}/consents/pair", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentRecordSignedPair>> getConsentRecordPairsByServiceProviderBusinessIdAndQuery(
			@PathVariable String businessId, @RequestParam(required = false) String surrogateId,
			@RequestParam(required = false) String serviceId, @RequestParam(required = false) String sourceServiceId,
			@RequestParam(required = false) String datasetId,
			@RequestParam(required = false) ConsentRecordStatusEnum status,
			@RequestParam(required = false) String purposeId, @RequestParam(required = false) String purposeName,
			@RequestParam(required = false) PurposeCategory purposeCategory,
			@RequestParam(required = false) List<ProcessingCategory> processingCategories,
			@RequestParam(defaultValue = "DESC") Sort.Direction iatSort)
			throws ConsentRecordNotFoundException, ConsentManagerException {

		List<ConsentRecordSigned> existingConsents = null;

		if (serviceId != null || sourceServiceId != null || datasetId != null || status != null
				|| purposeCategory != null || processingCategories != null || status != null || purposeCategory != null
				|| processingCategories != null)
			existingConsents = consentRecordRepo.findByBusinessIdAndQuery(businessId, surrogateId, serviceId,
					sourceServiceId, datasetId, status, purposeId, purposeName, purposeCategory, processingCategories,
					iatSort);
		else
			existingConsents = consentRecordRepo.findByPayload_commonPart_serviceProviderBusinessId(businessId);

		List<ConsentRecordSignedPair> result = new ArrayList<ConsentRecordSignedPair>(existingConsents.size());
		HashSet<String> processedCrPairIds = new HashSet<String>(existingConsents.size());

		for (ConsentRecordSigned consent : existingConsents) {

			CommonPart consentCommonPart = consent.getPayload().getCommonPart();
			if (StringUtils.isNotBlank(consentCommonPart.getCrPairId())) {
				if (!processedCrPairIds.contains(consentCommonPart.getCrPairId())) {
					result.add(getConsentRecordPairByCrPairId(consent.getAccountId(), consentCommonPart.getCrPairId()));
					processedCrPairIds.add(consentCommonPart.getCrPairId());
				}

			} else
				result.add(new ConsentRecordSignedPair(consent, null));
		}

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Get the list of signed Consent Status Records for the input Account Id, Slr Id and Cr Id.", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the list of signed Consent Status Records belonging to the input Account Id, Slr Id and Cr Id.", responseCode = "200") })
	@GetMapping(value = "/accounts/{accountId}/servicelinks/{slrId}/consents/{crId}/statuses", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ConsentStatusRecordSigned>> getConsentStatusRecordsByAccountIdAndSlrIdAndCrId(
			@PathVariable String accountId, @PathVariable String slrId, @PathVariable String crId)
			throws ConsentRecordNotFoundException {

		List<ConsentStatusRecordSigned> result = consentRecordRepo
				.getConsentStatusRecordsByAccountIdAndSlrIdAndCrId(accountId, slrId, crId)
				.orElseThrow(() -> new ConsentRecordNotFoundException("No Consent Record found for Account Id: "
						+ accountId + ", Slr Id: " + slrId + " and Cr Id: " + crId));

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Get the signed Consent Status Record for the input Account Id, Slr Id, Cr Id and Ssr Id.", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the signed Consent Status Record belonging to the Consent Record matching input Account Id, Slr Id, Cr Id and Ssr.", responseCode = "200") })
	@GetMapping(value = "/accounts/{accountId}/servicelinks/{slrId}/consents/{crId}/statuses/{recordId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ConsentStatusRecordSigned> getConsentStatusRecordByAccountIdAndSlrIdAndCrIdAndRecordId(
			@PathVariable String accountId, @PathVariable String slrId, @PathVariable String crId,
			@PathVariable String recordId) throws ConsentRecordNotFoundException {

		ConsentStatusRecordSigned result = consentRecordRepo
				.getConsentStatusRecordByAccountIdAndSlrIdAndCrIdAndRecordId(accountId, slrId, crId, recordId)
				.orElseThrow(() -> new ConsentRecordNotFoundException("No Consent Record found for Account Id: "
						+ accountId + ", Slr Id: " + slrId + ", Cr Id: " + crId + " and record Id: " + recordId));

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Get the last signed Consent Status Records for the input Account Id, Slr Id and Cr Id.", tags = {
			"Consent Record" }, responses = {
					@ApiResponse(description = "Returns the last signed Consent Status Records belonging to the Consent Record matchin the input Account Id, Slr Id and Cr Id.", responseCode = "200") })
	@GetMapping(value = "/accounts/{accountId}/servicelinks/{slrId}/consents/{crId}/statuses/last", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ConsentStatusRecordSigned> getLastConsentStatusRecordByAccountIdAndSlrIdAndCrId(
			@PathVariable String accountId, @PathVariable String slrId, @PathVariable String crId)
			throws ConsentRecordNotFoundException {

		ConsentStatusRecordSigned result = consentRecordRepo
				.getLastConsentStatusRecordByAccountIdAndSlrIdAndCrId(accountId, slrId, crId)
				.orElseThrow(() -> new ConsentRecordNotFoundException("No Consent Record found for Account Id: "
						+ accountId + ", Slr Id: " + slrId + " and Cr Id: " + crId));

		return ResponseEntity.ok(result);

	}

	@Operation(summary = "Change status, starting from Operator (Cape Server / User Dashboard) of an existing Consent Record associated to the the input AccountId, Slr Id and Cr Id.", description = "The new Status can contain not only the new state (Active, Disabled, Withdrawn), but also a new Resource Set or Sink Usage Rules.", tags = {
			"Consenting" }, responses = {
					@ApiResponse(description = "Returns 201 Created with the new Consent Status Record Signed.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsentStatusRecordSigned.class))) })
	@PostMapping(value = "/accounts/{accountOrSurrogateId}/servicelinks/{slrId}/consents/{crId}/statuses", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ConsentRecordSigned> changeConsentStatus(@PathVariable String accountOrSurrogateId,
			@PathVariable String slrId, @PathVariable String crId,
			@RequestBody @Valid ChangeConsentStatusRequest request)
			throws ConsentRecordNotFoundException, ConsentManagerException, ConsentStatusNotValidException,
			ResourceSetIdNotFoundException, ServiceLinkRecordNotFoundException, ServiceLinkStatusNotValidException,
			ServiceDescriptionNotFoundException, ChangeConsentStatusException {

		ConsentRecordStatusEnum newStatus = request.getStatus();
		DataMapping[] newConcepts = request.getResourceSet().getDatasets()
				.get(request.getResourceSet().getDatasets().size() - 1).getDataMappings().stream()
				.toArray(DataMapping[]::new);
		ConsentActionType actionType;

		ConsentRecordSigned existingCr, updatedCr, updatedPairedCr = null, pairedCr = null;
		ConsentStatusRecordSigned newCsrSigned, sourceCsrSigned = null;
		CommonPart pairedCrCommonPart = null;
		String pairedServiceId = null;
		ConsentRecordStatusEnum lastSourceStatus = null;

		if (newStatus != null)
			switch (newStatus) {
			case Disabled:
				actionType = ConsentActionType.DISABLE;
				break;
			case Withdrawn:
				actionType = ConsentActionType.WITHDRAW;
				break;
			default:
				actionType = ConsentActionType.ACTIVATE;

			}
		else
			actionType = ConsentActionType.UPDATE;

		/****************************************************************************
		 * Get the Service Link Record associated to the Consent Record (either by
		 * AccountId or Surrogate Id depending on which part initiated the request) to
		 * check if its status is compatible with the new Consent Status Record (e.g.
		 * Activating a Consent with its related SLR removed)
		 *******************************************************************************/

		ServiceLinkRecordDoubleSigned associatedSlr = null;
		associatedSlr = request.getRequestFrom().equals(ChangeConsentStatusRequestFrom.OPERATOR)
				? clientService.callGetServiceLinkRecordByAccountIdAndSlrId(accountOrSurrogateId, slrId)
				: clientService.callGetServiceLinkRecordBySurrogateId(accountOrSurrogateId);
		ServiceLinkRecordPayload associateSlrPayload = associatedSlr.getPayload();
		ServiceLinkStatusRecordPayload associatedSlrStatus = associatedSlr.getServiceLinkStatuses()
				.get(associatedSlr.getServiceLinkStatuses().size() - 1).getPayload();

		if (actionType.equals(ConsentActionType.ACTIVATE)
				&& associatedSlrStatus.getServiceLinkStatus().equals(ServiceLinkStatusEnum.REMOVED))
			throw new ServiceLinkStatusNotValidException("The Service Link Record with id: " + slrId
					+ " must be Active to Activate the related Consent Record with id: " + crId);

		/*
		 * **************************************************************************
		 * Get the existing Consent Record related to the input pair
		 * accountOrSurrogateId - crId
		 ***************************************************************************/

		existingCr = (request.getRequestFrom().equals(ChangeConsentStatusRequestFrom.OPERATOR)
				? consentRecordRepo.findByAccountIdAndPayload_commonPart_crId(accountOrSurrogateId, crId)
				: consentRecordRepo
						.findByPayload_commonPart_surrogateIdAndPayload_commonPart_crId(accountOrSurrogateId, crId))
								.orElseThrow(() -> new ConsentRecordNotFoundException(
										"No Consent Record found for Account/Surrogate Id: " + accountOrSurrogateId
												+ ", Slr Id: " + slrId + " and Cr Id: " + crId));

		CommonPart existingCrCommonPart = existingCr.getPayload().getCommonPart();
		ResourceSet existingResourceSet = existingCrCommonPart.getRsDescription().getResourceSet();
		String serviceId = existingCrCommonPart.getSubjectId();
		SinkUsageRules existingUsageRules = ((ConsentRecordSinkRoleSpecificPart) existingCr.getPayload()
				.getRoleSpecificPart()).getUsageRules();
		LegalBasis existingLegalBasis = existingUsageRules.getLegalBasis();
		ConsentStatusRecordPayload lastCsrPayload = existingCr.getConsentStatusList()
				.get(existingCr.getConsentStatusList().size() - 1).getPayload();
		ConsentRecordStatusEnum lastStatus = lastCsrPayload.getConsentStatus();

		/***************************************************************************
		 * Initial Checks 1. Status validity 2. Rs Id validity
		 ****************************************************/
		if (!newStatusIsValid(lastStatus, newStatus))
			throw new ConsentStatusNotValidException(
					"The new Consent Record Status: " + newStatus + " is not valid for current status: " + lastStatus);

		if (!request.getResourceSet().getRsId().equals(existingResourceSet.getRsId()))
			throw new ResourceSetIdNotFoundException(
					"The input RsId does not match with the one in the existing Consent Record: "
							+ existingResourceSet.getRsId());

		/*
		 * Get the previously existing Concepts, either from the Consent Status (if more
		 * than one) or from Consent Record Resource Set
		 */
		DataMapping[] existingConcepts;
		if (lastCsrPayload.getPrevRecordId().equals("none"))
			existingConcepts = existingResourceSet.getDatasets().get(existingResourceSet.getDatasets().size() - 1)
					.getDataMappings().stream().toArray(DataMapping[]::new);
		else {
			existingConcepts = lastCsrPayload.getConsentResourceSet().getDatasets()
					.get(lastCsrPayload.getConsentResourceSet().getDatasets().size() - 1).getDataMappings().stream()
					.toArray(DataMapping[]::new);
		}

		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

		/***************************************************************************
		 * Create the new Consent Status Record, with the input new Status (if null use
		 * the last status) and other new state info from request body (ResourceSet,
		 * UsageRules)
		 **************************************************************************/
		if (newStatus == null)
			newStatus = lastStatus;

		String newCsrId = new ObjectId().toString();
		ConsentStatusRecordPayload newCsrPayload = new ConsentStatusRecordPayload(newCsrId,
				appProperty.getCape().getVersion(), existingCrCommonPart.getSurrogateId(),
				existingCrCommonPart.getCrId(), newStatus, request.getResourceSet(), request.getUsageRules(), now,
				lastCsrPayload.getRecordId());

		/******************************************************************************
		 * If there is a Consent Record pair (Third Party re-use case), get the other
		 * Consent Record of the pair (Source or Sink, depending on the role of the
		 * already retrieved one)
		 *****************************************************/
		String crPairId = existingCrCommonPart.getCrPairId();
		if (StringUtils.isNotBlank(crPairId)) {

			pairedCr = getPairedConsentRecordByCrPairId(accountOrSurrogateId, crPairId, existingCrCommonPart.getRole(),
					request.getRequestFrom());
			pairedCrCommonPart = pairedCr.getPayload().getCommonPart();
			String sourceSlrId = pairedCrCommonPart.getSlrId();
			String sourceSurrogateId = pairedCrCommonPart.getSurrogateId();
			pairedServiceId = pairedCrCommonPart.getSubjectId();

			ConsentStatusRecordPayload lastSourceCsrPayload = pairedCr.getConsentStatusList()
					.get(pairedCr.getConsentStatusList().size() - 1).getPayload();

			lastSourceStatus = lastSourceCsrPayload.getConsentStatus();

			/****************************************************************************
			 * Get the Service Link Record associated to the Consent Record (either by
			 * AccountId or Surrogate Id depending on which part initiated the request) to
			 * check if its status is compatible with the new Consent Status Record (e.g.
			 * Activating a Consent with its related SLR removed)
			 *******************************************************************************/

			ServiceLinkRecordDoubleSigned associatedSourceSlr = request.getRequestFrom()
					.equals(ChangeConsentStatusRequestFrom.OPERATOR)
							? clientService.callGetServiceLinkRecordByAccountIdAndSlrId(accountOrSurrogateId,
									sourceSlrId)
							: clientService.callGetServiceLinkRecordBySurrogateId(sourceSurrogateId);

			ServiceLinkStatusRecordPayload associatedSourceSlrStatus = associatedSourceSlr.getServiceLinkStatuses()
					.get(associatedSourceSlr.getServiceLinkStatuses().size() - 1).getPayload();

			if (actionType.equals(ConsentActionType.ACTIVATE)
					&& associatedSourceSlrStatus.getServiceLinkStatus().equals(ServiceLinkStatusEnum.REMOVED))
				throw new ServiceLinkStatusNotValidException("The Service Link Record with id: " + sourceSlrId
						+ " must be Active to Activate the paired Consent Record of the one with id: " + crId);

			/*************************************************************************
			 * Create the new Consent Status Record for the Source, with the input new
			 * Status and other state info from Consent Record (ResourceSet, UsageRules)
			 **************************************************************************/
			ConsentStatusRecordPayload sourceCsrPayload = new ConsentStatusRecordPayload(new ObjectId().toString(),
					appProperty.getCape().getVersion(), pairedCrCommonPart.getSurrogateId(),
					pairedCrCommonPart.getCrId(), newStatus, request.getResourceSet(), request.getUsageRules(), now,
					lastSourceCsrPayload.getRecordId());

			/***************************************************************************
			 * Call Account Manager to sign the new Consent Status Record both for the
			 * Source and the Sink. Sign also the updated Consent Records, as the CSR list
			 * changed
			 **************************************************************************/

			ThirdPartyReuseConsentSignResponse signResponse = clientService.callSignThirdPartyReuseConsent(
					associatedSlr.getPayload().getSurrogateId(), associateSlrPayload.getSlrId(), sourceSlrId,
					existingCr.getPayload(), newCsrPayload, pairedCr.getPayload(), sourceCsrPayload,
					existingCr.getConsentStatusList(), pairedCr.getConsentStatusList());

			updatedCr = signResponse.getSink().getSignedCr();
			newCsrSigned = signResponse.getSink().getSignedCsr();
			updatedPairedCr = signResponse.getSource().getSignedCr();
			sourceCsrSigned = signResponse.getSource().getSignedCsr();

		} else {

			/***************************************************************************
			 * Call Account Manager to sign the new Consent Status Record. Sign also the
			 * updated Consent Record, as the CSR list changed
			 ***************************************************************************/

			WithinServiceConsentSignResponse signResponse = clientService.callSignWithinServiceConsent(
					associatedSlr.getPayload().getSurrogateId(), slrId, existingCr.getPayload(), newCsrPayload,
					existingCr.getConsentStatusList());
			updatedCr = signResponse.getSignedCr();
			newCsrSigned = signResponse.getSignedCsr();
		}

		/***************************************************************************
		 * Once Sink (and if in that case also Source) Cr and Csr has been correctly
		 * made and signed, save locally and send them to the Service(s)
		 *************************************************************************/
		updatedCr.setAccountId(existingCr.getAccountId());
		updatedCr.set_id(new ObjectId(updatedCr.getPayload().getCommonPart().getCrId()));
		consentRecordRepo.save(updatedCr);

		/***************************************************************************
		 * Send the new signed Consent Status Record to the Service. Rollback stored CR
		 * and CSR in case of failure ( to keep consistent Operator and Service CRs)
		 **************************************************************************/
		try {
			ServiceEntry serviceDescription = clientService.getServiceDescriptionFromRegistry(serviceId);
			clientService.sendUpdatedConsentRecordToService(
					serviceDescription.getServiceInstance().getServiceUrls().getLibraryDomain(), updatedCr);
		} catch (Exception e) {

			/*
			 * Rollback stored CR
			 */
			consentRecordRepo.save(existingCr);

			throw new ChangeConsentStatusException(
					"There was an error while sending the Consent Status Record to the Sink Service: "
							+ e.getMessage());
		}

		if (StringUtils.isNotBlank(crPairId)) {
			/*
			 * **************************************************************************
			 * Store signed Source Consent Status Record into relative Consent Record
			 ***************************************************************************/
			updatedPairedCr.setAccountId(pairedCr.getAccountId());
			updatedPairedCr.set_id(new ObjectId(updatedPairedCr.getPayload().getCommonPart().getCrId()));
			consentRecordRepo.save(updatedPairedCr);

			/***************************************************************************
			 * Send the new signed Consent Status Record to the Source Service
			 ***************************************************************************/
			try {
				ServiceEntry sourceServiceDescription = clientService.getServiceDescriptionFromRegistry(serviceId);
				clientService.sendUpdatedConsentRecordToService(
						sourceServiceDescription.getServiceInstance().getServiceUrls().getLibraryDomain(),
						updatedPairedCr);
			} catch (Exception e) {

				/*
				 * Rollback stored CRs (both for Sink and Source)
				 */
				consentRecordRepo.save(existingCr);
				consentRecordRepo.save(pairedCr);

				throw new ChangeConsentStatusException(
						"There was an error while sending the Consent Status Record to the Source Service: "
								+ e.getMessage());
			}

			/***************************************************************************
			 * Auditing for Consent Update (Third party reuse case)
			 **************************************************************************/
			// Audit for Update Consent
			clientService.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, pairedCr.getAccountId(),
					existingLegalBasis, "Consent Status updated", existingUsageRules, serviceId, pairedServiceId,
					newConcepts, existingConcepts, actionType, lastStatus, existingCrCommonPart.getCrId()));

		} else {

			/***************************************************************************
			 * Auditing for Consent Update (Within service case)
			 **************************************************************************/

			clientService.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, existingCr.getAccountId(),
					existingLegalBasis, "Consent Status updated", existingUsageRules, serviceId, serviceId, newConcepts,
					existingConcepts, actionType, lastStatus, existingCrCommonPart.getCrId()));
		}

		/*
		 * Audit of consent sending (Either Case)
		 */
		clientService.callAddEventLog(new ConsentEventLog(now, EventType.CONSENT, existingCr.getAccountId(),
				existingLegalBasis, "Consent Record sent", existingUsageRules, serviceId, pairedServiceId, newConcepts,
				existingConcepts, ConsentActionType.SEND, lastSourceStatus, existingCrCommonPart.getCrId()));

		/**************************************************************************
		 * Return the new Consent Status Record
		 **************************************************************************/
		return ResponseEntity.created(UriComponentsBuilder
				.fromHttpUrl(consentManagerHost
						+ "/accounts/{accountId}/servicelinks/{slrId}/consents/{crId}/statuses/{recordId}")
				.build(existingCr.getAccountId(), crId, slrId, newCsrId)).body(updatedCr);

	}

	@Operation(summary = "Delete all the signed Consent Records for the input Account Id.", tags = {
			"Consent Record" }, responses = { @ApiResponse(description = "Returns No Content.", responseCode = "204") })
	@DeleteMapping(value = "/accounts/{accountId}/consents")
	@Override
	public ResponseEntity<ConsentRecordSigned> deleteConsentRecordsByAccountId(@PathVariable String accountId,
			@RequestParam(defaultValue = "true") Boolean deleteConsentForms)
			throws ConsentRecordNotFoundException, ConsentManagerException {

		if (!deleteConsentForms)
			consentRecordRepo.deleteConsentRecordSignedByAccountId(accountId);
		else {
			List<ConsentRecordSigned> existingConsents = consentRecordRepo.findByAccountId(accountId);

			consentRecordRepo.deleteConsentRecordSignedByAccountId(accountId);

			for (ConsentRecordSigned consent : existingConsents) {
				CommonPart consentCommonPart = consent.getPayload().getCommonPart();
				consentFormRepo.deleteConsentFormBySurrogateId(consentCommonPart.getSurrogateId());
			}

		}

		return ResponseEntity.noContent().build();

	}

	@Operation(summary = "Delete all the Consent Forms of the Cape Account associated to the input Surrogate Id.", tags = {
			"Consent Record" }, responses = { @ApiResponse(description = "Returns No Content.", responseCode = "204") })
	@DeleteMapping(value = "/users/{surrogateId}/consentForms")
	@Override
	public ResponseEntity<String> deleteConsentFormsBySurrogateId(@PathVariable String surrogateId) {

		consentFormRepo.deleteConsentFormBySurrogateId(surrogateId);

		return ResponseEntity.noContent().build();

	}

	@Override
	@Operation(summary = "Get the Authorisation Token used by Sink service in the Data Requests.", tags = {
			"Consenting" }, responses = {
					@ApiResponse(description = "Returns the AuthorisationToken signed with the Operator private key") })
	@GetMapping(value = "/auth_token/{cr_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AuthorisationTokenResponse> getAuthorisationToken(@PathVariable(name = "cr_id") String crId)
			throws ConsentRecordNotFoundException, ConsentManagerException, ServiceLinkRecordNotFoundException {

		ConsentRecordSigned consent = consentRecordRepo.findById(crId).orElseThrow(
				() -> new ConsentRecordNotFoundException("The Consent Record with Cr Id: " + crId + " was not found"));
		CommonPart commonPart = consent.getPayload().getCommonPart();
		String surrogateId = commonPart.getSurrogateId();

		ConsentRecordSigned pairedCr = getPairedConsentRecordByCrPairId(surrogateId, commonPart.getCrPairId(),
				ConsentRecordRoleEnum.SINK, ChangeConsentStatusRequestFrom.SERVICE);

		ServiceLinkRecordDoubleSigned serviceLink = clientService.callGetServiceLinkRecordBySurrogateIdAndServiceId(
				commonPart.getSurrogateId(), commonPart.getSubjectId());
		ServicePopKey sinkPopKey = serviceLink.getPayload().getPopKey();

		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

		return ResponseEntity.ok()
				.body(clientService.callSignAuthorisationToken(operatorId, new AuthorisationTokenPayload(operatorId,
						new AuthorisationTokenPayloadPopKid(sinkPopKey.getJwk().getKeyID()), new String[] { "/data" },
						now.plus(consentExpiration, ChronoUnit.SECONDS), null, now, new ObjectId().toString(), crId)));

	}

	private Boolean newStatusIsValid(ConsentRecordStatusEnum lastStatus, ConsentRecordStatusEnum newStatus) {

		return (!lastStatus.equals(ConsentRecordStatusEnum.Withdrawn) && !lastStatus.equals(newStatus));
//						&& (newStatus.equals(ConsentRecordStatusEnum.Disabled)
//								|| newStatus.equals(ConsentRecordStatusEnum.Withdrawn)))
//				|| (lastStatus.equals(ConsentRecordStatusEnum.Disabled)
//						&& (newStatus.equals(ConsentRecordStatusEnum.Active)
//								|| newStatus.equals(ConsentRecordStatusEnum.Withdrawn)));

	}

}
