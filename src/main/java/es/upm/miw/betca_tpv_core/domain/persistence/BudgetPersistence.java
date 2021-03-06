package es.upm.miw.betca_tpv_core.domain.persistence;
import es.upm.miw.betca_tpv_core.domain.model.Budget;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BudgetPersistence {
    Mono<Budget> create(Budget budget);
    Mono< Budget > read(String id);
    Mono<Budget> findById(String id);
    Flux< String > findNullSafe(String id);
}
