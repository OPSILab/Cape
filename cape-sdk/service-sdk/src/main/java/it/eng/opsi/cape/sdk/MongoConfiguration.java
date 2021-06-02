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
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;

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

			for (MongoPersistentEntity<?> persistentEntity : mongoMappingContext.getPersistentEntities()) {
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
