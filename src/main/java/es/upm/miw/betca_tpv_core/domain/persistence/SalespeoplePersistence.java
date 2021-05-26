package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface SalespeoplePersistence {
    Flux<Salespeople> findBySalespersonAndSalesDateBetween(String salesperson, LocalDate dateBegin, LocalDate dateEnd);

    Flux<Salespeople> findBySalesDateBetween(LocalDate dateBegin, LocalDate dateEnd);
}
