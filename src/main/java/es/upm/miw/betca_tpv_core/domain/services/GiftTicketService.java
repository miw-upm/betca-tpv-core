package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.GiftTicketPersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class GiftTicketService {
    private GiftTicketPersistence giftTicketPersistence;
    private UserMicroservice userMicroservice;

    @Autowired
    public GiftTicketService(GiftTicketPersistence giftTicketPersistence, UserMicroservice userMicroservice) {
        this.giftTicketPersistence = giftTicketPersistence;
        this.userMicroservice = userMicroservice;
    }

    public Mono<GiftTicket> create(GiftTicket giftTicket) {
        System.out.println(giftTicket.getTicketId());
        System.out.println(giftTicket.getMessage());
        return giftTicketPersistence.create(giftTicket);
    }
}
