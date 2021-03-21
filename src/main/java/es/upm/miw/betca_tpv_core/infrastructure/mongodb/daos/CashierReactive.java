package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CashierEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface CashierReactive extends ReactiveSortingRepository< CashierEntity, String > {
    Mono< CashierEntity > findFirstByOrderByOpeningDateDesc();

    Flux< CashierEntity > findCashierEntitiesByClosureDateBetween(LocalDateTime dateBegin, LocalDateTime dateEnd);

}
