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
        return Mono.justOrEmpty(giftTicket.getTicketId())
                .flatMap(providerTicket -> this.ticketReactive.findById(giftTicket.getTicketId())
                            .switchIfEmpty(Mono.error(new NotFoundException("Non exist ticket id")))
                )
                .map(providerEntity -> new GiftTicketEntity(giftTicket, providerEntity))
                .flatMap(this.giftTicketReactive::save)
                .map(GiftTicketEntity::toGiftTicket);
    }

    @Override
    public Mono<GiftTicket> readById(String id) {
        return this.giftTicketReactive.findById(id)
                .map(GiftTicketEntity::toGiftTicket);
    }
}
