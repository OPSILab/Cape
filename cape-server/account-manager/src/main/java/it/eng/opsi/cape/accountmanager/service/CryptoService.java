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
package it.eng.opsi.cape.accountmanager.service;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
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

import it.eng.opsi.cape.accountmanager.model.Account;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentRecordPayload;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentRecordRoleEnum;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentRecordRoleSpecificPart;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentRecordSigned;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentStatusRecordPayload;
import it.eng.opsi.cape.accountmanager.model.consenting.ConsentStatusRecordSigned;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordAccountSigned;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordDoubleSigned.ServiceLinkRecordSignature;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkRecordPayload;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkStatusRecordPayload;
import it.eng.opsi.cape.accountmanager.model.linking.ServiceLinkStatusRecordSigned;
import it.eng.opsi.cape.accountmanager.repository.AccountRepository;
import it.eng.opsi.cape.exception.AccountNotFoundException;
import it.eng.opsi.cape.serviceregistry.data.ServiceEntry.Role;
import it.eng.opsi.cape.accountmanager.ApplicationProperties;

@Service
public class CryptoService {

	private final ApplicationProperties appProperty;
	private final String operatorId;

	@Autowired
	private ObjectMapper payloadMapper;

	@Autowired
	AccountRepository accountRepo;

	@Autowired
	public CryptoService(ApplicationProperties appProperty) {
		this.appProperty = appProperty;
		this.operatorId = this.appProperty.getCape().getOperatorId();
	}

	/*
	 * Create a new JWK Pair for Account and put all into the Account
	 */
	public Account createAccountKeyPair(Account account) throws JOSEException {

		String accountId = account.get_id().toString();

		// GENERATE THE PUBLIC/PRIVATE RSA KEY PAIR
		RSAKey rsaJWK = new RSAKeyGenerator(2048).keyID(operatorId + ":" + accountId).keyUse(KeyUse.SIGNATURE)
				.algorithm(new Algorithm("RS256")).generate();
		account.setKeyPair(rsaJWK);

		// Generate X509 Certificate self-signed from generate RSA key pair
//		X509Certificate operatorCertificate = buildX509Certificate(operatorId, operatorId, rsaJWK);

		// Set x5c field with the PEM encoded certificate
//		Cert operatorCertField = new Cert();
//		operatorCertField.setX5c(pemEncodeX509Certificate(operatorCertificate));
//		operator.setCert(operatorCertField);

		return account;

	}

	public RSAKey getKeyPairByAccountId(String accountId) throws AccountNotFoundException {

		return accountRepo.getKeyPairBy_idOrUsername(accountId, accountId)
				.orElseThrow(() -> new AccountNotFoundException(
						"Account or key pair for account id: " + accountId + " was not found"));

	}

	public RSAKey getPublicKeyByAccountId(String accountId) throws AccountNotFoundException {

		return accountRepo.getKeyPairBy_idOrUsername(accountId, accountId)
				.orElseThrow(() -> new AccountNotFoundException(
						"Account or key pair for account id: " + accountId + " was not found"))
				.toPublicJWK();

	}

	public ServiceLinkRecordAccountSigned signPartialSLR(RSAKey accountKeyPair, ServiceLinkRecordPayload slrPayload)
			throws JsonProcessingException, JOSEException, AccountNotFoundException {

		/*
		 * Get Public part of Account key pair to be put into the header of resulting
		 * JWS
		 */
		RSAKey accountPublicKey = accountKeyPair.toPublicJWK();

		/*
		 * Sign the the partial SLR and return new Account signed SLR (payload + JWS
		 * Headers (protected as BASE64URL and unprotected) + Signature)
		 */

		JWSHeader protectedHeader = new JWSHeader.Builder(JWSAlgorithm.parse(accountKeyPair.getAlgorithm().getName()))
				.keyID(accountKeyPair.getKeyID()).jwk(accountPublicKey).build();

		JWSHeader unprotectedHeader = new JWSHeader.Builder(JWSAlgorithm.parse(accountKeyPair.getAlgorithm().getName()))
				.keyID(accountKeyPair.getKeyID()).build();

		ServiceLinkRecordAccountSigned accountSignedSlr = new ServiceLinkRecordAccountSigned();
		accountSignedSlr.setHeader(unprotectedHeader);
		accountSignedSlr.set_protected(protectedHeader.toBase64URL());

		/*
		 * Create the object to be signed with protected Header and Json serialized SLR
		 * payload
		 */
		JWSObject jwsObject = new JWSObject(protectedHeader, new Payload(payloadMapper.writeValueAsString(slrPayload)));

		/*
		 * RSA Sign the JWS Object with the Account private Key
		 */
		jwsObject.sign(new RSASSASigner(accountKeyPair));

		/*
		 * Get the signature and insert in the Account Signed SLR
		 */
		accountSignedSlr.setPayload(slrPayload);
		accountSignedSlr.setSignature(jwsObject.getSignature());

		return accountSignedSlr;
	}

	public ServiceLinkStatusRecordSigned signSSR(RSAKey accountKeyPair, ServiceLinkStatusRecordPayload ssrPayload)
			throws JsonProcessingException, JOSEException {

		/*
		 * Get Public part of Account key pair to be put into the header of resulting
		 * JWS
		 */
		RSAKey accountPublicKey = accountKeyPair.toPublicJWK();

		/*
		 * Sign the the partial SLR and return new Account signed SLR (payload + JWS
		 * Headers (protected as BASE64URL and unprotected) + Signature)
		 */

		JWSHeader protectedHeader = new JWSHeader.Builder(JWSAlgorithm.parse(accountKeyPair.getAlgorithm().getName()))
				.keyID(accountKeyPair.getKeyID()).jwk(accountPublicKey).build();

		JWSHeader unprotectedHeader = new JWSHeader.Builder(JWSAlgorithm.parse(accountKeyPair.getAlgorithm().getName()))
				.keyID(accountKeyPair.getKeyID()).build();

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
		jwsObject.sign(new RSASSASigner(accountKeyPair));

		/*
		 * Get the signature and insert in the ServiceLinkStatusRecordSigned
		 * 
		 */
		signedSsr.setPayload(ssrPayload);
		signedSsr.setSignature(jwsObject.getSignature());

		return signedSsr;
	}

	/**
	 * Sign the input Consent Record Payload with input Account RSAKey The
	 * ConsentRecordRoleSpecificPart and crId will be always left out of the
	 * signature in order to produce the same signature for both Sink and eventual
	 * Source Consent Record copies.
	 * 
	 * @param accountKeyPair
	 * @param crPayload
	 * @return
	 * @throws JsonProcessingException
	 * @throws JOSEException
	 */
	public ConsentRecordSigned signConsentRecord(RSAKey accountKeyPair, ConsentRecordPayload crPayload)
			throws JsonProcessingException, JOSEException {

		/*
		 * Get Public part of Account key pair to be put into the header of resulting
		 * JWS
		 */
		RSAKey accountPublicKey = accountKeyPair.toPublicJWK();

		/*
		 * Sign the the partial SLR and return new Account signed SLR (payload + JWS
		 * Headers (protected as BASE64URL and unprotected) + Signature)
		 */
		JWSHeader protectedHeader = new JWSHeader.Builder(JWSAlgorithm.parse(accountKeyPair.getAlgorithm().getName()))
				.keyID(accountKeyPair.getKeyID()).jwk(accountPublicKey).build();

		JWSHeader unprotectedHeader = new JWSHeader.Builder(JWSAlgorithm.parse(accountKeyPair.getAlgorithm().getName()))
				.keyID(accountKeyPair.getKeyID()).build();

		/*
		 * Create the object to be signed with protected Header and Json serialized SLR
		 * payload
		 * 
		 * (first put aside the consentRecordRoleSpecificPart and CrId, it will be not
		 * part of payload to be signed)
		 */

		ConsentRecordRoleSpecificPart roleSpecificPart = crPayload.getRoleSpecificPart();
		String crId = crPayload.getCommonPart().getCrId();

		crPayload.setRoleSpecificPart(null);
		crPayload.getCommonPart().setCrId("__"); // To not trigger NotBlank errors

		JWSObject jwsObject = new JWSObject(protectedHeader, new Payload(payloadMapper.writeValueAsString(crPayload)));

		/*
		 * RSA Sign the JWS Object with the Account private Key
		 */
		jwsObject.sign(new RSASSASigner(accountKeyPair));

		/*
		 * Get the signature and insert in the new ConsentRecordSigned
		 * 
		 */
		ConsentRecordSigned signedCr = new ConsentRecordSigned();
		signedCr.set_protected(protectedHeader.toBase64URL());
		signedCr.setHeader(unprotectedHeader);

		/*
		 * We reinsert as payload the input one, including the
		 * ConsentRecordRoleSpecificPart and CrId left out previously of the signing
		 * process
		 */
		crPayload.setRoleSpecificPart(roleSpecificPart);

		signedCr.setPayload(crPayload);
		signedCr.setSignature(jwsObject.getSignature());
		crPayload.getCommonPart().setCrId(crId);

		return signedCr;
	}

	public ConsentStatusRecordSigned signConsentStatusRecord(RSAKey accountKeyPair,
			ConsentStatusRecordPayload csrPayload) throws JsonProcessingException, JOSEException {

		/*
		 * Get Public part of Account key pair to be put into the header of resulting
		 * JWS
		 */
		RSAKey accountPublicKey = accountKeyPair.toPublicJWK();

		/*
		 * Sign the the partial SLR and return new Account signed SLR (payload + JWS
		 * Headers (protected as BASE64URL and unprotected) + Signature)
		 */

		JWSHeader protectedHeader = new JWSHeader.Builder(JWSAlgorithm.parse(accountKeyPair.getAlgorithm().getName()))
				.keyID(accountKeyPair.getKeyID()).jwk(accountPublicKey).build();

		JWSHeader unprotectedHeader = new JWSHeader.Builder(JWSAlgorithm.parse(accountKeyPair.getAlgorithm().getName()))
				.keyID(accountKeyPair.getKeyID()).build();

		/*
		 * Create the object to be signed with protected Header and Json serialized CSR
		 * payload
		 */
		JWSObject jwsObject = new JWSObject(protectedHeader, new Payload(payloadMapper.writeValueAsString(csrPayload)));

		/*
		 * RSA Sign the JWS Object with the Account private Key
		 */
		jwsObject.sign(new RSASSASigner(accountKeyPair));

		/*
		 * Get the signature and insert in the new ConsentStatusRecordSigned
		 * 
		 */
		ConsentStatusRecordSigned signedCsr = new ConsentStatusRecordSigned();

		signedCsr.set_protected(protectedHeader.toBase64URL());
		signedCsr.setHeader(unprotectedHeader);
		signedCsr.setPayload(csrPayload);
		signedCsr.setSignature(jwsObject.getSignature());

		return signedCsr;
	}

	public Base64URL signConsentStatusRecordsList(RSAKey accountKeyPair, List<ConsentStatusRecordSigned> csrList)
			throws JsonProcessingException, JOSEException {

		/*
		 * Get Public part of Account key pair to be put into the header of resulting
		 * JWS
		 */
		RSAKey accountPublicKey = accountKeyPair.toPublicJWK();

		/*
		 * Sign the the partial SLR and return new Account signed SLR (payload + JWS
		 * Headers (protected as BASE64URL and unprotected) + Signature)
		 */

		JWSHeader protectedHeader = new JWSHeader.Builder(JWSAlgorithm.parse(accountKeyPair.getAlgorithm().getName()))
				.keyID(accountKeyPair.getKeyID()).jwk(accountPublicKey).build();

//		JWSHeader unprotectedHeader = new JWSHeader.Builder(JWSAlgorithm.parse(accountKeyPair.getAlgorithm().getName()))
//				.keyID(accountKeyPair.getKeyID()).build();

		/*
		 * Create the object to be signed with protected Header and Json serialized SLR
		 * payload
		 */
		JWSObject jwsObject = new JWSObject(protectedHeader, new Payload(payloadMapper.writeValueAsString(csrList)));

		/*
		 * RSA Sign the JWS Object with the Account private Key
		 */
		jwsObject.sign(new RSASSASigner(accountKeyPair));

		/*
		 * Return the signature of input Consent Status Record (signed) list
		 */
		return jwsObject.getSignature();
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

	public Boolean verifyServiceSignature(ServiceLinkRecordDoubleSigned serviceSignedSlr, Base64 encodedServiceCert)
			throws JOSEException, JsonProcessingException, ParseException {
		// Parse X509 certificate from Base64-encoded string retrieved from URI
		X509Certificate cert = X509CertUtils.parse(encodedServiceCert.toString().getBytes());

		/*
		 * Verify kid field from Service X509 Cert matching with kid of the
		 * DoubleSignedSLR (in the JWS header) Issuer??
		 */

		// Retrieve public key as RSA JWK
		RSAKey rsaPublicJWK = RSAKey.parse(cert);

		// Get the Service signature (2nd one) from double signed SLR
		ServiceLinkRecordSignature serviceSlrSignature = serviceSignedSlr.getSignatures().get(1);
		Base64URL signature = serviceSlrSignature.getSignature();
		JWSHeader unprotectedSlrHeader = serviceSlrSignature.getHeader();
		JWSHeader protectedSlrHeader = JWSHeader.parse(serviceSlrSignature.get_protected());

		/*
		 * Check if alg and kid coming from protected and unprotected JWS Headers match
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

	public Boolean verifyAccountSignedSLR(ServiceLinkRecordDoubleSigned doubleSignedSlr)
			throws ParseException, JOSEException, JsonProcessingException {

		ServiceLinkRecordSignature accountSignature = doubleSignedSlr.getSignatures().get(0);

		/*
		 * JWS Unprotected Header (should contain alg and kid)
		 */
		JWSHeader unprotectedSlrHeader = accountSignature.getHeader();

		/*
		 * JWS Protected header (should contain alg, kid and jwk)
		 */
		JWSHeader protectedSlrHeader = JWSHeader.parse(accountSignature.get_protected());

		/*
		 * Check if alg and kid coming from protected and unprotected JWS Headers match
		 */
		if (unprotectedSlrHeader.getAlgorithm().equals(protectedSlrHeader.getAlgorithm())
				&& unprotectedSlrHeader.getKeyID().equals(protectedSlrHeader.getKeyID())) {

			/*
			 * Create the object to be signed with protected Header and Json serialized SLR
			 * payload
			 */
			Base64URL payload = new Payload(payloadMapper.writeValueAsString(doubleSignedSlr.getPayload()))
					.toBase64URL();
			JWSObject jwsObject = new JWSObject(accountSignature.get_protected(), payload,
					accountSignature.getSignature());

			RSAKey accountRSAKey = (RSAKey) protectedSlrHeader.getJWK();

			return jwsObject.verify(new RSASSAVerifier(accountRSAKey));

		} else
			throw new JOSEException("Protected and unprotected header fields don't match");

	}

}
