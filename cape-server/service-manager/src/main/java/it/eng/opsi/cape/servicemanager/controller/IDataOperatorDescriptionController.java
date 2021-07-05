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
package it.eng.opsi.cape.servicemanager.controller;

import java.io.IOException;

import org.bouncycastle.cert.CertIOException;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;

import it.eng.opsi.cape.exception.DataOperatorDescriptionNotFoundException;
import it.eng.opsi.cape.servicemanager.model.DataOperatorDescription;
import it.eng.opsi.cape.servicemanager.model.consenting.AuthorisationTokenPayload;
import it.eng.opsi.cape.servicemanager.model.consenting.AuthorisationTokenResponse;

public interface IDataOperatorDescriptionController {

	public abstract ResponseEntity<DataOperatorDescription> createDataOperatorDescription(DataOperatorDescription description)
			throws JOSEException, CertIOException, IOException;

	public abstract ResponseEntity<DataOperatorDescription> getDataOperatorDescription(String operatorId)
			throws DataOperatorDescriptionNotFoundException;

	public abstract ResponseEntity<String> deleteDataOperatorDescription(String operatorId)
			throws DataOperatorDescriptionNotFoundException;

	public abstract ResponseEntity<AuthorisationTokenResponse> signAuthorisationToken(String dataOperatorId,
			AuthorisationTokenPayload payload) throws DataOperatorDescriptionNotFoundException, JsonProcessingException, JOSEException;

}
