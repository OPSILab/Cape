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
package it.eng.opsi.cape.servicemanager.model.linking;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChangeSlrStatusRequestFrom {

	SERVICE("Service"), OPERATOR("Operator");

	private String value;

	ChangeSlrStatusRequestFrom(String value) {
		this.value = value;
	}

	@JsonValue
	public String getText() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}
}
