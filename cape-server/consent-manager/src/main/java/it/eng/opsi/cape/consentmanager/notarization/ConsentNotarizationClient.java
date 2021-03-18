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
//import java.io.ByteArrayInputStream;
//
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//import org.glassfish.jersey.media.multipart.MultiPart;
//import org.glassfish.jersey.media.multipart.MultiPartFeature;
//import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
//
//import it.eng.opsi.cape.consentmanager.model.CapeProperty;
//import it.eng.opsi.cape.consentmanager.model.ConsentRecord;
//import it.eng.opsi.cape.consentmanager.notarization.model.Asset;
//import it.eng.opsi.cape.consentmanager.notarization.model.AuthenticateRequest;
//import it.eng.opsi.cape.consentmanager.notarization.model.AuthenticateResponse;
//import it.eng.opsi.cape.consentmanager.notarization.model.Transaction;
//import it.eng.opsi.cape.consentmanager.utils.PropertyManager;
//
//public class ConsentNotarizationClient {
//
//	private static String notarizationHost = PropertyManager.getProperty(CapeProperty.CONSENT_NOTARIZATION_HOST);
//
//	public static <T extends ConsentRecord> Response callAddAssetAsDocument(T asset, String authToken) {
//
//		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
//		WebTarget webTarget = client.target(notarizationHost + "/api/OfficeDocument");
//		MultiPart multiPart = new MultiPart();
//		multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
//
//		StreamDataBodyPart streamDataBodyPart = new StreamDataBodyPart("FileContents",
//				new ByteArrayInputStream(asset.toString().getBytes()));
//		multiPart.bodyPart(streamDataBodyPart);
//
//		Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
//				.header("Authorization", "Bearer " + authToken)
//				.post(Entity.entity(multiPart, multiPart.getMediaType()));
//
//		return response;
//
//	}
//
//	public static <T extends ConsentRecord> Asset callAddAssetAsString(T asset, String authToken) {
//
//		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
//		WebTarget webTarget = client.target(notarizationHost + "/api/Information");
//
//		Asset response = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
//				.header("Authorization", "Bearer " + authToken)
//				.post(Entity.json("{\"informationText\":\"" + asset.getConsentSignedToken() + "\"}"), Asset.class);
//
//		return response;
//
//	}
//
//	public static Response callSetAssetForNotarization(Integer assetId, String authToken) {
//
//		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
//		WebTarget webTarget = client
//				.target(notarizationHost + "/api/AssetNotarization/setAssetReadyForNotarization/{assetId}")
//				.resolveTemplate("assetId", assetId);
//
//		Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
//				.header("Authorization", "Bearer " + authToken).get();
//
//		return response;
//
//	}
//
//	public static Boolean callCheckAssetNotarization(String assetId, String authToken) {
//
//		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
//		WebTarget webTarget = client.target(notarizationHost + "/api/AssetNotarization/check/{assetId}")
//				.resolveTemplate("assetId", assetId);
//
//		Boolean response = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
//				.header("Authorization", "Bearer " + authToken).get(Boolean.class);
//
//		return response;
//	}
//
//	public static Asset callGetAssetByAssetId(Integer assetId, String authToken) {
//
//		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
//		WebTarget webTarget = client.target(notarizationHost + "/api/Information/{assetId}").resolveTemplate("assetId",
//				assetId);
//
//		Asset response = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
//				.header("Authorization", "Bearer " + authToken).get(Asset.class);
//
//		return response;
//
//	}
//
//	public static Transaction getTransactionByHash(String transactionHash, String authToken) {
//
//		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
//		WebTarget webTarget = client.target(notarizationHost + "/api/Transaction/hash/{transactionHash}")
//				.resolveTemplate("transactionHash", transactionHash);
//
//		Transaction response = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
//				.header("Authorization", "Bearer " + authToken).get(Transaction.class);
//
//		return response;
//	}
//
//	public static <T extends ConsentRecord> AuthenticateResponse callAuthenticate(String user, String password) {
//
//		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
//		WebTarget webTarget = client.target(notarizationHost + "/api/Users/authenticate");
//
//		AuthenticateResponse response = webTarget.request(MediaType.APPLICATION_JSON_TYPE).post(
//				Entity.entity(new AuthenticateRequest(user, password), MediaType.APPLICATION_JSON_TYPE),
//				AuthenticateResponse.class);
//
//		return response;
//
//	}
//
//}
