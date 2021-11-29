package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CompositeArticleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompositeArticleDao extends MongoRepository< CompositeArticleEntity, String > {
}
