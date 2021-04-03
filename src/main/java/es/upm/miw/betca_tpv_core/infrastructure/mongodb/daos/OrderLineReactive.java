package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderLineEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

public interface OrderLineReactive extends ReactiveSortingRepository<OrderLineEntity, String> {
    Mono<OrderLineEntity> findById(String id);
}