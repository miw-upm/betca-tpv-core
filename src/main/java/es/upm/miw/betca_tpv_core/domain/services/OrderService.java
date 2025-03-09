package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Order;
import es.upm.miw.betca_tpv_core.domain.persistence.OrderPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

    private final OrderPersistence orderPersistence;

    @Autowired
    public OrderService(OrderPersistence orderPersistence) {
        this.orderPersistence = orderPersistence;
    }

    public Mono<Order> create(Order order) {
        return this.orderPersistence.create(order);
    }

    public Mono<Order> read(String reference) {
        return this.orderPersistence.readByReference(reference);
    }

    public Mono<Order> update(String reference, Order order) {
        return this.orderPersistence.update(reference, order);
    }
}
