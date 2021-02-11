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
package it.eng.opsi.cape.accountmanager;

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
			if (body.contains("Invalid token: access token is invalid"))
				error = new ErrorResponse(HttpStatus.UNAUTHORIZED, body, new Exception("Invalid token: access token is invalid"));
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
