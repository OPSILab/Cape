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
package it.eng.opsi.cape.sdk.utils;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nimbusds.jose.JWSHeader;


public class JWSHeaderDeserializer extends StdDeserializer<JWSHeader> {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
