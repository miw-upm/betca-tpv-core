package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ShoppingEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TicketEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TicketPersistenceMongodb implements TicketPersistence {

    private final TicketReactive ticketReactive;
    private final ArticleReactive articleReactive;

    @Autowired
    public TicketPersistenceMongodb(TicketReactive ticketReactive, ArticleReactive articleReactive) {
        this.ticketReactive = ticketReactive;
        this.articleReactive = articleReactive;
    }

    @Override
    public Mono<Ticket> create(Ticket ticket) {
        TicketEntity ticketEntity = new TicketEntity(ticket);
        return Flux.fromStream(ticket.getShoppingList().stream())
                .flatMap(shopping -> {
                    ShoppingEntity shoppingEntity = new ShoppingEntity(shopping);
                    return this.articleReactive.findByBarcode(shopping.getBarcode())
                            .switchIfEmpty(Mono.error(new NotFoundException("Article: " + shopping.getBarcode())))
                            .map(articleEntity -> {
                                shoppingEntity.setArticleEntity(articleEntity);
                                shoppingEntity.setDescription(articleEntity.getDescription());
                                return shoppingEntity;
                            });
                }).doOnNext(ticketEntity::add)
                .then(this.ticketReactive.save(ticketEntity))
                .map(TicketEntity::toTicket);
    }

    @Override
    public Mono< Ticket > readById(String id) {
        return this.ticketReactive.findById(id)
                .map(TicketEntity::toTicket);
    }

}
