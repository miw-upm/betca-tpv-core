package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import es.upm.miw.betca_tpv_core.domain.model.User;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface InvoicePersistence {
    Mono<Invoice> create(Invoice invoice);
    Mono<Invoice> readByIdentity(Integer identity);
    Mono<Invoice> findByTicketId(String ticketId);
    Flux<Invoice> findByUserMobile(String mobile);
    Flux<Invoice> findAll();
    Mono<Invoice> updateUser(Integer identity, User user);
}