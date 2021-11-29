package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Cashier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CashierPersistence {

    Mono< Cashier > findLast();

    Mono< Cashier > create(Cashier cashier);

    Mono< Cashier > update(String id, Cashier lastCashier);
}

