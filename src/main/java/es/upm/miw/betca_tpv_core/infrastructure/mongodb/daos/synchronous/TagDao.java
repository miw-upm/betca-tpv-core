package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TagEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TagDao extends MongoRepository<TagEntity, String> {
}
