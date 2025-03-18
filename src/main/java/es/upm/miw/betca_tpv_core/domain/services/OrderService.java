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

    private final OrderPersistence orderPersistence;

    @Autowired
    public OrderService(OrderPersistence orderPersistence) {
        this.orderPersistence = orderPersistence;
    }

    public Mono<Order> create(Order order) {
        order.setOpeningDate(LocalDateTime.now());
        return this.orderPersistence.create(order);
    }

    public Mono<Order> read(String reference) {
        return this.orderPersistence.readByReference(reference);
    }

    public Flux<Order> findByReferenceAndDescriptionAndCompanyAndOpeningDateAndClosingDateNullSafe(
            String reference, String description, String company, LocalDateTime openingDate, LocalDateTime closingDate) {
        return this.orderPersistence.findByReferenceAndDescriptionAndCompanyAndOpeningDateAndClosingDateNullSafe(
                reference, description, company, openingDate, closingDate);
    }

    public Mono<Order> update(String reference, Order order) {
        return this.orderPersistence.update(reference, order);
    }

    public Mono<Void> delete(String reference) {
        return this.orderPersistence.delete(reference);
    }
}
