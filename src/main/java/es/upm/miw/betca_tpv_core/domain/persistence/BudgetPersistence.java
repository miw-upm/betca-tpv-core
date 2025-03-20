package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Budget;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BudgetPersistence {
    Mono<Budget> create(Budget budget);

    Mono<Budget> readById(String id);

    Mono<Budget> update(String id, Budget budget);

    Mono<Void> deleteById(String id);

    Flux<Budget> findByReferenceLikeNullSafe(String reference);

    Flux<Budget> findByReferenceLike(String reference);
}
