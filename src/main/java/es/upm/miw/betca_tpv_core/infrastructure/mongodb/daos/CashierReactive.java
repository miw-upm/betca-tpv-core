package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CashierEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface CashierReactive extends ReactiveMongoRepository<CashierEntity, String> {
    Mono<CashierEntity> findFirstByOrderByOpeningDateDesc();

    Flux<CashierEntity> findAllByClosureDateBetween(LocalDateTime closureDate, LocalDateTime closureDate2);
}
