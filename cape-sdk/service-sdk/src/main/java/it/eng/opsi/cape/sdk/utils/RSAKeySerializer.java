package it.eng.opsi.cape.sdk.utils;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nimbusds.jose.jwk.RSAKey;

public class RSAKeySerializer extends StdSerializer<RSAKey> {

	public RSAKeySerializer() {
		this(null);
	}

	public RSAKeySerializer(Class<RSAKey> t) {
		super(t);
	}

	// Serializing RSAKey, remove any private information (private key of the pair)
	@Override
	public void serialize(RSAKey value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

		jgen.writeStartObject();
		RSAKey publicKey = value.toPublicJWK();
		jgen.writeStringField("n", publicKey.getModulus().toString());
		jgen.writeStringField("e", publicKey.getPublicExponent().toString());

		if (publicKey.getKeyType() != null)
			jgen.writeObjectField("kty", publicKey.getKeyType().getValue());

		if (publicKey.getKeyUse() != null)
			jgen.writeObjectField("use", publicKey.getKeyUse().getValue());

		if (publicKey.getAlgorithm() != null)
			jgen.writeObjectField("alg", publicKey.getAlgorithm().getName());
		
		jgen.writeStringField("kid", publicKey.getKeyID());
		jgen.writeEndObject();

	}

}
