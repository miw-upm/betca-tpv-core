package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.GiftTicketEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface GiftTicketReactive extends ReactiveMongoRepository<GiftTicketEntity, String> {
    Mono<GiftTicketEntity> findByReference(String reference);
}