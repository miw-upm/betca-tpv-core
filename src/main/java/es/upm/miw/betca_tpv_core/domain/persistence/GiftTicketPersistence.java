package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface GiftTicketPersistence {
    Mono<GiftTicket> create(GiftTicket giftTicket);

    Mono<GiftTicket> readById(String id);
}
