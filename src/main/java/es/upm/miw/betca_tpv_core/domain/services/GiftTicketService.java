package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class GiftTicketService {
    private TicketPersistence ticketPersistence;

    public Mono<Void> create(GiftTicket giftTicket) {
        System.out.println(giftTicket.getTicketId());
        System.out.println(giftTicket.getMessage());
        return Mono.empty();
    }
}
