package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.domain.services.utils.PdfTicketBuilder;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketPersistence ticketPersistence;
    private final UserMicroservice userMicroservice;
    private final ArticlePersistence articlePersistence;
    private final CashierService cashierService;

    @Autowired
    public TicketService(TicketPersistence ticketPersistence, UserMicroservice userMicroservice, ArticlePersistence articlePersistence,
                         CashierService cashierService) {
        this.ticketPersistence = ticketPersistence;
        this.userMicroservice = userMicroservice;
        this.articlePersistence = articlePersistence;
        this.cashierService = cashierService;
    }

    public Mono< Ticket > create(Ticket ticket) {
        ticket.setId(null);
        ticket.setReference(UUIDBase64.URL.encode());
        ticket.setCreationDate(LocalDateTime.now());
        Mono< Void > articlesStock = this.updateArticlesStockAssuredSequentially(ticket);
        Mono< Void > cashierUpdated = this.cashierService.addSale(ticket.getCash(), ticket.getCard(), ticket.getVoucher()).then();
        Mono< Void > userMono = this.readUserByUserMobileNullSafe(ticket.getUser())
                .then();
        return Mono.when(articlesStock, cashierUpdated, userMono)
                .then(this.ticketPersistence.create(ticket));

    }

    private Mono< Void > updateArticlesStockAssuredSequentially(Ticket ticket) {
        return Flux.concat( // Flux<Article> but sequential!!!
                ticket.getShoppingList().stream() // Stream<Shopping>
                        .map(shopping -> // Stream< Mono< Article > >
                                this.articlePersistence.readAndWriteStockByBarcodeAssured(
                                        shopping.getBarcode(), -shopping.getAmount())
                        ).collect(Collectors.toList() // List< Mono<Article> >
                )
        ).then(); // Mono<Void>
    }

    public Mono< byte[] > readReceipt(String id) {
        return this.ticketPersistence.readById(id)
                .flatMap(ticket -> this.readUserByUserMobileNullSafe(ticket.getUser())
                        .map(user -> {
                            ticket.setUser(user);
                            return ticket;
                        })
                        .switchIfEmpty(Mono.just(ticket)))
                .map(new PdfTicketBuilder()::generateTicket);

    }

    private Mono< User > readUserByUserMobileNullSafe(User user) {
        if (user != null) {
            return this.userMicroservice.readByMobile(user.getMobile());
        } else {
            return Mono.empty();
        }
    }
}
