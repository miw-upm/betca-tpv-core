package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import es.upm.miw.betca_tpv_core.domain.persistence.GiftTicketPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.GiftTicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.GiftTicketEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class GiftTicketPersistenceMongodb implements GiftTicketPersistence {

    private TicketReactive ticketReactive;
    private GiftTicketReactive giftTicketReactive;

    @Autowired
    public GiftTicketPersistenceMongodb(TicketReactive ticketReactive, GiftTicketReactive giftTicketReactive) {
        this.ticketReactive = ticketReactive;
        this.giftTicketReactive = giftTicketReactive;
    }

    @Override
    public Mono<GiftTicket> create(GiftTicket giftTicket) {
        System.out.println("es un json" );
        GiftTicketEntity giftTicketEntity = new GiftTicketEntity(giftTicket);
        return Mono.just(giftTicket.getTicketId())
                .map(ticketid-> this.ticketReactive.findById(ticketid)
                        .switchIfEmpty(Mono.error(new NotFoundException("ticket: " + ticketid))))
                .then(this.giftTicketReactive.save(giftTicketEntity))
                .map(GiftTicketEntity::toGiftTicket);
    }
}
