package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticlesTreeEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArticlesTreeReactive extends ReactiveSortingRepository< ArticlesTreeEntity, String > {
    Mono<ArticlesTreeEntity> findByReference(String reference);
    Mono<ArticlesTreeEntity> findFirstByReference(String reference);
}
