package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Tags;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TagsPersistence {
    Mono<Tags> create(Tags tag);

    Mono<Tags> readByName(String name);

    Mono<Tags> update(String name, Tags tag);

    Mono<Void> deleteByName(String name);

    Flux<Tags> findByAnyNullField();

    Flux<Tags> findByNameAndGroupAndDescriptionNullSafe(String name, String group, String description);

    Flux<Tags> findByNameLikeAndGroupIsNotNullNullSafe(String name);
}
