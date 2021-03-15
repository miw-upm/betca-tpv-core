package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import es.upm.miw.betca_tpv_core.domain.persistence.GiftTicketPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class GiftTicketService {
    private GiftTicketPersistence giftTicketPersistence;

    @Autowired
    public GiftTicketService(GiftTicketPersistence giftTicketPersistence) {
        this.giftTicketPersistence = giftTicketPersistence;
    }

    public Mono<GiftTicket> create(GiftTicket giftTicket) {
        return giftTicketPersistence.create(giftTicket);
    }
}
