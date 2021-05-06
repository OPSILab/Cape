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
package it.eng.opsi.cape.exception;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import it.eng.opsi.cape.consentmanager.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
//@JsonSubTypes({ @Type(value = ErrorResponse.class, name = "ErrorResponse") })
public class RestTemplateException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1776267319024707859L;
	private HttpStatus statusCode;
	private String error;
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private ZonedDateTime timestamp;

	private ErrorResponse innerError = null;
	private List<? extends ApiSubError> subErrors;

	/** URI that has been called */
	private String path;

	private RestTemplateException() {
		super();
		timestamp = ZonedDateTime.now(ZoneId.of("UTC"));
	}

	public RestTemplateException(String message) {
		super(message);
		timestamp = ZonedDateTime.now(ZoneId.of("UTC"));
	}

	public RestTemplateException(HttpStatus status) {
		this();
		this.statusCode = status;
	}

	public RestTemplateException(HttpStatus status, Throwable error, String path) {
		this(error.getMessage());
		this.statusCode = status;
		this.error = error.getClass().getName();
		this.path = path;
	}

//	ErrorResponse(HttpStatus status, Throwable error, List<ErrorResponse> innerErrors) {
//		this(error.getMessage());
//		this.status = status;
//		this.error = error.getClass().getName();
//		this.innerErrors.addAll(innerErrors);
//	}

	public RestTemplateException(HttpStatus status, Throwable error, ErrorResponse innerError) {
		this(error.getMessage());
		this.statusCode = status;
		this.error = error.getClass().getName();
		this.innerError = innerError;
	}

	public RestTemplateException(HttpStatus status, Throwable error, ErrorResponse innerError, String path) {
		this(error.getMessage());
		this.statusCode = status;
		this.error = error.getClass().getName();
		this.innerError = innerError;
		this.path = path;
	}
	public RestTemplateException(HttpStatus status, String message) {
		this(message);
		this.statusCode = status;
	}

	public RestTemplateException(HttpStatus status, String message, Throwable error) {
		this(message);
		this.statusCode = status;
		this.error = error.getClass().getName();

	}

	public RestTemplateException(HttpStatus status, String message, List<? extends ApiSubError> subErrors,
			Throwable error) {
		this(message);
		this.statusCode = status;
		this.subErrors = subErrors;
		this.error = error.getClass().getName();

	}

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
	@JsonSubTypes({ @Type(value = ApiValidationError.class, name = "ApiValidationError") })
	public static abstract class ApiSubError {

	}

	@Data
	@EqualsAndHashCode(callSuper = false)
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ApiValidationError extends ApiSubError {
		private String object;
		private String field;
		private Object rejectedValue;
		private String message;

		@JsonCreator(mode = JsonCreator.Mode.DEFAULT)
		ApiValidationError(String object, String message) {
			this.object = object;
			this.message = message;
		}
	}

}
