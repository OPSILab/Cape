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
package it.eng.opsi.cape.servicemanager.controller;

import java.io.IOException;

import org.bouncycastle.cert.CertIOException;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;

import it.eng.opsi.cape.exception.OperatorDescriptionNotFoundException;
import it.eng.opsi.cape.servicemanager.model.OperatorDescription;
import it.eng.opsi.cape.servicemanager.model.consenting.AuthorisationTokenPayload;
import it.eng.opsi.cape.servicemanager.model.consenting.AuthorisationTokenResponse;

public interface IOperatorDescriptionController {

	public abstract ResponseEntity<OperatorDescription> createOperatorDescription(OperatorDescription description)
			throws JOSEException, CertIOException, IOException;

	public abstract ResponseEntity<OperatorDescription> getOperatorDescription(String operatorId)
			throws OperatorDescriptionNotFoundException;

	public abstract ResponseEntity<String> deleteOperatorDescription(String operatorId)
			throws OperatorDescriptionNotFoundException;

	public abstract ResponseEntity<AuthorisationTokenResponse> signAuthorisationToken(String operatorId,
			AuthorisationTokenPayload payload) throws OperatorDescriptionNotFoundException, JsonProcessingException, JOSEException;

}
