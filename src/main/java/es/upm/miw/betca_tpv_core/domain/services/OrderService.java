package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Order;
import es.upm.miw.betca_tpv_core.domain.persistence.OrderPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private OrderPersistence orderPersistence;

    @Autowired
    public OrderService(OrderPersistence orderPersistence) {
        this.orderPersistence = orderPersistence;
    }

    public Flux<Order> readByDescriptionAndOpeningDateBetween(String description, LocalDateTime fromDate, LocalDateTime toDate) {
        return this.orderPersistence.findByDescriptionAndOpeningDateBetween(description, fromDate, toDate);
    }

    public Mono<Order> readByReference(String reference) {
        return this.orderPersistence.findByReference(reference);
    }

    public Mono<Order> create(Order order) {
        return this.orderPersistence.create(order);
    }

    public Mono<Order> update(String reference, Order order) {
        return this.orderPersistence.update(reference, order);
    }

    public Mono<Void> delete(String reference) {
        return this.orderPersistence.delete(reference);
    }

}
