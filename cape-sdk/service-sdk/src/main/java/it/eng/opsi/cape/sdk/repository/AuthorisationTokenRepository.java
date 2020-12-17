package it.eng.opsi.cape.sdk.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.eng.opsi.cape.sdk.model.datatransfer.AuthorisationTokenResponse;

public interface AuthorisationTokenRepository extends MongoRepository<AuthorisationTokenResponse, String> {

}
