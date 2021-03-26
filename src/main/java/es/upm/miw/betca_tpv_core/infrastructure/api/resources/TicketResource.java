package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.configuration.JwtService;
import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.model.Tracking;
import es.upm.miw.betca_tpv_core.domain.services.TicketService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ArticleNewDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.TicketBasicDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.TicketEditionDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.UserBasicDto;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.logging.Logger;

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

    private TicketService ticketService;
    private JwtService jwtService;

    @Autowired
    public TicketResource(TicketService ticketService, JwtService jwtService) {
        this.ticketService = ticketService;
        this.jwtService = jwtService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono< Ticket > create(@Valid @RequestBody Ticket ticket) {
        return this.ticketService.create(ticket);
    }

    @GetMapping(value = ID_ID + RECEIPT, produces = {"application/pdf", "application/json"})
    public Mono< byte[] > readReceipt(@PathVariable String id) {
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

    @GetMapping(REFERENCE_ID + REFERENCE)
    public Mono<TicketEditionDto> findByReference(@PathVariable String reference) {
        return this.ticketService.findByReference(reference)
                .map(TicketEditionDto::new);
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

    @PostMapping(SEARCH + TRACKING)
    public Flux<UserBasicDto> findTracking(@RequestBody List<Tracking> data) {
        return Flux
                .fromIterable(data)
                .flatMap(da -> this.ticketService.findByBarcodeAndAmount(da.getBarcode(), da.getAmount()));
    }

}
