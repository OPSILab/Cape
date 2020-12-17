package it.eng.opsi.cape.sdk;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class MongoConfiguration {

	
	private final MongoTemplate mongoTemplate;

	private final MongoConverter mongoConverter;


	@EventListener(ApplicationReadyEvent.class)
	public void initIndicesAfterStartup() {
		log.info("Mongo InitIndicesAfterStartup init");
		long init = System.currentTimeMillis();

		MappingContext mappingContext = this.mongoConverter.getMappingContext();

		if (mappingContext instanceof MongoMappingContext) {
			MongoMappingContext mongoMappingContext = (MongoMappingContext) mappingContext;

			for (BasicMongoPersistentEntity<?> persistentEntity : mongoMappingContext.getPersistentEntities()) {
				Class<?> clazz = persistentEntity.getType();
				if (clazz.isAnnotationPresent(Document.class)) {
					IndexOperations indexOps = mongoTemplate.indexOps(clazz);
					MongoPersistentEntityIndexResolver resolver = new MongoPersistentEntityIndexResolver(
							mongoMappingContext);
					resolver.resolveIndexFor(clazz).forEach(indexOps::ensureIndex);
				}
			}
		}
		log.info("Mongo InitIndicesAfterStartup take: {}", (System.currentTimeMillis() - init));
	}
}