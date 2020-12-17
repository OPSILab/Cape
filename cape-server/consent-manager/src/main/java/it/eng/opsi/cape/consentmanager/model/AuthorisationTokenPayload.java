package it.eng.opsi.cape.consentmanager.model;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthorisationTokenPayload {

	String iss;

	AuthorisationTokenPayloadPopKid cnf;

	String[] aud;

	ZonedDateTime exp;

	ZonedDateTime nbf;

	ZonedDateTime iat;

	String jti;

	String cr_id;
}
