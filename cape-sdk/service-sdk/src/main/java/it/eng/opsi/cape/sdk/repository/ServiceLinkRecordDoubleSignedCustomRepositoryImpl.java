package it.eng.opsi.cape.sdk.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import it.eng.opsi.cape.exception.ServiceLinkRecordNotFoundException;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkRecordDoubleSigned;
import it.eng.opsi.cape.sdk.model.linking.ServiceLinkStatusRecordSigned;

public class ServiceLinkRecordDoubleSignedCustomRepositoryImpl
		implements ServiceLinkRecordDoubleSignedCustomRepository {

	@Autowired
	MongoTemplate template;

	@Override
	public ServiceLinkRecordDoubleSigned addStatusToSlr(String slrId, ServiceLinkStatusRecordSigned ssr)
			throws ServiceLinkRecordNotFoundException {

		Query q = query(where("payload._id").is(new ObjectId(slrId)));

		ServiceLinkRecordDoubleSigned result = template.findAndModify(q, new Update().push("serviceLinkStatuses", ssr),
				new FindAndModifyOptions().returnNew(true), ServiceLinkRecordDoubleSigned.class);

		if (result == null)
			throw new ServiceLinkRecordNotFoundException(
					"The Service Link Record with id: " + slrId + " was not found");

		return result;
	}

}
