package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.persistence.GiftTicketPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.GiftTicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.GiftTicketEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ShoppingEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TicketEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class GiftTicketPersistenceMongodb implements GiftTicketPersistence {

    private final GiftTicketReactive giftTicketReactive;
    private final ArticleReactive articleReactive;

    @Autowired
    public GiftTicketPersistenceMongodb(GiftTicketReactive giftTicketReactive, TicketReactive ticketReactive, ArticleReactive articleReactive) {
        this.giftTicketReactive = giftTicketReactive;
        this.articleReactive = articleReactive;
    }

    @Override
    public Mono<Ticket> readTicketByReference(String reference) {
        return giftTicketReactive.findByReference(reference)
                .flatMap(giftTicketEntity -> {
                    TicketEntity ticketEntity = giftTicketEntity.getTicket();
                    return Mono.just(ticketEntity.toTicket());
                });
    }

    @Override
    public Mono<GiftTicket> create(GiftTicket giftTicket) {
        GiftTicketEntity giftTicketEntity = new GiftTicketEntity(giftTicket);

        List<Shopping> shopList = giftTicket.getTicket().getShoppingList();

        Shopping[] shopListDos = shopList.toArray(new Shopping[0]);

        return Flux.fromArray(shopListDos)
                .flatMap(shopping -> this.articleReactive.findByBarcode(shopping.getBarcode())
                        .map(articleEntity -> {
                            ShoppingEntity shoppingEntity = new ShoppingEntity(shopping);
                            shoppingEntity.setArticleEntity(articleEntity);
                            shoppingEntity.setDescription(articleEntity.getDescription());
                            giftTicketEntity.getTicket().add(shoppingEntity);
                            return shoppingEntity;
                        }))
                .then(this.giftTicketReactive.save(giftTicketEntity))
                .map(GiftTicketEntity::toGiftTicket);
    }

    @Override
    public Mono<GiftTicket> getGiftTicketByReference(String reference) {
        return giftTicketReactive.findByReference(reference)
                .flatMap(giftTicketEntity -> {
                    GiftTicket giftTicket = giftTicketEntity.toGiftTicket();
                    return Mono.just(giftTicket);
                });
    }
}