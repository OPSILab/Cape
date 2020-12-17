package it.eng.opsi.cape.sdk.repository;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import com.nimbusds.jose.jwk.RSAKey;

public class RSAKeyWriteConverter implements Converter<RSAKey, Document> {

	@Override
	public Document convert(RSAKey source) {

		Document result = new Document();

		result.append("n", source.getModulus().toString());
		result.append("e", source.getPublicExponent().toString());

		if (source.getPrivateExponent() != null)
			result.append("d", source.getPrivateExponent().toString());

		if (source.getFirstPrimeFactor() != null)
			result.append("p", source.getFirstPrimeFactor().toString());

		if (source.getSecondPrimeFactor() != null)
			result.append("q", source.getSecondPrimeFactor().toString());

		if (source.getFirstFactorCRTExponent() != null)
			result.append("dp", source.getFirstFactorCRTExponent().toString());

		if (source.getSecondFactorCRTExponent() != null)
			result.append("dq", source.getSecondFactorCRTExponent().toString());

		if (source.getFirstCRTCoefficient() != null)
			result.append("qi", source.getFirstCRTCoefficient().toString());
		
		result.append("kty", source.getKeyType().getValue());
		result.append("use", source.getKeyUse().getValue());
		result.append("alg", source.getAlgorithm().getName());
		result.append("kid", source.getKeyID());

		return result;
	}

}
