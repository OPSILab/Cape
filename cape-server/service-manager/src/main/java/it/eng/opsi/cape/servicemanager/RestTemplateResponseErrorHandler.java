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
package it.eng.opsi.cape.servicemanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.eng.opsi.cape.exception.RestTemplateException;

@Service
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

	private ApplicationContext applicationContext;

	@Autowired
	public RestTemplateResponseErrorHandler(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public RestTemplateResponseErrorHandler() {
	}

	@Override
	public boolean hasError(ClientHttpResponse httpResponse) throws IOException {

		return (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR
				|| httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR);
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {

	}

	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse httpResponse) throws IOException {

		InputStream bodyStream = httpResponse.getBody();
		String body = new BufferedReader(new InputStreamReader(bodyStream)).lines().collect(Collectors.joining("\n"));

		ObjectMapper mapper = applicationContext.getBean(ObjectMapper.class);
//
//		if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
//
//			throw new IOException("There was an error while calling external API/URI: " + body);
//
//		} else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {

		Exception error = null;
		ErrorResponse errorResponse = null;
		try {

			if (httpResponse.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
				errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED,
						new Exception("Invalid token: access token is invalid"));
			else
				errorResponse = mapper.readValue(body, ErrorResponse.class);

			Class<?> clazz = Class.forName(errorResponse.getError());
			if (!clazz.getName().equals("org.apache.http.conn.HttpHostConnectException")) {

				Constructor<?> constructor = clazz.getConstructor(String.class);
				error = (Exception) constructor.newInstance(errorResponse.getMessage());
			} else {
				error = new ResourceAccessException(errorResponse.getMessage());
			}

		} catch (ClassNotFoundException | SecurityException | IllegalArgumentException | NoSuchMethodException
				| InvocationTargetException | InstantiationException | IllegalAccessException e) {

			// Unknown Cause class throw generic Exception with message from Response body
			e.printStackTrace();
			error = new Exception(errorResponse.getMessage());
		} catch (JsonProcessingException e) {

			// Unable to map to ErrorResponse, unknown Response body from Rest Client, throw
			// generic Exception with generic message (created by default case in the switch
			// below)
			e.printStackTrace();

			error = new Exception(body);
			throw new RestTemplateException(HttpStatus.INTERNAL_SERVER_ERROR, error, errorResponse,
					url.getPath());
		}

		throw new RestTemplateException(HttpStatus.valueOf(errorResponse.getStatus()), error, errorResponse,
				errorResponse.getPath());

//		switch (httpResponse.getStatusCode()) {
//
//		case NOT_FOUND:
//			throw new RestClientException("The external API/URI returned 404 NOT FOUND", cause);
//		case BAD_REQUEST:
//			throw new RestClientException("The external API/URI returned 400 BAD REQUEST", cause);
//		case CONFLICT:
//			throw new RestClientException("The external API/URI returned 409 CONFLICT", cause);
//		default:
//			throw new RestClientException("The external API/URI returned " + httpResponse.getStatusCode(), cause);
//		}

	}

}
