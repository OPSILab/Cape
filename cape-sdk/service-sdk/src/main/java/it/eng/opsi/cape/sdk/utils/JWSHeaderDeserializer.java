package it.eng.opsi.cape.sdk.utils;

import java.io.IOException;
import java.text.ParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.RSAKey;


public class JWSHeaderDeserializer extends StdDeserializer<JWSHeader> {

	  public JWSHeaderDeserializer() { 
	        this(null); 
	    } 
	 
	    public JWSHeaderDeserializer(Class<?> vc) { 
	        super(vc); 
	    }

		@Override
		public JWSHeader deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IllegalArgumentException, IOException {
			JsonNode node = jp.getCodec().readTree(jp);
			JWSHeader result;
			try {
				result = JWSHeader.parse(node.toString());
			} catch (Exception e) {
				
				e.printStackTrace();
				throw new IllegalArgumentException("Error while deserializing JWSHeader");
			}
			
			return result;
			
		}
	
	
}
