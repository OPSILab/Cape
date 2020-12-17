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
package it.eng.opsi.cape.accountmanager.repository;

import java.util.Optional;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;

public class Base64URLWriteConverter implements Converter<Base64URL, String> {

	@Override
	public String convert(Base64URL source) {

		return source.toString();
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
