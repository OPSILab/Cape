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
package it.eng.opsi.cape.servicemanager.utils;

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
