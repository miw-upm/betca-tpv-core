package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface SalespeoplePersistence {
    Flux<Salespeople> findBySalespersonAndSalesDate(String salesperson, LocalDate localDate);

    Flux<Salespeople> findBySalesDate(LocalDate localDate);
}
