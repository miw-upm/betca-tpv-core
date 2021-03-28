package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TagPersistence {

    Mono<Tag> create(Tag tag);

    Mono<Tag> readByName(String name);

    Mono<Tag> update(String name, Tag tag);

    Flux<Tag> findBNameAndGroupAndDescriptionNullSafe(
            String name, String group, String description);
}
