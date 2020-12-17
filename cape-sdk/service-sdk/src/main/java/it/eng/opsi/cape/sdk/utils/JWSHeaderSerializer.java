package it.eng.opsi.cape.sdk.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.RSAKey;

public class JWSHeaderSerializer extends StdSerializer<JWSHeader> {

	public JWSHeaderSerializer() {
		this(null);
	}

	public JWSHeaderSerializer(Class<JWSHeader> t) {
		super(t);
	}

	// Serializing RSAKey, remove any private information (private key of the pair)
	@Override
	public void serialize(JWSHeader header, JsonGenerator jgen, SerializerProvider provider) throws IOException {

		jgen.writeStartObject();

		if (header.getAlgorithm() != null)
			jgen.writeStringField("alg", header.getAlgorithm().getName());

		Optional.ofNullable(header.getKeyID()).ifPresent((value) -> {
			try {
				jgen.writeStringField("kid", value);
			} catch (IOException e) {
				e.printStackTrace();
				throw new UncheckedIOException(e);
			}
		});

		jgen.writeEndObject();

	}

}
