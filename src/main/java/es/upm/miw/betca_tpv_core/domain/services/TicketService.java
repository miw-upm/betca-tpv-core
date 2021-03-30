package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.ShoppingState;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.domain.services.utils.PdfTicketBuilder;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.TicketBasicDto;
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

    public Flux<Ticket> findByIdOrReferenceLikeOrUserMobileLikeNullSafe(String key) {
        return this.ticketPersistence.findByIdOrReferenceLikeOrUserMobileLikeNullSafe(key);
    }

    public Mono<Ticket> findById(String id) {
        return this.ticketPersistence.findById(id);
    }

    public Mono<Ticket> findByReference(String reference) {
        return this.ticketPersistence.findByReference(reference);
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

    public Flux<UserBasicDto> findByBarcodeAndAmount(String barcode, Integer amount) {
        return this.ticketPersistence
                .findAll()
                .filter(ticket -> ticket.getShoppingList().stream()
                        .anyMatch(shopping ->
                                barcode.equals(shopping.getBarcode())
                                && !(shopping.getState().equals(ShoppingState.COMMITTED))
                                && shopping.getAmount() > amount
                        )
                ).map(ticket -> new UserBasicDto(ticket.getUser()));
    }

    public Flux<TicketBasicDto> findAllWithoutInvoice() {
        return this.ticketPersistence.findAllWithoutInvoice()
                .map(TicketBasicDto::new);
    }
}
