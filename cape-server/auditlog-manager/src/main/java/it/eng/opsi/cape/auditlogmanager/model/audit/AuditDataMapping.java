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
package it.eng.opsi.cape.auditlogmanager.model.audit;

import javax.validation.constraints.NotNull;

import it.eng.opsi.cape.serviceregistry.data.DataMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuditDataMapping extends DataMapping {

	@NotNull(message = "count is mandatory")
	private Integer count;

	public AuditDataMapping(String property, String conceptId, String name, DataMapping.Type type, Boolean required) {

		super(property, conceptId, name, type, required);
	}

	public AuditDataMapping(String conceptId) {

		super();
		this.setConceptId(conceptId);
	}

}
