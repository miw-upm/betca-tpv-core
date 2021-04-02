package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.configuration.JwtService;
import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.ShoppingState;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.model.Tracking;
import es.upm.miw.betca_tpv_core.domain.services.TicketService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@Rest
@RequestMapping(TicketResource.TICKETS)
public class TicketResource {
    public static final String TICKETS = "/tickets";

    public static final String SEARCH = "/search";
    public static final String ID_ID = "/{id}";
    public static final String REFERENCE_ID = "/{reference}";
    public static final String REFERENCE = "/reference";
    public static final String RECEIPT = "/receipt";
    public static final String BOUGHT_ARTICLES = "/boughtArticles";
    public static final String TRACKING = "/tracking";
    public static final String NO_INVOICE = "/noInvoice";
    public static final String SELECTED = "/selected";

    private TicketService ticketService;
    private JwtService jwtService;

    @Autowired
    public TicketResource(TicketService ticketService, JwtService jwtService) {
        this.ticketService = ticketService;
        this.jwtService = jwtService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Ticket> create(@Valid @RequestBody Ticket ticket) {
        return this.ticketService.create(ticket);
    }

    @GetMapping(value = ID_ID + RECEIPT, produces = {"application/pdf", "application/json"})
    public Mono<byte[]> readReceipt(@PathVariable String id) {
        return this.ticketService.readReceipt(id);
    }

    @GetMapping(SEARCH)
    public Flux<TicketBasicDto> findByIdOrReferenceLikeOrUserMobileLikeNullSafe(@RequestParam(required = false) String key) {
        return this.ticketService.findByIdOrReferenceLikeOrUserMobileLikeNullSafe(key)
                .map(TicketBasicDto::new);
    }

    @GetMapping(ID_ID)
    public Mono<TicketEditionDto> findById(@PathVariable String id) {
        return this.ticketService.findById(id)
                .map(TicketEditionDto::new);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(REFERENCE_ID + REFERENCE)
    public Mono<TicketEditionDto> findByReference(@PathVariable String reference) {
        return this.ticketService.findByReference(reference)
                .map(TicketEditionDto::new);
    }

    @GetMapping(REFERENCE_ID + REFERENCE + SELECTED)
    public Mono<TicketSelectedDto> findSelectedByReference(@PathVariable String reference) {
        return this.ticketService.findByReference(reference)
                .map(TicketSelectedDto::new);
    }

    @PutMapping(ID_ID)
    public Mono<TicketEditionDto> update(@PathVariable String id, @Valid @RequestBody List<Shopping> shoppingList) {
        return this.ticketService.update(id, shoppingList)
                .map(TicketEditionDto::new);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(SEARCH + BOUGHT_ARTICLES)
    public Flux<ArticleNewDto> findAllBoughtArticlesByMobile(@RequestHeader("Authorization") String token) {
        String extractedToken = this.jwtService.extractBearerToken(token);
        return this.ticketService.findAllBoughtArticlesByMobile(this.jwtService.user(extractedToken))
                .map(ArticleNewDto::new);
    }

    @GetMapping(SEARCH + NO_INVOICE)
    public Mono<TicketReferencesDto> findAllWithoutInvoice() {
        return this.ticketService.findAllWithoutInvoice()
                .collectList()
                .map(TicketReferencesDto::new);
    }

    @PostMapping(SEARCH + TRACKING)
    public Flux<UserBasicDto> findTracking(@RequestBody List<Tracking> data) {
        return this.ticketService
                .findByBarcodeAndAmountList(data)
                .map(ticket -> new UserBasicDto(ticket.getUser()));
    }

    @PatchMapping(TRACKING)
    public Flux<Ticket> updateState(@RequestParam("state") ShoppingState state, @RequestBody List<Tracking> data) {
        return this.ticketService.updateByBarcodeAndAmountList(data, state);
    }

}
