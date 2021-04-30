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
package it.eng.opsi.cape.serviceregistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

	private ApplicationContext applicationContext;

	@Autowired
	public RestTemplateResponseErrorHandler(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public RestTemplateResponseErrorHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean hasError(ClientHttpResponse httpResponse) throws IOException {

		return (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR
				|| httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR);
	}

	@Override
	public void handleError(ClientHttpResponse httpResponse) throws IOException {

		InputStream bodyStream = httpResponse.getBody();
		String body = new BufferedReader(new InputStreamReader(bodyStream)).lines().collect(Collectors.joining("\n"));

		ObjectMapper mapper = applicationContext.getBean(ObjectMapper.class);

		if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {

			throw new IOException("There was an error while calling external API/URI: " + body);

		} else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
			ErrorResponse error = null;
			if (httpResponse.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
				error = new ErrorResponse(HttpStatus.UNAUTHORIZED, body,
						new Exception("Invalid token: access token is invalid"));
			else
				error = mapper.readValue(body, ErrorResponse.class);
			Exception cause = null;

			try {
				Class<?> clazz = Class.forName(error.getCause());
				Constructor<?> constructor = clazz.getConstructor(String.class);
				cause = (Exception) constructor.newInstance(error.getMessage());

			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException
					| SecurityException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				cause = new Exception(error.getMessage());
			}

			switch (httpResponse.getStatusCode()) {

			case NOT_FOUND:
				throw new IOException("The external API/URI returned 404 NOT FOUND", cause);
			case BAD_REQUEST:
				throw new IOException("The external API/URI returned 400 BAD REQUEST", cause);
			case CONFLICT:
				throw new IOException("The external API/URI returned 409 CONFLICT", cause);
			default:
				throw new IOException("There was an error with status: " + httpResponse.getStatusCode()
						+ " while calling external API/URI: " + body);
			}
		}
	}

}
