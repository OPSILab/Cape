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
package it.eng.opsi.cape.servicemanager.service;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.X509CertUtils;

import it.eng.opsi.cape.exception.DataOperatorDescriptionNotFoundException;
import it.eng.opsi.cape.servicemanager.model.DataOperatorDescription;
import it.eng.opsi.cape.servicemanager.model.consenting.AuthorisationTokenPayload;
import it.eng.opsi.cape.servicemanager.model.consenting.AuthorisationTokenResponse;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkRecordDoubleSigned.ServiceLinkRecordSignature;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkStatusRecordPayload;
import it.eng.opsi.cape.servicemanager.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.servicemanager.repository.DataOperatorDescriptionRepository;
import it.eng.opsi.cape.serviceregistry.data.Cert;

@Service
public class CryptoService {

	@Autowired
	private ObjectMapper payloadMapper;

	@Autowired
	private DataOperatorDescriptionRepository operatorRepo;

	/*
	 * Create a new JWK Pair for Operator and a X509 certificate for Public Key and
	 * put all in DataOperatorDescription
	 */
	public DataOperatorDescription createOperatorKeyPairAndCert(DataOperatorDescription operator)
			throws JOSEException, CertIOException, IOException {

		String operatorId = operator.getOperatorId();

		// GENERATE THE PUBLIC/PRIVATE RSA KEY PAIR
		RSAKey rsaJWK = new RSAKeyGenerator(2048).keyUse(KeyUse.SIGNATURE).algorithm(new Algorithm("RS256"))
				.keyID(operator.getOperatorId()).generate();
		operator.setKeyPair(rsaJWK);

		// Generate X509 Certificate self-signed from generate RSA key pair
		X509Certificate operatorCertificate = buildX509Certificate(operatorId, operatorId, rsaJWK);

		// Set x5c field with the PEM encoded certificate
		Cert operatorCertField = new Cert();
		operatorCertField.getX5c().add(pemEncodeX509Certificate(operatorCertificate));
		operator.setCert(operatorCertField);

		return operator;

	}

	public RSAKey getKeyPairByOperatorId(String operatorId) throws DataOperatorDescriptionNotFoundException {

		return operatorRepo.getKeyPairByOperatorId(operatorId)
				.orElseThrow(() -> new DataOperatorDescriptionNotFoundException(
						"Operator or key pair for Operator id: " + operatorId + " was not found"));
	}

	/*
	 * Build an self-signed X509 Certificate for issuer, subject and RSA key pair in
	 * input
	 */
	public X509Certificate buildX509Certificate(String issuerName, String subjectName, RSAKey rsaJWK)
			throws JOSEException, CertIOException, IOException {

		PrivateKey privateKey = rsaJWK.toPrivateKey();
		PublicKey publicKey = rsaJWK.toPublicKey();

		BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
		Date startDate = new Date(System.currentTimeMillis());
		Date expiryDate = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 100));
		X500Name issuer = new X500Name("CN=" + issuerName);
		X500Name subject = new X500Name("CN=" + subjectName);

		X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(issuer, serialNumber, startDate, expiryDate,
				subject, SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));

		KeyUsage keyUsage = new KeyUsage(KeyUsage.nonRepudiation);
		certBuilder.addExtension(new Extension(Extension.keyUsage, true, new DEROctetString(keyUsage)));

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

	/*
	 * Get Operator Public key (JWK) from Operator X509 certificate
	 */
	public RSAKey getPublicKeyFromCertificate(DataOperatorDescription operator) throws JOSEException {

		/*
		 * Get the Certificate PEM encoding from x5c field TODO Alternatively GET the
		 * PEM Certificate from the URI in the x5u field
		 */

		X509Certificate cert = X509CertUtils.parse(operator.getCert().getX5c().get(0));

		if (!(cert.getPublicKey() instanceof RSAPublicKey)) {
			throw new JOSEException("The public key of the X.509 certificate is not RSA");
		}

		RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();

		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

			return new RSAKey.Builder(publicKey).keyUse(KeyUse.from(cert)).keyID(operator.getOperatorId())
					.algorithm(new Algorithm("RS256"))
					.x509CertChain(Collections.singletonList(Base64.encode(cert.getEncoded())))
					.x509CertSHA256Thumbprint(Base64URL.encode(sha256.digest(cert.getEncoded()))).build();
		} catch (NoSuchAlgorithmException e) {
			throw new JOSEException("Couldn't encode x5t parameter: " + e.getMessage(), e);
		} catch (CertificateEncodingException e) {
			throw new JOSEException("Couldn't encode x5c parameter: " + e.getMessage(), e);
		}

	}

	public Boolean verifyServiceSignature(ServiceLinkRecordDoubleSigned serviceSignedSlr, Base64 encodedServiceCert)
			throws JOSEException, JsonProcessingException, ParseException {

		/*
		 * Get the Service signature (2nd one) from double signed SLR
		 */
		ServiceLinkRecordSignature serviceSlrSignature = serviceSignedSlr.getSignatures().get(1);
		Base64URL signature = serviceSlrSignature.getSignature();
		JWSHeader unprotectedSlrHeader = serviceSlrSignature.getHeader();
		JWSHeader protectedSlrHeader = JWSHeader.parse(serviceSlrSignature.get_protected());

		/*
		 * Get Service Public Key from its X509 Certificatr
		 */
		// Parse X509 certificate from Base64-encoded string retrieved from URI
		X509Certificate cert = X509CertUtils.parse(encodedServiceCert.toString().getBytes());
		// Retrieve public key as RSA JWK
		RSAKey rsaPublicJWK = RSAKey.parse(cert);

		/*
		 * 
		 * 1. Verify kid field from Service X509 Cert matching with kid of the
		 * DoubleSignedSLR (in the JWS header) Issuer
		 * 
		 * 
		 * 
		 * 2. Check if alg and kid coming from protected and unprotected JWS Headers
		 * match
		 */
		if (unprotectedSlrHeader.getAlgorithm().equals(protectedSlrHeader.getAlgorithm())
				&& unprotectedSlrHeader.getKeyID().equals(protectedSlrHeader.getKeyID())) {

			// Verify Service (Double) Signed SLR with its Public Key
			// Retrieve public key as RSA JWK
			JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);

			// Construct JWS to be verified against serviceSignResponse signature
			String payloadString = payloadMapper.writeValueAsString(serviceSignedSlr.getPayload());

			Base64URL payload = new Payload(payloadString).toBase64URL();
			JWSObject jws = new JWSObject(serviceSlrSignature.get_protected(), payload, signature);

			return jws.verify(verifier);

		} else
			throw new JOSEException("Protected and unprotected header fields don't match");

	}

	public ServiceLinkStatusRecordSigned signSSR(RSAKey operatorKeyPair, ServiceLinkStatusRecordPayload ssrPayload)
			throws JsonProcessingException, JOSEException {

		/*
		 * Get Public part of Operator key pair to be put into the header of resulting
		 * JWS
		 */
		RSAKey operatorPublicKey = operatorKeyPair.toPublicJWK();

		/*
		 * Sign the the partial SLR and return new Account signed SLR (payload + JWS
		 * Headers (protected as BASE64URL and unprotected) + Signature)
		 */

		JWSHeader protectedHeader = new JWSHeader.Builder(JWSAlgorithm.parse(operatorKeyPair.getAlgorithm().getName()))
				.keyID(operatorKeyPair.getKeyID()).jwk(operatorPublicKey).build();

		JWSHeader unprotectedHeader = new JWSHeader.Builder(
				JWSAlgorithm.parse(operatorKeyPair.getAlgorithm().getName())).keyID(operatorKeyPair.getKeyID()).build();

		ServiceLinkStatusRecordSigned signedSsr = new ServiceLinkStatusRecordSigned();

		signedSsr.set_protected(protectedHeader.toBase64URL());
		signedSsr.setHeader(unprotectedHeader);

		/*
		 * Create the object to be signed with protected Header and Json serialized SLR
		 * payload
		 */
		JWSObject jwsObject = new JWSObject(protectedHeader, new Payload(payloadMapper.writeValueAsString(ssrPayload)));

		/*
		 * RSA Sign the JWS Object with the Account private Key
		 */
		jwsObject.sign(new RSASSASigner(operatorKeyPair));

		/*
		 * Get the signature and insert in the ServiceLinkStatusRecordSigned
		 * 
		 */
		signedSsr.setPayload(ssrPayload);
		signedSsr.setSignature(jwsObject.getSignature());

		return signedSsr;
	}

	public AuthorisationTokenResponse signAuthorisationToken(RSAKey operatorKeyPair,
			AuthorisationTokenPayload tokenPayload) throws JOSEException, JsonProcessingException {

		/*
		 * Get Public part of Account key pair to be put into the header of resulting
		 * JWS
		 */
//		RSAKey operatorPublicKey = operatorKeyPair.toPublicJWK();

		/*
		 * Sign the the Authorisation Token payload and return result (payload + JWS
		 * Header + Signature)
		 */

		JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.parse(operatorKeyPair.getAlgorithm().getName()))
				.keyID(operatorKeyPair.getKeyID()).type(JOSEObjectType.JWT).build();

		/*
		 * Create the object to be signed with protected Header and Json serialized SLR
		 * payload
		 */
		JWSObject jwsObject = new JWSObject(header, new Payload(payloadMapper.writeValueAsString(tokenPayload)));

		/*
		 * RSA Sign the JWS Object with the Account private Key
		 */
		jwsObject.sign(new RSASSASigner(operatorKeyPair));

		/*
		 * Get the signature and insert in the AuthorisationToken
		 * 
		 */
		return new AuthorisationTokenResponse(tokenPayload.getCr_id(), jwsObject.serialize());

	}

}
