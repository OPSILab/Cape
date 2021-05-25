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
package it.eng.opsi.cape.servicemanager.utils;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nimbusds.jose.jwk.RSAKey;

public class RSAKeySerializer extends StdSerializer<RSAKey> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
