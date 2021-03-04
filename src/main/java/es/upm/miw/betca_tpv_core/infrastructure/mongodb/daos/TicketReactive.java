package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TicketEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface TicketReactive extends ReactiveSortingRepository< TicketEntity, String > {

    @Query("{$or:[" // allow NULL: all elements
            + "?#{ [0] == null ? {_id : {$ne:null}} : { id : {$regex:[0], $options: 'i'} } },"
            + "?#{ [1] == null ? {_id : {$ne:null}} : { reference : {$regex:[1], $options: 'i'} } },"
            + "?#{ [2] == null ? {_id : {$ne:null}} : { userMobile :{$regex:[2], $options: 'i'} }  }"
            + "] }")
    Flux<TicketEntity> findByIdLikeOrReferenceLikeOrUserMobileLikeNullSafe(String id, String reference, String userMobile);
}
