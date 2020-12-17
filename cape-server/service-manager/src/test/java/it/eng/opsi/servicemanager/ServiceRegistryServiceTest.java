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
///*******************************************************************************
// * CaPe - a Consent Based Personal Data Suite
// *   Copyright (C) 2019 Engineering Ingegneria Informatica S.p.A.
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
//package it.eng.opsi.servicemanager;
//
//import org.junit.*;
//import org.junit.runner.RunWith;
//import static org.mockito.ArgumentMatchers.any;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//
//import static org.mockito.Mockito.when;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.validation.constraints.AssertTrue;
//
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import it.eng.opsi.cape.servicemanager.data.DataMapping;
//import it.eng.opsi.cape.servicemanager.data.HumanReadableDescription;
//import it.eng.opsi.cape.servicemanager.data.ServiceEntry;
//import it.eng.opsi.cape.servicemanager.model.ServiceReport;
//import it.eng.opsi.cape.servicemanager.repository.ServiceEntryDAO;
//import it.eng.opsi.cape.servicemanager.service.ServiceRegistryService;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestConfiguration.class)
//
//public class ServiceRegistryServiceTest {
//
//	@Mock
//	ServiceEntryDAO dao;
//
//	@Mock
//	ServiceEntry serviceEntry;
//	
//	@Mock
//	ServiceReport serviceReport;
//
//	@Autowired
//	@InjectMocks
//	@Qualifier("ServiceRegistryService")
//
//	ServiceRegistryService registryService;
//
//	@Before
//	public void setUp() {
//		MockitoAnnotations.initMocks(this);
//	}
//
//	@Test
//	public void testSaveService() {
//
//		System.out.println("testing saveService");
//
//		// Mocking ServiceEntry methods
//		when(serviceEntry.getPublicServiceID()).thenReturn("1234");
//		when(serviceEntry.getPublicServiceName()).thenReturn("myService");
//		when(serviceEntry.getPublicServiceKeyword()).thenReturn("aaaaa");
//
//		// Mocking database methods
//		when(dao.create(any(ServiceEntry.class))).thenReturn(serviceEntry);
//
//		// Create sample ServiceEntry
//		ServiceEntry service = new ServiceEntry();
//		service.setPublicServiceID("1234");
//		service.setPublicServiceName("myService");
//		service.setPublicServiceKeyword("aaaaa");
//		service.setServiceUri("http://aaaa.it");
//		service.setOrganizationId("OrganizationId");
//		service.setServiceProviderId("ProviderId");
//		service.setPublicServiceStatus("active");
//		service.setServiceDescriptionVersion("v1.0");
//		service.setTechnicalDescriptionId("11111");
//		service.getPublicServiceLanguage().add("EN");
//		service.getPublicServiceSector().add("Public");
//		HumanReadableDescription hd = new HumanReadableDescription();
//		hd.setLocale("EN");
//		hd.setDescription("description");
//		service.getHumanReadableDescription().add(hd);
//
//		// Call create method
//		ServiceEntry result = registryService.create(service);
//
//		// Assert expected results
//		Assert.assertNotNull(result);
//		Assert.assertTrue(service.getPublicServiceName().equalsIgnoreCase(result.getPublicServiceName()));
//		Assert.assertTrue(service.getPublicServiceKeyword().equalsIgnoreCase(result.getPublicServiceKeyword()));
//
//	}
//
//	@Test
//	public void updateTest() {
//
//		System.out.println("testing updateService");
//
//		// Mocking serviceEntry methods
//		when(serviceEntry.getPublicServiceID()).thenReturn("1234");
//		when(serviceEntry.getPublicServiceName()).thenReturn("myService");
//		when(serviceEntry.getPublicServiceKeyword()).thenReturn("aaaaa");
//
//		// Create sample ServiceEntry
//		ServiceEntry service = new ServiceEntry();
//		service.setPublicServiceID("1234");
//		service.setPublicServiceName("myService");
//
//		service.setPublicServiceKeyword("aaaaa");
//		service.setServiceUri("http://aaaa.it");
//
//		// Call create method
//		ServiceEntry result = registryService.update(service, "1234");
//
//		// Assert expected results
//		Assert.assertNotNull(result);
//		Assert.assertTrue(service.getPublicServiceName().equalsIgnoreCase(result.getPublicServiceName()));
//		Assert.assertTrue(service.getPublicServiceKeyword().equalsIgnoreCase(result.getPublicServiceKeyword()));
//	}
//
//	@Test
//	public void getMappingTest() {
//
//		System.out.println("testing getMappingService");
//		DataMapping mapping = new DataMapping();
//		mapping.setConceptId("conceptId");
//		mapping.setName("aConcept");
//		mapping.setProperty("aConceptProperty");
//		mapping.setType("type");
//
//		List<DataMapping> mappings = new ArrayList<DataMapping>();
//		mappings.add(mapping);
//
//		// Mocking database methods
//		when(dao.getDataMapping(Mockito.anyString())).thenReturn(mappings);
//
//		// Call create method
//		List<DataMapping> result = registryService.getServiceDataMapping("1234");
//
//		System.out.println(result.toString());
//		// Assert expected results
//		Assert.assertNotNull(result);
//		Assert.assertNotNull(result.get(0).getConceptId());
//		Assert.assertNotNull(result.get(0).getName());
//		Assert.assertNotNull(result.get(0).getProperty());
//		Assert.assertNotNull(result.get(0).getType());
//
//	}
//
//	@Test
//	public void getServiceTest() {
//		System.out.println("testing getService");
//
//		// Mocking ServiceEntry methods
//		when(serviceEntry.getPublicServiceID()).thenReturn("1234");
//		when(serviceEntry.getPublicServiceName()).thenReturn("myService");
//		when(serviceEntry.getPublicServiceKeyword()).thenReturn("aaaaa");
//
//		// Mocking database methods
//		when(dao.findById(Mockito.anyString())).thenReturn(serviceEntry);
//
//		// Call create method
//		ServiceEntry result = registryService.findById("1234");
//
//		// Assert expected results
//		Assert.assertNotNull(result);
//		Assert.assertNotNull(result.getPublicServiceName());
//		Assert.assertNotNull(result.getPublicServiceName());
//		Assert.assertNotNull(result.getPublicServiceKeyword());
//
//	}
//
//	@Test
//	public void getServicesTest() {
//		System.out.println("testing getServices");
//
//		// Mocking ServiceEntry methods
//		when(serviceEntry.getPublicServiceID()).thenReturn("1234");
//		when(serviceEntry.getPublicServiceName()).thenReturn("myService");
//		when(serviceEntry.getPublicServiceKeyword()).thenReturn("aaaaa");
//
//		List<ServiceEntry> entries = new ArrayList<ServiceEntry>();
//		entries.add(serviceEntry);
//		// Mocking database methods
//		when(dao.findAll()).thenReturn(entries);
//
//		// Call create method
//		List<ServiceEntry> result = registryService.getServices();
//
//		// Assert expected results
//		Assert.assertNotNull(result);
//		Assert.assertTrue(result.size() == 1);
//		Assert.assertNotNull(result.get(0).getPublicServiceName());
//		Assert.assertNotNull(result.get(0).getPublicServiceName());
//		Assert.assertNotNull(result.get(0).getPublicServiceKeyword());
//
//	}
//
//	@Test
//	public void getServicesByNameTest() {
//		System.out.println("testing getServicesByName");
//
//		// Mocking ServiceEntry methods
//		when(serviceEntry.getPublicServiceID()).thenReturn("1234");
//		when(serviceEntry.getPublicServiceName()).thenReturn("myService");
//		when(serviceEntry.getPublicServiceKeyword()).thenReturn("aaaaa");
//
//		List<ServiceEntry> entries = new ArrayList<ServiceEntry>();
//		entries.add(serviceEntry);
//		// Mocking database methods
//		when(dao.findByName(Mockito.anyString())).thenReturn(entries);
//
//		// Call create method
//		List<ServiceEntry> result = registryService.findServicesByName("");
//		// Assert expected results
//		Assert.assertNotNull(result);
//		Assert.assertTrue(result.size() == 1);
//		Assert.assertNotNull(result.get(0).getPublicServiceName());
//		Assert.assertNotNull(result.get(0).getPublicServiceName());
//		Assert.assertNotNull(result.get(0).getPublicServiceKeyword());
//
//	}
//	
//	@Test
//	public void getServicesByUrlTest() {
//		System.out.println("testing getServicesByUrl");
//
//		// Mocking ServiceEntry methods
//		when(serviceEntry.getPublicServiceID()).thenReturn("1234");
//		when(serviceEntry.getPublicServiceName()).thenReturn("myService");
//		when(serviceEntry.getPublicServiceKeyword()).thenReturn("aaaaa");
//
//		// Mocking database methods
//		when(dao.findByURL(Mockito.anyString())).thenReturn(serviceEntry);
//
//		// Call create method
//		ServiceEntry result = registryService.findServiceByUrl("");
//		// Assert expected results
//		Assert.assertNotNull(result);
//		Assert.assertNotNull(result.getPublicServiceName());
//		Assert.assertNotNull(result.getPublicServiceName());
//		Assert.assertNotNull(result.getPublicServiceKeyword());
//
//	}
//	
//	@Test
//	public void getServiceReportByTypeTest() {
//		System.out.println("testing getServiceReportByType");
//
//		// Mocking ServiceEntry methods
//		when(serviceReport.getCount()).thenReturn(4);
//		when(serviceReport.getCategory()).thenReturn("type");
//		
//
//		List<ServiceReport> entries = new ArrayList<ServiceReport>();
//		entries.add(serviceReport);
//		// Mocking database methods
//		when(dao.getServiceReportbyType()).thenReturn(entries);
//
//		// Call create method
//		List<ServiceReport> result = registryService.getServiceReportbyType();
//		// Assert expected results
//		Assert.assertNotNull(result);
//		Assert.assertTrue(result.size() == 1);
//		Assert.assertNotNull(result.get(0).getCategory());
//		Assert.assertNotNull(result.get(0).getCount());
//		
//	}
//	
//	@Test
//	public void getServiceReportBySectorTest() {
//		System.out.println("testing getServiceReportBySector");
//
//		// Mocking ServiceEntry methods
//		when(serviceReport.getCount()).thenReturn(4);
//		when(serviceReport.getCategory()).thenReturn("sector");
//		
//
//		List<ServiceReport> entries = new ArrayList<ServiceReport>();
//		entries.add(serviceReport);
//		// Mocking database methods
//		when(dao.getServiceReportbySector()).thenReturn(entries);
//
//		// Call create method
//		List<ServiceReport> result = registryService.getServiceReportbySector();
//		// Assert expected results
//		Assert.assertNotNull(result);
//		Assert.assertTrue(result.size() == 1);
//		Assert.assertNotNull(result.get(0).getCategory());
//		Assert.assertNotNull(result.get(0).getCount());
//		
//	}
//	
//
//	@Test
//	public void removeServicesTest() {
//		System.out.println("testing removeService");
//
//		registryService.remove("");
//
//	}
//
//}
