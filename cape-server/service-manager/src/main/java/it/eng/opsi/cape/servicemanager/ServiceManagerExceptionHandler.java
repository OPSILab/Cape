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

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.ConflictingSessionFoundException;
import it.eng.opsi.cape.exception.DataOperatorDescriptionNotFoundException;
import it.eng.opsi.cape.exception.RestTemplateException;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceDescriptionStatusNotAllowedException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkStatusConflictingException;
import it.eng.opsi.cape.exception.ServiceLinkingRedirectUriMismatchException;
import it.eng.opsi.cape.exception.SessionNotFoundException;
import it.eng.opsi.cape.exception.SessionStateNotAllowedException;
import it.eng.opsi.cape.servicemanager.ErrorResponse.ApiSubError;
import it.eng.opsi.cape.servicemanager.ErrorResponse.ApiValidationError;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ServiceManagerExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex);
		return ResponseEntity.status(error.getStatus()).contentType(MediaType.APPLICATION_JSON).body(error);

	}

	@Override
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String message = "Malformed JSON request";
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex);
		return ResponseEntity.status(error.getStatus()).contentType(MediaType.APPLICATION_JSON).body(error);
	}

	@ExceptionHandler(DataOperatorDescriptionNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleOperatorNotFound(DataOperatorDescriptionNotFoundException ex,
			HttpServletRequest req) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex, req.getRequestURI()));
	}

	@ExceptionHandler(ServiceDescriptionNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleServiceDescripitionNotFound(ServiceDescriptionNotFoundException ex,
			HttpServletRequest req) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex, req.getRequestURI()));
	}

	@ExceptionHandler(SessionNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleSessionNotFound(SessionNotFoundException ex, HttpServletRequest req) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex, req.getRequestURI()));
	}

	@ExceptionHandler(ServiceLinkRecordNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleSlrNotFound(ServiceLinkRecordNotFoundException ex,
			HttpServletRequest req) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex, req.getRequestURI()));
	}

	@ExceptionHandler(AccountNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex, HttpServletRequest req) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex, req.getRequestURI()));
	}

	@ExceptionHandler(ConflictingSessionFoundException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	protected ResponseEntity<ErrorResponse> handleConflictingSessionFound(ConflictingSessionFoundException ex,
			HttpServletRequest req) {

//		return buildResponseEntity(new ErrorResponse(HttpStatus.CONFLICT,
//				ex.getMessage() + " - Current State: " + ex.getCurrentState(), ex));
		return buildResponseEntity(new ErrorResponse(HttpStatus.CONFLICT, ex, req.getRequestURI()));

	}

	@ExceptionHandler(ServiceLinkStatusConflictingException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	protected ResponseEntity<ErrorResponse> handleConflictingSessionFound(ServiceLinkStatusConflictingException ex,
			HttpServletRequest req) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.CONFLICT, ex, req.getRequestURI()));
	}

	@ExceptionHandler(ServiceDescriptionStatusNotAllowedException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	protected ResponseEntity<ErrorResponse> handleConflictingSessionFound(
			ServiceDescriptionStatusNotAllowedException ex, HttpServletRequest req) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.CONFLICT, ex, req.getRequestURI()));
	}

	@ExceptionHandler(ServiceLinkingRedirectUriMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<ErrorResponse> ServiceLinkingRedirectUriMismatchException(
			ServiceLinkingRedirectUriMismatchException ex, HttpServletRequest req) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, ex, req.getRequestURI()));
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

	@Override
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {

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
	protected ResponseEntity<ErrorResponse> handleIllegalArgumentError(IllegalArgumentException ex,
			HttpServletRequest req) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, ex, req.getRequestURI()));
	}

	@ExceptionHandler(DuplicateKeyException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<ErrorResponse> handleDuplicateKeyError(DuplicateKeyException ex, HttpServletRequest req) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, ex, req.getRequestURI()));
	}

	@ExceptionHandler(SessionStateNotAllowedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<ErrorResponse> handleSessionStateNotAllowed(SessionStateNotAllowedException ex,
			HttpServletRequest req) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, ex, req.getRequestURI()));
	}

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
