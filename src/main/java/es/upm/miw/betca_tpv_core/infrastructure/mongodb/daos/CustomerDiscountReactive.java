package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CustomerDiscountEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface CustomerDiscountReactive extends ReactiveSortingRepository<CustomerDiscountEntity, String> {
    @Query("{$and:[" // allow NULL: all elements
            + "?#{ [0] == null ? {_id : {$ne:null}} : { note : {$regex:[0], $options: 'i'} } },"
            + "?#{ [1] == null ? {_id : {$ne:null}} : { discount : [1] } },"
            + "?#{ [2] == null ? {_id : {$ne:null}} : { minimumPurchase : [2] }  }"
            + "?#{ [3] == null ? {_id : {$ne:null}} : { user : {$regex:[3], $options: 'i'} }  }"
            + "] }")
    Flux<CustomerDiscountEntity> findByNoteAndDiscountAndMinimumPurchaseAndUserNullSafe(
            String note, Double discount, Double minimumPurchase, String user
    );

    Flux<CustomerDiscountEntity> findByUser(String user);
}
