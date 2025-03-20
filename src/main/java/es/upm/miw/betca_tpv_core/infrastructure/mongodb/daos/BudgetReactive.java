package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.BudgetEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface BudgetReactive extends ReactiveMongoRepository<BudgetEntity, String> {
    @Query("{$and:[" // allow NULL: all elements
            + "?#{ [0] == null ? {_id : {$ne:null}} : { reference : {$regex:[0], $options: 'i'} } },"
            + "] }")
    Flux<BudgetEntity> findByReferenceLikeNullSafe(String reference);

    Flux<BudgetEntity> findByReferenceLike(String reference);
}
