package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.InvoiceEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface InvoiceReactive extends ReactiveMongoRepository<InvoiceEntity, String> {
    Mono<InvoiceEntity> findByTicketId(String ticketId);
    Mono<InvoiceEntity> findTopByOrderByIdentityDesc();
}
