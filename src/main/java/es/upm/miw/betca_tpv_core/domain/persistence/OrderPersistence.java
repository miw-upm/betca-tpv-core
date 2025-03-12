package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Order;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface OrderPersistence {

    Mono<Order> create(Order order);

    Mono<Order> readByReference(String reference);

    Mono<Order> update(String reference, Order order);
}