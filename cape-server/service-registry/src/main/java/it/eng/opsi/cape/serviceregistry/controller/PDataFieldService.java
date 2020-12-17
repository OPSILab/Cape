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
//package it.eng.opsi.cape.serviceregistry.controller;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//import org.springframework.stereotype.Service;
//
//import it.eng.opsi.cape.serviceregistry.data.PDataCategory;
//import it.eng.opsi.cape.serviceregistry.data.PDataField;
//import it.eng.opsi.cape.serviceregistry.model.CapeProperty;
//import it.eng.opsi.cape.serviceregistry.repository.PDataFieldDAO;
//import it.eng.opsi.cape.serviceregistry.repository.ServiceEntryDAO;
//import it.eng.opsi.cape.serviceregistry.utils.DAOUtils;
//import it.eng.opsi.cape.serviceregistry.utils.PropertyManager;
//
//@Service("PDataFieldService")
//
//@Path("/v1")
//public class PDataFieldService implements IPDataFieldService {
//	static final String api_version = "1.0";
//	
//	
//	
//private static String collectionName = null;
//	
//	PDataFieldDAO dao = new PDataFieldDAO(collectionName);
//	
//	static {
//		collectionName = PropertyManager.getProperty(CapeProperty.PDATA_COLLECTION_NAME);
//	}
//
//	
//
//	@GET
//	@Path("/pdatafields")
//	@Produces(MediaType.APPLICATION_JSON)
//	public List<PDataField> getPDataFields() {
//		return dao.findAll();
//		
//	}
//
//	@GET
//	@Path("/pdatafields/{id}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public PDataField getPDataFieldById(
//			@PathParam("id") String id) {
//		return dao.findById(id);
//	}
//
//	@GET
//	@Path("/pdatafields/search/")
//	@Produces(MediaType.APPLICATION_JSON)
//	public List<PDataField> findPDataFieldByName(
//			@QueryParam("regex") String regex) {
//		return dao.findByName(regex);
//	}
//
//	@GET
//	@Path("/pdatafields/category/{category}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public List<PDataField> getPDataFieldByCategory(
//			@PathParam("category") String category) {
//		return dao.findByCategory(category);
//
//	}
//
//	@GET
//	@Path("/pdatafields/category/tree")
//	@Produces(MediaType.APPLICATION_JSON)
//	public List<PDataCategory> getPDataCategoryTree() {
//		return dao.getPDataTree();
//
//	}
//
//}
