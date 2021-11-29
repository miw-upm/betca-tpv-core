package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.SingleArticleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SingleArticleDao extends MongoRepository< SingleArticleEntity, String > {
}
