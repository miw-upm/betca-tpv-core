package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface InvoicePersistence {
    Mono<Invoice> create(Invoice invoice);
    Mono<Invoice> findByTicketId(String ticketId);
}
