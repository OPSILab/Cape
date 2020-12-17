package it.eng.opsi.cape.sdk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.eng.opsi.cape.sdk.model.linking.UserSurrogateIdLink;

public interface UserSurrogateIdLinkRepository extends MongoRepository<UserSurrogateIdLink, String> {

	public Optional<UserSurrogateIdLink> findTopByUserIdAndServiceIdAndOperatorIdOrderByCreatedDesc(String userId,
			String serviceId, String operatorId);

}
