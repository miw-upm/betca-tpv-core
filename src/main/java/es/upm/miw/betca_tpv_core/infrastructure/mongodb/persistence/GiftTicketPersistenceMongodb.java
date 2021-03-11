package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import es.upm.miw.betca_tpv_core.domain.persistence.GiftTicketPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.GiftTicketEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ShoppingEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TicketEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class GiftTicketPersistenceMongodb implements GiftTicketPersistence {

    private TicketReactive ticketReactive;

    @Autowired
    public GiftTicketPersistenceMongodb(TicketReactive ticketReactive) {
        this.ticketReactive = ticketReactive;
    }

    @Override
    public Mono<GiftTicket> create(GiftTicket giftTicket) {
        System.out.println("es un json" );
        TicketEntity ticketEntity = new TicketEntity();
        GiftTicketEntity giftTicketEntity = new GiftTicketEntity(giftTicket.getId(), giftTicket.getMessage(), this.ticketReactive
                .findById(giftTicket.getTicketId()).block());
        System.out.println("----es un json" + giftTicketEntity.toGiftTicket() );
        return Mono.just(giftTicketEntity.toGiftTicket());
    }
}
