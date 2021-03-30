package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.InvoiceReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.InvoiceEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ShoppingEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TicketEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TicketPersistenceMongodb implements TicketPersistence {

    private TicketReactive ticketReactive;
    private ArticleReactive articleReactive;
    private InvoiceReactive invoiceReactive;

    @Autowired
    public TicketPersistenceMongodb(TicketReactive ticketReactive, ArticleReactive articleReactive, InvoiceReactive invoiceReactive) {
        this.ticketReactive = ticketReactive;
        this.articleReactive = articleReactive;
        this.invoiceReactive = invoiceReactive;
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
    public Mono<Ticket> readById(String id) {
        return this.ticketReactive.findById(id)
                .map(TicketEntity::toTicket);
    }

    @Override
    public Flux<Ticket> findByIdOrReferenceLikeOrUserMobileLikeNullSafe(String key) {
        if (key == null) {
            return this.ticketReactive.findAll()
                    .map(TicketEntity::toTicket);
        }
        return this.ticketReactive.findById(key)
                .mergeWith(this.ticketReactive.findByReferenceLikeOrUserMobileLikeNullSafe(key, key))
                .map(TicketEntity::toTicket);
    }

    public Flux<Ticket> findTicketByRegistrationDateAfter(LocalDateTime localDateTime) {
        return this.ticketReactive.findTicketEntitiesByCreationDateAfter(localDateTime)
                .map(TicketEntity::toTicket);
    }

    @Override
    public Flux<Ticket> findByUserMobile(String mobile) {
        return this.ticketReactive.findByUserMobile(mobile)
                .map(TicketEntity::toTicket);
    }

    @Override
    public Mono<Ticket> findById(String id) {
        return this.ticketReactive.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent ticket id: " + id)))
                .map(TicketEntity::toTicket);
    }

    @Override
    public Mono<Ticket> findByReference(String reference) {
        return this.ticketReactive.findByReference(reference)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent ticket reference: " + reference)))
                .map(TicketEntity::toTicket);
    }

    @Override
    public Mono<Ticket> update(String id, List<Shopping> shoppingList) {
        return this.ticketReactive.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Ticket does not exist: " + id)))
                .flatMap(ticketEntity -> {
                    ticketEntity.getShoppingEntityList().clear();
                    return Flux.fromStream(shoppingList.stream())
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
                });
    }

    @Override
    public Flux<Ticket> findByRangeRegistrationDate(LocalDateTime initial, LocalDateTime end) {
        return this.ticketReactive.findByCreationDateBetween(initial, end)
                .map(TicketEntity::toTicket);
    }

    @Override
    public Flux<Ticket> findAll() {
        return this.ticketReactive.findAll()
                .map(TicketEntity::toTicket);
    }

    /*
     @Override
    public Flux<Ticket> findAllWithoutInvoice() {
        return this.ticketReactive.findAll()
                .flatMap(ticketEntity -> {
                    return this.invoiceReactive.findByTicketEntity(ticketEntity)
                            .flatMap(invoiceEntity -> Mono.error(new ConflictException("error")))
                            .then(Mono.just(ticketEntity));

                })
                .map(TicketEntity::toTicket);
    }
     */

    @Override
    public Flux<Ticket> findAllWithoutInvoice() {
        return this.ticketReactive.findAll()
                .flatMap(ticketEntity ->
                        this.invoiceReactive.findByTicketEntity(ticketEntity)
                                .switchIfEmpty(Mono.just(new InvoiceEntity(ticketEntity)))
                                //.doOnNext(System.out::println)
                                .map(invoiceEntity -> {
                                    if (invoiceEntity.getId() != null) {
                                        System.out.println("Devuelvo un ticket dummy");
                                        return new TicketEntity();
                                    } else {
                                        System.out.println("Devuelvo un ticket bueno");
                                        return ticketEntity;
                                    }
                                })
                )
                .filter(ticketEntity -> ticketEntity.getId() != null)
                //.doOnNext(System.out::println)
                .map(TicketEntity::toTicket);
    }

}
