package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Order;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface OrderPersistence {
    Flux<Order> findByDescriptionAndCompanyAndOpeningDateBetweenAndNullSafe(String company, String description, LocalDateTime openingDate);

    Mono<Order> findByReference(String reference);

    Mono<Order> create(Order order);

    Mono<Order> update(String reference, Order updatedOrder);

    Mono<Void> delete(String reference);
}
