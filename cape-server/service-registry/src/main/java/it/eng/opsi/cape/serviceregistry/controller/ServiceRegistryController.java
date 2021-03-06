package it.eng.opsi.cape.serviceregistry.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.oauth2.sdk.util.StringUtils;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.eng.opsi.cape.exception.DatasetIdNotFoundException;
import it.eng.opsi.cape.exception.PurposeIdNotFoundException;
import it.eng.opsi.cape.exception.ServiceNotEditableException;
import it.eng.opsi.cape.exception.ServiceNotFoundException;
import it.eng.opsi.cape.serviceregistry.ApplicationProperties;
import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;
import it.eng.opsi.cape.serviceregistry.data.ServiceInstance;
import it.eng.opsi.cape.serviceregistry.model.ServiceReport;
import it.eng.opsi.cape.serviceregistry.model.ServiceReportGroupingCriteria;
import it.eng.opsi.cape.serviceregistry.repository.ServiceEntryRepository;
import lombok.extern.slf4j.Slf4j;

@OpenAPIDefinition(security = { @SecurityRequirement(name = "bearer-key") }, tags = {
		@Tag(name = "Service Entry", description = "Service entry Description APIs to get and manage service entry descriptions."),
		@Tag(name = "Service Dataset Data Mapping", description = "APIs to get Data Mapping for Services Datasets"),
		@Tag(name = "Service Report", description = "Service Report APIs about Service descriptions.") }, info = @Info(title = "CaPe API - Service Registry", description = "Servicve Registry APIs used to manage Service entry descriptions and get data mappings and reports.", version = "2.0"))
@RestController
@RequestMapping("/api/v2")
@Slf4j
public class ServiceRegistryController implements IServiceRegistryController {

	@Autowired
	ServiceEntryRepository serviceRepo;

	private final ApplicationProperties appProperty;
	private final String serviceRegistryPublicUrl;

	@Autowired
	public ServiceRegistryController(ApplicationProperties appProperty) {
		this.appProperty = appProperty;
		this.serviceRegistryPublicUrl = this.appProperty.getCape().getServiceRegistry().getHost();
	}

	@Operation(summary = "Get all the Service Entry descriptions. Optionally return only services registered to Cape, namely having not null Cert field", description = "Get all the registered services. Optionally filter also by by Service Name, Service Url or Service Provider Business Id.", tags = {
			"Service Entry" }, responses = {
					@ApiResponse(description = "Returns the list of all registered Service Entry descriptions", responseCode = "200") })
	@GetMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ServiceEntry>> getServices(@RequestParam(required = false) String serviceName,
			@RequestParam(required = false) String serviceUrl, @RequestParam(required = false) String businessId,
			@RequestParam(defaultValue = "false") Boolean onlyRegistered) throws ServiceNotFoundException {

		List<ServiceEntry> result = null;

		if (!onlyRegistered) {
			if (StringUtils.isNotBlank(serviceName))
				result = Arrays.asList(
						serviceRepo.findByServiceName(serviceName).orElseThrow(() -> new ServiceNotFoundException(
								"No Service description found for Service Name: " + serviceName)));
			else if (StringUtils.isNotBlank(serviceUrl))
				result = Arrays
						.asList(serviceRepo.findByServiceUrl(serviceUrl).orElseThrow(() -> new ServiceNotFoundException(
								"No Service description found for Service Url: " + serviceUrl)));
			else if (StringUtils.isNotBlank(businessId))
				result = serviceRepo.findByServiceProviderBusinessId(businessId);
			else
				result = serviceRepo.findAll();

		} else {
			result = StringUtils.isNotBlank(businessId)
					? serviceRepo.findRegisteredServicesByServiceProviderBusinessId(businessId)
					: serviceRepo.findAllRegisteredServices();

		}

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Get the Service Entry description by Service Id. (Registered to CaPe or not)", tags = {
			"Service Entry" }, responses = {
					@ApiResponse(description = "Returns the requested Service Entry", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceEntry.class))) })
	@GetMapping(value = "/services/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ServiceEntry> getServiceById(@PathVariable String serviceId,
			@RequestParam(defaultValue = "false") Boolean onlyRegistered) throws ServiceNotFoundException {

		ServiceEntry result = null;

		if (onlyRegistered)
			result = serviceRepo.findRegisteredByServiceId(serviceId).orElseThrow(
					() -> new ServiceNotFoundException("No Service description found for Service Id: " + serviceId));
		else
			result = serviceRepo.findByServiceId(serviceId).orElseThrow(
					() -> new ServiceNotFoundException("No Service description found for Service Id: " + serviceId));

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Get the count of the registered Service Entry descriptions.", tags = {
			"Service Entry" }, responses = {
					@ApiResponse(description = "Returns the requested Service Entry", responseCode = "200") })
	@GetMapping(value = "/services/count", produces = MediaType.TEXT_PLAIN_VALUE)
	@Override
	public ResponseEntity<String> getServicesCount(@RequestParam(defaultValue = "false") Boolean onlyRegistered) {

		return ResponseEntity.ok(serviceRepo.getServicesCount(onlyRegistered).toString());
	}

	@Operation(summary = "Create a new Service Entry description.", tags = { "Service Entry" }, responses = {
			@ApiResponse(description = "Returns 201 Created with the created Service Entry.", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceEntry.class))) })
	@Override
	@PostMapping(value = "/services")
	public ResponseEntity<ServiceEntry> createService(@RequestBody ServiceEntry service) {

		// Set Service Cert to null, it will be created by Sdk when registering
		// Service to Cape
		service.getServiceInstance().setCert(null);
		ServiceEntry result = serviceRepo.insert(service);

		return ResponseEntity.created(URI.create(serviceRegistryPublicUrl + "/services/" + result.getServiceId()))
				.body(result);
	}

	@Operation(summary = "Update Service Entry description, by replacing the existing one", tags = {
			"Service Entry" }, responses = {
					@ApiResponse(description = "Returns the updated Service Entry.", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceEntry.class))) })
	@Override
	@PutMapping(value = "/services/{serviceId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceEntry> updateService(@PathVariable String serviceId, @RequestBody ServiceEntry service)
			throws ServiceNotFoundException, ServiceNotEditableException {

		ServiceEntry existingService = serviceRepo.findByServiceId(serviceId).orElseThrow(
				() -> new ServiceNotFoundException("No Service description found for Service Id: " + serviceId));
		ServiceEntry result;
		// Check if Service has Cert -> then is registered on Cape, reject Description
		// update
		if (existingService.getServiceInstance().getCert() != null
				&& ((existingService.getServiceInstance().getCert().getX5c() != null
						&& !existingService.getServiceInstance().getCert().getX5c().isEmpty())
						|| StringUtils.isNotBlank(existingService.getServiceInstance().getCert().getX5u())))
			throw new ServiceNotEditableException("The Service description with Service Id: " + serviceId
					+ " is registered on Cape and then can't be updated. First unregister from CaPe using SDK API.");
		else
			result = serviceRepo.updateService(serviceId, service).orElseThrow(
					() -> new ServiceNotFoundException("No Service description found for Service Id: " + serviceId));

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Delete CaPe Account by Id or Username. Optionally deletes only the Cert field, in order to be considered unregistered by CaPe.", tags = {
			"Service Entry" }, responses = { @ApiResponse(description = "Returns No Content.", responseCode = "204") })
	@Override
	@DeleteMapping(value = "/services/{serviceId}")
	public ResponseEntity<Object> deleteService(@PathVariable String serviceId,
			@RequestParam(defaultValue = "false") Boolean deleteOnlyCertificate) throws ServiceNotFoundException {

		if (deleteOnlyCertificate) {
			ServiceEntry existingService = serviceRepo.findByServiceId(serviceId).orElseThrow(
					() -> new ServiceNotFoundException("No Service description found for Service Id: " + serviceId));
			ServiceInstance serviceInstance = existingService.getServiceInstance();
			serviceInstance.setCert(null);
			existingService.setServiceInstance(serviceInstance);
			serviceRepo.updateService(serviceId, existingService);

		} else if (serviceRepo.deleteServiceEntryByServiceId(serviceId) == 0L)
			throw new ServiceNotFoundException("No Service description found for Service Id: " + serviceId);

		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Get the Data Mapping list of the specified Dataset and Service", tags = {
			"Service Dataset Data Mapping" }, responses = {
					@ApiResponse(description = "Returns the requested Service Entry", responseCode = "200") })
	@GetMapping(value = "/services/{serviceId}/datasets/{datasetId}/dataMappings", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<DataMapping>> getDataMappingListByServiceIdAndDatasetId(@PathVariable String serviceId,
			@PathVariable String datasetId)
			throws ServiceNotFoundException, PurposeIdNotFoundException, DatasetIdNotFoundException {

		return ResponseEntity.ok(serviceRepo.getDatasetDataMappingByServiceIdAndDatasetId(serviceId, datasetId));
	}

	@Operation(summary = "Get the Data Mapping list of the specified Dataset and Service", tags = {
			"Service Dataset Data Mapping" }, responses = {
					@ApiResponse(description = "Returns the requested Service Entry", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceEntry.class))) })
	@GetMapping(value = "/services/{serviceId}/purposes/{purposeId}/dataMappings", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<DataMapping>> getDataMappingListByServiceIdAndPurposeId(@PathVariable String serviceId,
			@PathVariable String purposeId)
			throws ServiceNotFoundException, PurposeIdNotFoundException, DatasetIdNotFoundException {

		return ResponseEntity.ok(serviceRepo.getDatasetDataMappingByServiceIdAndPurposeId(serviceId, purposeId));
	}

	@Operation(summary = "Get the Service Report, grouped by input criteria", tags = {
			"Service Dataset Data Mapping" }, responses = {
					@ApiResponse(description = "Returns the requested Service Report", responseCode = "200") })
	@GetMapping(value = "/serviceReport", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<List<ServiceReport>> getServiceReport(@RequestParam ServiceReportGroupingCriteria groupBy) {

		return ResponseEntity.ok(serviceRepo.getServiceReport(groupBy));
	}

	@Operation(summary = "Get the ProcessingBasis of Service with input Service Id and Purpose Id", tags = {
			"Service Entry" }, responses = {
					@ApiResponse(description = "Returns the requested ProcessingBasis", responseCode = "200") })
	@GetMapping(value = "/services/{serviceId}/purposes/{purposeId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public ResponseEntity<ProcessingBasis> getProcessingBasisByServiceIdAndPurposeId(@PathVariable String serviceId,
			@PathVariable String purposeId) throws ServiceNotFoundException, PurposeIdNotFoundException {

		return ResponseEntity.ok(serviceRepo.getProcessingBasisByServiceIdAndPurposeId(serviceId, purposeId));
	}

}
