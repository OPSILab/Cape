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
package it.eng.opsi.cape.sdk.repository;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import com.nimbusds.jose.JWSHeader;

public class JWSHeaderWriteConverter implements Converter<JWSHeader, Document> {

	@Override
	public Document convert(JWSHeader source) {

		source.toJSONObject();

		return Document.parse(source.toJSONObject().toJSONString());
//		Document result = new Document();

//		result.append("n", source.getModulus().toString());
//		result.append("e", source.getPublicExponent().toString());
//
//		if (source.getPrivateExponent() != null)
//			result.append("d", source.getPrivateExponent().toString());
//
//		if (source.getFirstPrimeFactor() != null)
//			result.append("p", source.getFirstPrimeFactor().toString());
//
//		if (source.getSecondPrimeFactor() != null)
//			result.append("q", source.getSecondPrimeFactor().toString());
//
//		if (source.getFirstFactorCRTExponent() != null)
//			result.append("dp", source.getFirstFactorCRTExponent().toString());
//
//		if (source.getSecondFactorCRTExponent() != null)
//			result.append("dq", source.getSecondFactorCRTExponent().toString());
//
//		if (source.getFirstCRTCoefficient() != null)
//			result.append("qi", source.getFirstCRTCoefficient().toString());
//		
//		result.append("kty", source.getKeyType().getValue());
//		result.append("use", source.getKeyUse().getValue());
//		result.append("alg", source.getAlgorithm().getName());
//		result.append("kid", source.getKeyID());

//		return result;
	}

}
