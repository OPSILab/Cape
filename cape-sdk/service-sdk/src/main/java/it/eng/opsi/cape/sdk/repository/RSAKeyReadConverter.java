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
