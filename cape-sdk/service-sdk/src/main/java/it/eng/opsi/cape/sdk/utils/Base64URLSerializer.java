package it.eng.opsi.cape.sdk.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;

public class Base64URLSerializer extends StdSerializer<Base64URL> {

	public Base64URLSerializer() {
		this(null);
	}

	public Base64URLSerializer(Class<Base64URL> t) {
		super(t);
	}

	// Serializing RSAKey, remove any private information (private key of the pair)
	@Override
	public void serialize(Base64URL value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

		jgen.writeString(value.toString());
	}

}
