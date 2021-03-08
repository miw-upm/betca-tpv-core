package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OfferEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OfferReactive extends ReactiveSortingRepository<OfferEntity, String> {

    @Query("{$and:[" // allow NULL: all elements
            + "?#{ [0] == null ? {_id : {$ne:null}} : { reference : {$regex:[0], $options: 'i'} } },"
            + "?#{ [1] == null ? {_id : {$ne:null}} : { description : {$regex:[1], $options: 'i'} } },"
            + "] }")
    Flux<OfferEntity> findByReferenceAndDescriptionNullSafe(String reference, String description);

    Mono<OfferEntity> findByReference(String reference);
}