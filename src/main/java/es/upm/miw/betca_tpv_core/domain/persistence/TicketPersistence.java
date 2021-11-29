package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TicketPersistence {
    Mono< Ticket > create(Ticket ticket);

    Mono< Ticket > readById(String id);
}
