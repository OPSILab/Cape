/*******************************************************************************
 * CaPe - a Consent Based Personal Data Suite
 *  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.opsi.cape.sdk;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.eng.opsi.cape.exception.OperatorDescriptionNotFoundException;
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
		String message = "Malformed JSON request";
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, message, ex);
		return ResponseEntity.status(error.getStatus()).contentType(MediaType.APPLICATION_JSON).body(error);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleServiceDescripitionNotFound(ResourceNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getCause()));
	}
	
	@ExceptionHandler(UserSurrogateIdLinkNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleOperatorNotFound(UserSurrogateIdLinkNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
	}
	
	@ExceptionHandler(OperatorDescriptionNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleOperatorNotFound(OperatorDescriptionNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
	}

	@ExceptionHandler(ServiceDescriptionNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleServiceDescripitionNotFound(ServiceDescriptionNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
	}

	@ExceptionHandler(ServiceLinkRecordNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleServiceDescripitionNotFound(ServiceLinkRecordNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
	}

	@ExceptionHandler(ServiceSignKeyNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleServiceDescripitionNotFound(ServiceSignKeyNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
	}

	@ExceptionHandler(SessionNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleSessionNotFound(SessionNotFoundException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
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
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), subErrors, ex);
		return ResponseEntity.status(error.getStatus()).contentType(MediaType.APPLICATION_JSON).body(error);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<ErrorResponse> handleIllegalArgumentError(IllegalArgumentException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
	}

	@ExceptionHandler(DuplicateKeyException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<ErrorResponse> handleDuplicateKeyError(DuplicateKeyException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
	}

	@ExceptionHandler(SessionStateNotAllowedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<ErrorResponse> handleSessionStateNotAllowed(SessionStateNotAllowedException ex) {

		return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
	}

	@ExceptionHandler(value = Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected ResponseEntity<ErrorResponse> handleGenericError(Exception ex) {

		if (ex.getCause() instanceof IOException) {

			String message = ex.getMessage();
			if (message.contains("401 UNAUTHORIZED"))
				return buildResponseEntity(new ErrorResponse(HttpStatus.UNAUTHORIZED, message, ex));
			if (message.contains("404 NOT FOUND"))
				return buildResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND,
						ex.getCause().getCause().getMessage(), ex.getCause().getCause()));
			if (message.contains("400 BAD REQUEST"))
				return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST,
						ex.getCause().getCause().getMessage(), ex.getCause().getCause()));
			if (message.contains("409 CONFLICT"))
				return buildResponseEntity(new ErrorResponse(HttpStatus.CONFLICT,
						ex.getCause().getCause().getMessage(), ex.getCause().getCause()));
			
		}

		ex.printStackTrace();
		return buildResponseEntity(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
	}

	private ResponseEntity<ErrorResponse> buildResponseEntity(ErrorResponse error) {
		return ResponseEntity.status(error.getStatus()).contentType(MediaType.APPLICATION_JSON).body(error);
	}

}
