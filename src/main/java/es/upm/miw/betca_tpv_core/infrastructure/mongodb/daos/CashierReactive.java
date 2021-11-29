package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CashierEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

public interface CashierReactive extends ReactiveSortingRepository< CashierEntity, String > {
    Mono< CashierEntity > findFirstByOrderByOpeningDateDesc();
}
