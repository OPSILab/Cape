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
package it.eng.opsi.cape.consentmanager.notarization.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction extends BasicTransactionInfo {

	@NonNull
	private Integer blockNumber;

	@NonNull
	private Date timestamp;

	private String fromAddress;

	private String toAddress;

	@NonNull
	private Double transactionFee;

	@NonNull
	private Integer gasLimit;

	@NonNull
	private Integer gasUsedByTransaction;

	@NonNull
	private Double gasPrice;

	@NonNull
	private Integer nonce;

	@NonNull
	private String inputData;

	@NonNull
	private Double transactionFeeInEth;

	@NonNull
	private Integer etherValue;

	@NonNull
	private Integer txStatus;

}
