package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Order;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface OrderPersistence {

    Mono<Order> create(Order order);

    Mono<Order> readByReference(String reference);

    Flux<Order> findByReferenceAndDescriptionAndCompanyAndOpeningDateAndClosingDateNullSafe(
            String reference, String description, String company, LocalDateTime openingDate, LocalDateTime closingDate);

    Mono<Order> update(String reference, Order order);

    Mono<Void> delete(String reference);
}