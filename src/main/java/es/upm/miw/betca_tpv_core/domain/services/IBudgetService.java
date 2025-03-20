package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Budget;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBudgetService {
    Mono<Budget> create(Budget budget);

    Mono<Budget> read(String id);

    Mono<Void> delete(String id);

    Mono<Budget> update(String id, Budget budget);

    Flux<Budget> findByReferenceLikeNullSafe(String reference);

    Flux<Budget> findByReferenceLike(String reference);
}
