package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.BudgetEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

public interface BudgetReactive extends ReactiveSortingRepository<BudgetEntity, String > {
    Mono<BudgetEntity> readById(String id);
}
