package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.CustomerPoints;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.domain.services.utils.PdfTicketBuilder;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import es.upm.miw.betca_tpv_core.domain.services.CustomerPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.LocalDateTime;

@Service
public class TicketService {

    private final TicketPersistence ticketPersistence;
    private final UserMicroservice userMicroservice;
    private final ArticlePersistence articlePersistence;
    private final CashierService cashierService;
    private final CustomerPointsService customerPointsService;

    @Autowired
    public TicketService(TicketPersistence ticketPersistence, UserMicroservice userMicroservice, ArticlePersistence articlePersistence,
                         CashierService cashierService, CustomerPointsService customerPointsService) {
        this.ticketPersistence = ticketPersistence;
        this.userMicroservice = userMicroservice;
        this.articlePersistence = articlePersistence;
        this.cashierService = cashierService;
        this.customerPointsService = customerPointsService;
    }

    public Mono<Ticket> create(Ticket ticket) {
        ticket.setId(null);
        ticket.setReference(UUIDBase64.URL.encode());
        ticket.setCreationDate(LocalDateTime.now());
        Mono<Void> articlesStock = this.updateArticlesStockAssuredSequentially(ticket);
        Mono<Void> cashierUpdated = this.cashierService.addSale(ticket.getCash(), ticket.getCard(), ticket.getVoucher()).then();
        Mono<Void> userMono = this.readUserByUserMobileNullSafe(ticket.getUser())
                .then();
        return Mono.when(articlesStock, cashierUpdated, userMono)
                .then(this.ticketPersistence.create(ticket));

    }

    private Mono<Void> updateArticlesStockAssuredSequentially(Ticket ticket) {
        return Flux.concat( // Flux<Article> but sequential!!!
                ticket.getShoppingList().stream() // Stream<Shopping>
                        .map(shopping -> // Stream< Mono< Article > >
                                this.articlePersistence.readAndWriteStockByBarcodeAssured(
                                        shopping.getBarcode(), -shopping.getAmount())
                        ).toList() // List< Mono<Article>
        ).then(); // Mono<Void>
    }

    public Mono<byte[]> readReceipt(String id) {
        return ticketPersistence.readById(id)
                .flatMap(ticket -> {
                    if (ticket.getUser() != null) {
                        return Mono.zip(
                                Mono.just(ticket),
                                customerPointsService.readCustomerPointsByMobile(ticket.getUser().getMobile())
                                        .defaultIfEmpty(new CustomerPoints())
                        );
                    }
                    return Mono.just(Tuples.of(ticket, new CustomerPoints()));
                })
                .map(tuple -> {
                    Ticket ticket = tuple.getT1();
                    CustomerPoints customerPoints = (CustomerPoints) tuple.getT2();
                    return new PdfTicketBuilder().generateTicket(ticket, customerPoints);
                });
    }

    private Mono<User> readUserByUserMobileNullSafe(User user) {
        if (user != null) {
            return this.userMicroservice.readByMobile(user.getMobile());
        } else {
            return Mono.empty();
        }
    }
}
