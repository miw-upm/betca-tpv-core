package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TicketEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface TicketReactive extends ReactiveSortingRepository< TicketEntity, String > {

    @Query("{$or:[" // allow NULL: all elements
            + "?#{ [0] == null ? {_id : {$ne:null}} : { reference : {$regex:[0], $options: 'i'} } },"
            + "?#{ [1] == null ? {_id : {$ne:null}} : { userMobile :{$regex:[1], $options: 'i'} }  }"
            + "] }")
    Flux<TicketEntity> findByReferenceLikeOrUserMobileLikeNullSafe(String reference, String userMobile);

    Mono<TicketEntity> findByReference(String reference);

    Mono<TicketEntity> findById(String id);

    Flux<TicketEntity> findTicketEntitiesByCreationDateAfter(LocalDateTime localDateTime);

    Flux<TicketEntity> findByUserMobile(String userMobile);
}
