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
package it.eng.opsi.cape.accountmanager.model.linking;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.jwk.RSAKey;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ServicePopKey {

	@Getter
	@Setter
	@NotBlank(message = "operator_id field is mandatory")
	@JsonProperty(value = "operator_id")
	private String operatorId;

	@Getter
	@Setter
	@NotBlank(message = "service_id field is mandatory")
	@JsonProperty(value = "service_id")
	private String serviceId;

	@Getter
	@Setter
	@NotBlank(message = "surrogate_id field is mandatory")
	@JsonProperty(value = "surrogate_id")
	private String surrogateId;

	@Getter
	@Setter
	@NotNull(message = "jwk field is mandatory")
	@JsonProperty(value = "jwk")
	private RSAKey jwk;

//
//	public void setJwk(RSAKey jwk) {
//		this.jwk = jwk;
//	}
//
//	
//	public RSAKey getJwk() {
//		return jwk;
//	}

}
