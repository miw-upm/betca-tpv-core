package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Tags;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TagsPersistence {
    Mono<Tag> create(Tag tag);

    Mono<Tag> readById(String id);

    Flux<Tag> findAll();

    Flux<Tag> findByName(String name);

    Flux<Tag> findByGroup(String group);

    Mono<Tag> update(String id, Tag tag);

    Mono<Void> delete(String id);
}
