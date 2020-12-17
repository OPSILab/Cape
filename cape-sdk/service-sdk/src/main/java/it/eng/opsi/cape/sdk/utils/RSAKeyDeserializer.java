package it.eng.opsi.cape.sdk.utils;

import java.io.IOException;
import java.text.ParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nimbusds.jose.jwk.RSAKey;


public class RSAKeyDeserializer extends StdDeserializer<RSAKey> {

	  public RSAKeyDeserializer() { 
	        this(null); 
	    } 
	 
	    public RSAKeyDeserializer(Class<?> vc) { 
	        super(vc); 
	    }

		@Override
		public RSAKey deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IllegalArgumentException, IOException {
			JsonNode node = jp.getCodec().readTree(jp);
			RSAKey result;
			try {
				result = RSAKey.parse(node.toString());
			} catch (Exception e) {
				
				e.printStackTrace();
				throw new IllegalArgumentException("Error while deserializing RSAKey");
			}
			
			return result;
			
		}
	
	
}
