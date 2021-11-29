package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticlesTreeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ArticlesTreeDao extends MongoRepository< ArticlesTreeEntity, String > {
    List< ArticlesTreeEntity > findByReference(String reference);
}
