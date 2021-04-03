package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.domain.services.utils.MailService;
import es.upm.miw.betca_tpv_core.domain.services.utils.PdfTicketBuilder;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.UserBasicDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private TicketPersistence ticketPersistence;
    private UserMicroservice userMicroservice;
    private ArticlePersistence articlePersistence;
    private CashierService cashierService;
    private MailService mailService;

    @Autowired
    public TicketService(TicketPersistence ticketPersistence, UserMicroservice userMicroservice, ArticlePersistence articlePersistence,
                         CashierService cashierService, MailService mailService) {
        this.ticketPersistence = ticketPersistence;
        this.userMicroservice = userMicroservice;
        this.articlePersistence = articlePersistence;
        this.cashierService = cashierService;
        this.mailService = mailService;
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

    public Flux<Ticket> findByIdOrReferenceLikeOrUserMobileLikeNullSafe(String key) {
        return this.ticketPersistence.findByIdOrReferenceLikeOrUserMobileLikeNullSafe(key);
    }

    public Mono<Ticket> findById(String id) {
        return this.ticketPersistence.findById(id);
    }

    public Mono<Ticket> findByReference(String reference) {
        return this.ticketPersistence.findByReference(reference);
    }

    public Mono<Ticket> findSelectedByReference(String reference) {
        return this.ticketPersistence.findByReference(reference)
                .flatMap(ticket ->
                    this.readUserByUserMobileNullSafe(ticket.getUser())
                            .map(user -> {
                                ticket.setUser(user);
                                return ticket;
                            })
                            .switchIfEmpty(Mono.just(ticket))
                );
    }

    public Mono<Ticket> update(String id, List<Shopping> shoppingList) {
        return this.ticketPersistence.update(id, shoppingList);
    }

    public Flux<Ticket> findTicketByRegistrationDateAfter(LocalDateTime localDateTime){
        return this.ticketPersistence.findTicketByRegistrationDateAfter(localDateTime);
    }

    public Flux<Shopping> findAllBoughtArticlesByMobile(String mobile) {
        return this.findByUserMobile(mobile)
                .flatMap(ticket -> Flux.fromIterable(ticket.getShoppingList()))
                .distinct(Shopping::getBarcode);
    }

    private Flux<Ticket> findByUserMobile(String mobile) {
        return this.ticketPersistence.findByUserMobile(mobile);
    }

    public Flux<String> findAllWithoutInvoice() {
        return this.ticketPersistence.findAllWithoutInvoice()
                .map(Ticket::getReference);
    }

    public Flux<Ticket> findByBarcodeAndAmountList(List<Tracking> data) {
        return Flux
                .fromIterable(data)
                .flatMap(da -> this.ticketPersistence.findAll()
                        .filter(ticket -> ticket.getShoppingList().stream()
                                .anyMatch( shopping -> {
                                    if(
                                            da.getBarcode().equals(shopping.getBarcode()) &&
                                                    da.getAmount() >= shopping.getAmount() &&
                                                    !(shopping.getState().equals(ShoppingState.COMMITTED))
                                    ) {
                                        da.setAmount(da.getAmount() - shopping.getAmount());
                                        return true;
                                    } else {
                                        return false;
                                    }
                                })
                        )
                );
    }

    public Flux<Ticket> updateByBarcodeAndAmountList(List<Tracking> data, ShoppingState state) {
        return Flux
                .fromIterable(data)
                .flatMap(da -> this.ticketPersistence.findAll()
                        .map(ticket -> {
                            List<Shopping> shoppingList = ticket.getShoppingList()
                                    .stream()
                                    .peek(shopping -> {
                                        if (da.getAmount() >= shopping.getAmount() && shopping.getBarcode().equals(da.getBarcode())) {
                                            da.setAmount(da.getAmount() - shopping.getAmount());
                                            shopping.setState(state);
                                            sendEmail(
                                                    ticket.getUser(),
                                                    "El artículo " + shopping.getBarcode() +" ha sido abastecido, por favor revisa tu referencia: " + ticket.getReference() +" desde nuestro sitio web."
                                            );
                                        }
                                    }).collect(Collectors.toList());
                            this.update(ticket.getId(), shoppingList);
                            return ticket;
                        })
                );
    }

    public void sendEmail(User user, String message) {
        this.mailService.send(user.getEmail(), message);
    }

}
