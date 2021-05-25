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
package it.eng.opsi.cape.sdk;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import it.eng.opsi.cape.exception.OperatorDescriptionNotFoundException;
import it.eng.opsi.cape.exception.RestTemplateException;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.exception.ServiceSignKeyNotFoundException;
import it.eng.opsi.cape.exception.SessionNotFoundException;
import it.eng.opsi.cape.exception.SessionStateNotAllowedException;
import it.eng.opsi.cape.exception.UserSurrogateIdLinkNotFoundException;
import it.eng.opsi.cape.sdk.ErrorResponse.ApiSubError;
import it.eng.opsi.cape.sdk.ErrorResponse.ApiValidationError;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class CapeServiceSdkExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex);
		return ResponseEntity.status(error.getStatus()).contentType(MediaType.APPLICATION_JSON).body(error);
	}

	@ExceptionHandler(UserSurrogateIdLinkNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleOperatorNotFound(UserSurrogateIdLinkNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex));
	}

	@ExceptionHandler(OperatorDescriptionNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleOperatorNotFound(OperatorDescriptionNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex));
	}

	@ExceptionHandler(ServiceDescriptionNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleServiceDescripitionNotFound(ServiceDescriptionNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex));
	}

	@ExceptionHandler(ServiceLinkRecordNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleServiceDescripitionNotFound(ServiceLinkRecordNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex));
	}

	@ExceptionHandler(ServiceSignKeyNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleServiceDescripitionNotFound(ServiceSignKeyNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex));
	}

	@ExceptionHandler(SessionNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleSessionNotFound(SessionNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex));
	}

	@Override
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<ApiSubError> subErrors = new ArrayList<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			FieldError fieldError = ((FieldError) error);
			subErrors.add(new ApiValidationError(fieldError.getObjectName(), fieldError.getField(),
					fieldError.getRejectedValue(), fieldError.getDefaultMessage()));
		});
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex, subErrors);
		return ResponseEntity.status(error.getStatus()).contentType(MediaType.APPLICATION_JSON).body(error);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<ErrorResponse> handleIllegalArgumentError(IllegalArgumentException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, ex));
	}

	@ExceptionHandler(DuplicateKeyException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<ErrorResponse> handleDuplicateKeyError(DuplicateKeyException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, ex));
	}

	@ExceptionHandler(SessionStateNotAllowedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<ErrorResponse> handleSessionStateNotAllowed(SessionStateNotAllowedException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, ex));
	}

//	@ExceptionHandler(value = Exception.class)
//	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//	protected ResponseEntity<ErrorResponse> handleGenericError(Exception ex) {
//
//		ex.printStackTrace();
//		return buildResponseEntity(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex));
//	}

	@ExceptionHandler(value = Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected ResponseEntity<ErrorResponse> handleErrorResponseFromRestClient(Exception ex, HttpServletRequest req) {

		if (ex instanceof RestTemplateException) {

			ex.printStackTrace();
			return buildResponseEntity(new ErrorResponse(((RestTemplateException) ex).getStatusCode(), ex,
					((RestTemplateException) ex).getInnerError(), req.getRequestURI()));

		}

		ex.printStackTrace();
		return buildResponseEntity(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, req.getRequestURI()));

	}

	private ResponseEntity<ErrorResponse> buildResponseEntity(ErrorResponse error) {
		return ResponseEntity.status(error.getStatus()).contentType(MediaType.APPLICATION_JSON).body(error);
	}

}
