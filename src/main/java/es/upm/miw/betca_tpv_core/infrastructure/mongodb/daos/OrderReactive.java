package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface OrderReactive extends ReactiveMongoRepository<OrderEntity, String> {

    Mono<OrderEntity> findByReference(String reference);

    @Query("{$and:[" // allow NULL: all elements
            + "?#{ [0] == null ? {_id : {$ne:null}} : { reference : {$regex:[0], $options: 'i'} } },"
            + "?#{ [1] == null ? {_id : {$ne:null}} : { description : {$regex:[1], $options: 'i'} } },"
            + "?#{ [2] == null ? {_id : {$ne:null}} : { company : {$regex:[2], $options: 'i'} } },"
            + "?#{ [3] == null ? {_id : {$ne:null}} : { openingDate : {$lt:[3]} } },"
            + "?#{ [4] == null ? {_id : {$ne:null}} : { closingDate: { $gte: [4] } } }"
            + "] }")
    Flux<OrderEntity> findByReferenceAndDescriptionAndCompanyAndOpeningDateAndClosingDateNullSafe(
            String reference, String description, String company, LocalDateTime openingDate, LocalDateTime closingDate);
}
