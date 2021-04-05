package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketPersistence {
    Mono< Ticket > create(Ticket ticket);

    Mono< Ticket > readById(String id);

    Flux<Ticket> findByIdOrReferenceLikeOrUserMobileLikeNullSafe(String key);

    Mono<Ticket> findById(String id);

    Mono<Ticket> findByReference(String reference);

    Mono<Ticket> update(String id, List<Shopping> shoppingList);

    Flux<Ticket> findByRegistrationDateAfter(LocalDateTime localDateTime);

    Flux<Ticket> findByUserMobile(String mobile);

    Flux<Ticket> findByRangeRegistrationDate(LocalDateTime initial, LocalDateTime end);

    Flux<Ticket> findAll();

    Flux<Ticket> findAllWithoutInvoice();
}
