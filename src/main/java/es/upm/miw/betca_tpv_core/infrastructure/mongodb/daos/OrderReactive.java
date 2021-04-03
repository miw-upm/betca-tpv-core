package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface OrderReactive extends ReactiveSortingRepository<OrderEntity, String> {

    Flux<OrderEntity> findByDescriptionAndOpeningDateBetween(String description, LocalDateTime fromDate, LocalDateTime toDate);

    Mono<OrderEntity> findByReference(String reference);
}