package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Budget;
import reactor.core.publisher.Mono;

public interface IBudgetService {
    Mono<Budget> create(Budget budget);
}
