/*******************************************************************************
 * CaPe - a Consent Based Personal Data Suite
 *  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.opsi.cape.sdk.service;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.*;
import com.nimbusds.jose.util.Base64URL;

import it.eng.opsi.cape.exception.DataRequestNotValid;
import it.eng.opsi.cape.sdk.model.ServicePopKey;
import it.eng.opsi.cape.sdk.model.ServiceSignKey;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSignedPair;
import it.eng.opsi.cape.sdk.model.consenting.ConsentRecordSourceRoleSpecificPart;
import it.eng.opsi.cape.sdk.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.sdk.model.datatransfer.AuthorisationTokenPayload;
import it.eng.opsi.cape.sdk.model.datatransfer.DataRequestAuthorizationPayload;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordAccountSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordDoubleSigned.ServiceLinkRecordSignature;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry;

@Service
public class CryptoService {

	@Autowired
	private ObjectMapper payloadMapper;

	public RSAKey generateRSAKey() throws JOSEException {

		// Generate 2048-bit RSA key pair in JWK format, attach some metadata
		return new RSAKeyGenerator(2048).algorithm(new Algorithm("RS256")).keyUse(KeyUse.SIGNATURE) // indicate the
																									// intended use of
																									// the key
				.keyID(UUID.randomUUID().toString()) // give the key a unique ID
				.generate();

	}

	/*
	 * Build an self-signed X509 Certificate for issuer, subject and RSA key pair in
	 * input
	 */
	public X509Certificate buildX509Certificate(String issuerName, String subjectName, RSAKey rsaJWK)
			throws JOSEException {

		PrivateKey privateKey = rsaJWK.toPrivateKey();
		PublicKey publicKey = rsaJWK.toPublicKey();

		BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
		Date startDate = new Date(System.currentTimeMillis());
		Date expiryDate = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 100));
		X500Name issuer = new X500Name("CN=" + issuerName);
		X500Name subject = new X500Name("CN=" + subjectName);

		X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(issuer, serialNumber, startDate, expiryDate,
				subject, SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));

		ContentSigner signer;
		try {
			signer = new JcaContentSignerBuilder("SHA256withRSA").build(privateKey);

//			byte[] certBytes = certBuilder.build(signer).getEncoded();
//			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			X509CertificateHolder certHolder = certBuilder.build(signer);
			X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certHolder);

			return cert;

		} catch (Exception e) {

			throw new JOSEException(
					"Error while creating X509 certificate for issuer: " + issuerName + " and subject: " + subjectName);
		}

	}

	public String pemEncodeX509Certificate(X509Certificate certificate) throws JOSEException {

		StringWriter sw = new StringWriter();

		try (PemWriter pw = new PemWriter(sw)) {
			PemObjectGenerator gen = new JcaMiscPEMGenerator(certificate);
			pw.writeObject(gen);
		} catch (IOException e) {
			throw new JOSEException("There was an error while PEM encoding certificate");
		}

		return sw.toString();
	}

	public Boolean verifyAccountSignedSLR(ServiceLinkRecordAccountSigned accountSignedSlr)
			throws ParseException, JOSEException, JsonProcessingException {

		/*
		 * JWS Unprotected Header (should contain alg and kid)
		 */
		JWSHeader unprotectedSlrHeader = accountSignedSlr.getHeader();

		/*
		 * JWS Protected header (should contain alg, kid and jwk)
		 */
		JWSHeader protectedSlrHeader = JWSHeader.parse(accountSignedSlr.get_protected());

		/*
		 * Check if alg and kid coming from protected and unprotected JWS Headers match
		 */
		if (unprotectedSlrHeader.getAlgorithm().equals(protectedSlrHeader.getAlgorithm())
				&& unprotectedSlrHeader.getKeyID().equals(protectedSlrHeader.getKeyID())) {

			/*
			 * Create the object to be signed with protected Header and Json serialized SLR
			 * payload
			 */
			Base64URL payload = new Payload(payloadMapper.writeValueAsString(accountSignedSlr.getPayload()))
					.toBase64URL();
			JWSObject jwsObject = new JWSObject(accountSignedSlr.get_protected(), payload,
					accountSignedSlr.getSignature());

			RSAKey accountRSAKey = (RSAKey) protectedSlrHeader.getJWK();

			return jwsObject.verify(new RSASSAVerifier(accountRSAKey));

		} else
			throw new JOSEException("Protected and unprotected header fields don't match");

	}

	public ServiceLinkRecordDoubleSigned signSLRPayload(ServiceSignKey serviceSignKey,
			ServiceLinkRecordAccountSigned accountSignedSlr) throws JsonProcessingException, JOSEException {

		/*
		 * Get Public part of Service key pair to be put into the header of resulting
		 * JWS
		 */
		RSAKey serviceKeyPair = serviceSignKey.getJwk();
		RSAKey servicePublicKey = serviceKeyPair.toPublicJWK();

		/*
		 * Sign the the partial SLR and return new Account signed SLR (payload + JWS
		 * Headers (protected as BASE64URL and unprotected) + Signature)
		 */

		JWSHeader protectedHeader = new JWSHeader.Builder(JWSAlgorithm.parse(servicePublicKey.getAlgorithm().getName()))
				.keyID(servicePublicKey.getKeyID()).jwk(servicePublicKey).build();

		JWSHeader unprotectedHeader = new JWSHeader.Builder(
				JWSAlgorithm.parse(servicePublicKey.getAlgorithm().getName())).keyID(servicePublicKey.getKeyID())
						.build();

		/*
		 * Create the object to be signed with protected Header and Json serialized SLR
		 * payload
		 */
		JWSObject jwsObject = new JWSObject(protectedHeader,
				new Payload(payloadMapper.writeValueAsString(accountSignedSlr.getPayload())));

		/*
		 * RSA Sign the JWS Object with the Service private Key
		 */
		jwsObject.sign(new RSASSASigner(serviceKeyPair));

		/*
		 * Get the new Service signature and the Account signature and insert them in
		 * the Signatures List of Double Signed SLR
		 */
		ServiceLinkRecordDoubleSigned doubleSignedSlr = new ServiceLinkRecordDoubleSigned();

		doubleSignedSlr.setPayload(accountSignedSlr.getPayload());

		ServiceLinkRecordSignature serviceSignature = new ServiceLinkRecordSignature(unprotectedHeader,
				protectedHeader.toBase64URL(), jwsObject.getSignature());
		ServiceLinkRecordSignature accountSignature = new ServiceLinkRecordSignature(accountSignedSlr.getHeader(),
				accountSignedSlr.get_protected(), accountSignedSlr.getSignature());

		doubleSignedSlr.setSignatures(Arrays.asList(accountSignature, serviceSignature));

		return doubleSignedSlr;

	}

	public Base64URL signServiceDescription(ServiceSignKey serviceSignKey, ServiceEntry serviceDescription)
			throws JsonProcessingException, JOSEException {

		/*
		 * Get Public part of Service key pair to be put into the header of resulting
		 * JWS
		 */
		RSAKey serviceKeyPair = serviceSignKey.getJwk();
		RSAKey servicePublicKey = serviceKeyPair.toPublicJWK();

		/*
		 * Sign the the partial SLR and return new Account signed SLR (payload + JWS
		 * Headers (protected as BASE64URL and unprotected) + Signature)
		 */

		JWSHeader protectedHeader = new JWSHeader.Builder(JWSAlgorithm.parse(servicePublicKey.getAlgorithm().getName()))
				.keyID(servicePublicKey.getKeyID()).jwk(servicePublicKey).build();

		JWSHeader unprotectedHeader = new JWSHeader.Builder(
				JWSAlgorithm.parse(servicePublicKey.getAlgorithm().getName())).keyID(servicePublicKey.getKeyID())
						.build();

		/*
		 * Create the object to be signed with protected Header and Json serialized
		 * Service Description
		 */

		JWSObject jwsObject = new JWSObject(protectedHeader,
				new Payload(payloadMapper.writeValueAsString(serviceDescription)));

		/*
		 * RSA Sign the JWS Object with the Service private Key
		 */
		jwsObject.sign(new RSASSASigner(serviceKeyPair));

		return jwsObject.getSignature();

	}

	public String signAndSerializeDataRequestAuthorization(ServicePopKey popKey,
			DataRequestAuthorizationPayload authPayload) throws JsonProcessingException, JOSEException {

		/*
		 * Get Public part of Service Proof of Possession key pair to be put into the
		 * header of resulting JWS
		 */
		RSAKey popKeyPair = popKey.getJwk();
		RSAKey popPublicKey = popKeyPair.toPublicJWK();

		/*
		 * Sign the the partial SLR and return new Account signed SLR (payload + JWS
		 * Headers (protected as BASE64URL and unprotected) + Signature)
		 */

		JWSHeader protectedHeader = new JWSHeader.Builder(JWSAlgorithm.parse(popPublicKey.getAlgorithm().getName()))
				.keyID(popPublicKey.getKeyID()).build();

		/*
		 * Create the object to be signed with protected Header and Json serialized
		 * Service Description
		 */

		JWSObject jwsObject = new JWSObject(protectedHeader,
				new Payload(payloadMapper.writeValueAsString(authPayload)));

		/*
		 * RSA Sign the JWS Object with the Service private Key
		 */
		jwsObject.sign(new RSASSASigner(popKeyPair));

		return jwsObject.serialize();

	}

	public Boolean verifyConsentRecordSigned(ConsentRecordSigned signedConsentRecord, RSAKey accountPublicKey)
			throws ParseException, JOSEException, JsonProcessingException {

		/*
		 * JWS Unprotected Header (should contain alg and kid)
		 */
		JWSHeader unprotectedSlrHeader = signedConsentRecord.getHeader();

		/*
		 * JWS Protected header (should contain alg, kid and jwk)
		 */
		JWSHeader protectedSlrHeader = JWSHeader.parse(signedConsentRecord.get_protected());

		/*
		 * Check if alg and kid coming from protected and unprotected JWS Headers match
		 */
		if (unprotectedSlrHeader.getAlgorithm().equals(protectedSlrHeader.getAlgorithm())
				&& unprotectedSlrHeader.getKeyID().equals(protectedSlrHeader.getKeyID())) {

			/*
			 * Create the object to be signed with protected Header and Json serialized SLR
			 * payload
			 */
			Base64URL payload = new Payload(payloadMapper.writeValueAsString(signedConsentRecord.getPayload()))
					.toBase64URL();
			JWSObject jwsObject = new JWSObject(signedConsentRecord.get_protected(), payload,
					signedConsentRecord.getSignature());

//			RSAKey accountRSAKey = (RSAKey) protectedSlrHeader.getJWK();

			return jwsObject.verify(new RSASSAVerifier(accountPublicKey));

		} else
			throw new JOSEException("Protected and unprotected header fields don't match");

	}

	public Boolean verifyConsentStatusRecordSigned(ConsentStatusRecordSigned signedConsentStatusRecord,
			RSAKey accountPublicKey) throws ParseException, JOSEException, JsonProcessingException {

		/*
		 * JWS Unprotected Header (should contain alg and kid)
		 */
		JWSHeader unprotectedSlrHeader = signedConsentStatusRecord.getHeader();

		/*
		 * JWS Protected header (should contain alg, kid and jwk)
		 */
		JWSHeader protectedSlrHeader = JWSHeader.parse(signedConsentStatusRecord.get_protected());

		/*
		 * Check if alg and kid coming from protected and unprotected JWS Headers match
		 */
		if (unprotectedSlrHeader.getAlgorithm().equals(protectedSlrHeader.getAlgorithm())
				&& unprotectedSlrHeader.getKeyID().equals(protectedSlrHeader.getKeyID())) {

			/*
			 * Create the object to be signed with protected Header and Json serialized SLR
			 * payload
			 */
			Base64URL payload = new Payload(payloadMapper.writeValueAsString(signedConsentStatusRecord.getPayload()))
					.toBase64URL();
			JWSObject jwsObject = new JWSObject(signedConsentStatusRecord.get_protected(), payload,
					signedConsentStatusRecord.getSignature());

//			RSAKey accountRSAKey = (RSAKey) protectedSlrHeader.getJWK();

			return jwsObject.verify(new RSASSAVerifier(accountPublicKey));

		} else
			throw new JOSEException("Protected and unprotected header fields don't match");

	}

	public Boolean verifyServiceLinkStatusRecordSigned(ServiceLinkStatusRecordSigned signedSsr)
			throws JOSEException, ParseException, JsonProcessingException {

		/*
		 * JWS Unprotected Header (should contain alg and kid)
		 */
		JWSHeader unprotectedSlrHeader = signedSsr.getHeader();

		/*
		 * JWS Protected header (should contain alg, kid and jwk)
		 */
		JWSHeader protectedSlrHeader = JWSHeader.parse(signedSsr.get_protected());

		/*
		 * Check if alg and kid coming from protected and unprotected JWS Headers match
		 */
		if (unprotectedSlrHeader.getAlgorithm().equals(protectedSlrHeader.getAlgorithm())
				&& unprotectedSlrHeader.getKeyID().equals(protectedSlrHeader.getKeyID())) {

			/*
			 * Create the object to be signed with protected Header and Json serialized SLR
			 * payload
			 */
			Base64URL payload = new Payload(payloadMapper.writeValueAsString(signedSsr.getPayload())).toBase64URL();
			JWSObject jwsObject = new JWSObject(signedSsr.get_protected(), payload, signedSsr.getSignature());

			RSAKey accountRSAKey = (RSAKey) protectedSlrHeader.getJWK();

			return jwsObject.verify(new RSASSAVerifier(accountRSAKey));

		} else
			throw new JOSEException("Protected and unprotected header fields don't match");

	}

	public AuthorisationTokenPayload extractPayloadFromSerializedAuthToken(String serializedToken)
			throws ParseException, JsonMappingException, JsonProcessingException {

		JWSObject object = JWSObject.parse(serializedToken);

		return payloadMapper.readValue(object.getPayload().toString(), AuthorisationTokenPayload.class);

	}

	public Boolean verifyAuthorisationToken(String serializedToken, ConsentRecordSignedPair consentPair,
			ZonedDateTime dataRequestTime)
			throws JOSEException, ParseException, JsonProcessingException, DataRequestNotValid {

		/*
		 * Parse JWS object from serialized Token string
		 */
		JWSObject jws = JWSObject.parse(serializedToken);

		/*
		 * JWS Header (should contain alg and kid)
		 */
		JWSHeader jwsHeader = jws.getHeader();

		ConsentRecordSourceRoleSpecificPart sourceSpecificPart = ((ConsentRecordSourceRoleSpecificPart) consentPair
				.getSource().getPayload().getRoleSpecificPart());

		/*
		 * Check if there is alg and kid matches with the one of Operator Key
		 */
		if (jwsHeader.getAlgorithm() == null || StringUtils.isBlank(jwsHeader.getKeyID())
				|| !jwsHeader.getKeyID().equals(sourceSpecificPart.getTokenIssuerKey().getKeyID()))
			throw new JOSEException("Alg or kid header not found or kid does not match with the operator Key");

		/*
		 * Verify JWS object with operator Key contained in the token issuer key field
		 * of Consent Record Source specific part
		 */
		Boolean signatureVerify = jws.verify(new RSASSAVerifier(sourceSpecificPart.getTokenIssuerKey()));

		/*
		 * Verify Authorisation Token expiration, Cr Id, etc..
		 */
		AuthorisationTokenPayload authToken = payloadMapper.readValue(jws.getPayload().toString(),
				AuthorisationTokenPayload.class);

		if (!authToken.getCr_id().equals(consentPair.getSink().getPayload().getCommonPart().getCrId()))
			throw new DataRequestNotValid(
					"The Cr Id in the Authorisation Token does not match the one of the Data Request");

		if (dataRequestTime.isAfter(authToken.getExp()))
			throw new DataRequestNotValid("The Authorisation Token is expired, get a new one from Consent Manager");

		return signatureVerify;

	}

	public Boolean verifyDataRequest(String serializedDataRequest, ConsentRecordSignedPair consentPair)
			throws JOSEException, ParseException, JsonProcessingException, DataRequestNotValid {

		/*
		 * Parse JWS object from serialized Token string
		 */
		JWSObject jws = JWSObject.parse(serializedDataRequest);

		/*
		 * JWS Header (should contain alg and kid)
		 */
		JWSHeader jwsHeader = jws.getHeader();

		ConsentRecordSourceRoleSpecificPart sourceSpecificPart = ((ConsentRecordSourceRoleSpecificPart) consentPair
				.getSource().getPayload().getRoleSpecificPart());

		/*
		 * Check if there is alg and kid matches with the one of Operator Key
		 */
		if (jwsHeader.getAlgorithm() == null || StringUtils.isBlank(jwsHeader.getKeyID())
				|| !jwsHeader.getKeyID().equals(sourceSpecificPart.getPopKey().getJwk().getKeyID()))
			throw new JOSEException("Alg or kid header not found or kid does not match with the Pop Key");

		/*
		 * Verify Authorization Token contained in the JWS payload
		 */
		DataRequestAuthorizationPayload dataRequestPayload = payloadMapper.readValue(jws.getPayload().toString(),
				DataRequestAuthorizationPayload.class);
		String serializedToken = dataRequestPayload.getAt();
		ZonedDateTime dataRequestTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(dataRequestPayload.getTs()),
				ZoneId.of("UTC"));

		if (!verifyAuthorisationToken(serializedToken, consentPair, dataRequestTime))
			throw new DataRequestNotValid("The Authorisation Token signature is not valid");

		/*
		 * Verify parsed JWS object with operator KEy contained in the token issuer key
		 * field of Consent Record Source specific part
		 */
		return jws.verify(new RSASSAVerifier(sourceSpecificPart.getPopKey().getJwk()));

	}

}
