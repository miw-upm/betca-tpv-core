package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticlesTreeEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ArticlesTreeReactive extends ReactiveMongoRepository<ArticlesTreeEntity, String> {
    Flux<ArticlesTreeEntity> findByReference(String reference);

}
