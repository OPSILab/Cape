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
package it.eng.opsi.cape.auditlogmanager;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import io.swagger.v3.oas.annotations.media.Schema;
import it.eng.opsi.cape.exception.RestTemplateException;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
public class ErrorResponse {

	/** HTTP Status Code */
	private int status;

	/** HTTP Reason phrase */
	private String error;

	/** A message that describe the error thrown when calling the downstream API */
	private String message;

	/** URI that has been called */
	private String path;

	private ZonedDateTime timestamp;
	
	@Schema(implementation = ErrorResponse.class, name = "innerError", type = "object", description = "Error Response object")
	private ErrorResponse innerError = null;
	private List<? extends ApiSubError> subErrors;

	private ErrorResponse() {
		super();
		timestamp = ZonedDateTime.now(ZoneId.of("UTC"));
	}

	ErrorResponse(int status) {
		this();
		this.status = status;
	}

	public ErrorResponse(RestTemplateException ex, String path) {
		this();
		this.status = ex.getStatusCode().value();
		this.error = ex.getClass().getName();
		this.message = ex.getError();
		this.path = path;
	}

	public ErrorResponse(HttpStatus status, Throwable ex) {
		this();
		this.status = status.value();
		this.error = ex.getClass().getName();
		this.message = ex.getMessage();
	}

	public ErrorResponse(HttpStatus status, Throwable ex, String path) {
		this();
		this.status = status.value();
		this.error = ex.getClass().getName();
		this.message = ex.getMessage();
		this.path = path;
	}

	public ErrorResponse(HttpStatus status, Throwable ex, ErrorResponse innerError) {
		this();
		this.status = status.value();
		this.error = ex.getClass().getName();
		this.message = ex.getMessage();
		this.innerError = innerError;
	}

	public ErrorResponse(HttpStatus status, Throwable ex, ErrorResponse innerError, String path) {
		this();
		this.status = status.value();
		this.error = ex.getClass().getName();
		this.message = ex.getMessage();
		this.innerError = innerError;
		this.path = path;
	}


	ErrorResponse(HttpStatus status, Throwable ex, List<? extends ApiSubError> subErrors) {
		this();
		this.status = status.value();
		this.error = ex.getClass().getName();
		this.message = ex.getMessage();
		this.subErrors = subErrors;
	}

	ErrorResponse(HttpStatus status, Throwable ex, List<? extends ApiSubError> subErrors, String path) {
		this();
		this.status = status.value();
		this.error = ex.getClass().getName();
		this.message = ex.getMessage();
		this.subErrors = subErrors;
		this.path = path;
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
