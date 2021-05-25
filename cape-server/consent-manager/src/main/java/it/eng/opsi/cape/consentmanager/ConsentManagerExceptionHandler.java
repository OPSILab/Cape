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
package it.eng.opsi.cape.consentmanager;

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

import it.eng.opsi.cape.consentmanager.ErrorResponse.ApiSubError;
import it.eng.opsi.cape.consentmanager.ErrorResponse.ApiValidationError;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.exception.ConsentRecordNotFoundException;
import it.eng.opsi.cape.exception.ConsentStatusNotValidException;
import it.eng.opsi.cape.exception.ResourceSetIdNotFoundException;
import it.eng.opsi.cape.exception.RestTemplateException;
import it.eng.opsi.cape.exception.ServiceDescriptionNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.exception.ServiceLinkStatusNotValidException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ConsentManagerExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex);
		return ResponseEntity.status(error.getStatus()).contentType(MediaType.APPLICATION_JSON).body(error);
	}

	@ExceptionHandler(ResourceSetIdNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleResourceSetIdNotFound(ResourceSetIdNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getCause()));
	}

	@ExceptionHandler(ConsentRecordNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleConsentRecordNotFound(ConsentRecordNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex));
	}

	@ExceptionHandler(ServiceLinkRecordNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleServiceLinkRecordNotFound(ServiceLinkRecordNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex));
	}

	@ExceptionHandler(AccountNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex));
	}

	@ExceptionHandler(ServiceDescriptionNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleServiceDescripitionNotFound(ServiceDescriptionNotFoundException ex) {

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

	@ExceptionHandler(value = IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<ErrorResponse> handleIllegalArgumentError(IllegalArgumentException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, ex));
	}

	@ExceptionHandler(value = ConsentStatusNotValidException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	protected ResponseEntity<ErrorResponse> handleConsentStatusNotValid(ConsentStatusNotValidException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.CONFLICT, ex));
	}

	@ExceptionHandler(value = ServiceLinkStatusNotValidException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	protected ResponseEntity<ErrorResponse> handleConsentStatusNotValid(ServiceLinkStatusNotValidException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.CONFLICT, ex));
	}

	@ExceptionHandler(DuplicateKeyException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<ErrorResponse> handleDuplicateKeyError(DuplicateKeyException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, ex));
	}

	@ExceptionHandler(value = Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected ResponseEntity<ErrorResponse> handleErrorResponseFromRestClient(Exception ex, HttpServletRequest req) {

		if (ex instanceof RestTemplateException) {

			ex.printStackTrace();
			return buildResponseEntity(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex,
					((RestTemplateException) ex).getInnerError(), req.getRequestURI()));

		}
		ex.printStackTrace();
		return buildResponseEntity(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, req.getRequestURI()));

	}

	private ResponseEntity<ErrorResponse> buildResponseEntity(ErrorResponse error) {
		return ResponseEntity.status(error.getStatus()).contentType(MediaType.APPLICATION_JSON).body(error);
	}

}
