package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TicketEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TicketReactive extends ReactiveMongoRepository<TicketEntity, String> {
}
