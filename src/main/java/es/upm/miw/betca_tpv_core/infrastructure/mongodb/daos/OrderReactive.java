package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface OrderReactive extends ReactiveSortingRepository<OrderEntity, String> {

    @Query("{$and:[" // allow NULL: all elements
            + "?#{ [0] == null ? {_id : {$ne:null}} : { description : {$regex:[0], $options: 'i'} } },"
            + "?#{ [1] == null ? {_id : {$ne:null}} : { description : {$regex:[1], $options: 'i'} } },"
            + "?#{ $and:[ ?#{ [2] == null}, ?#{ [3] == null} ] ? {_id : {$ne:null}} : { openingDate : {$regex:[1], $options: 'i'} } },"
            + "] }")
    Flux<OrderEntity> findByDescriptionAndCompanyAndOpeningDateBetweenAndNullSafe(String company, String description, LocalDateTime openingDate);

    Mono<OrderEntity> findByReference(String reference);
}