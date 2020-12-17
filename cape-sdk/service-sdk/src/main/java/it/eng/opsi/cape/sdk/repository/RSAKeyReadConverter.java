package it.eng.opsi.cape.sdk.repository;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import com.nimbusds.jose.jwk.RSAKey;

public class RSAKeyReadConverter implements Converter<Document, RSAKey> {

	@Override
	public RSAKey convert(Document source) {

		RSAKey result;
		try {
			result = RSAKey.parse(source.toJson().toString());
		} catch (Exception e) {

			e.printStackTrace();
			throw new IllegalArgumentException("Error while deserializing RSAKey from DB");
		}

		return result;
	}

}
