package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import es.upm.miw.betca_tpv_core.domain.services.GiftTicketService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(GiftTicketResource.GIFTTICKETS)
public class GiftTicketResource {

    public static final String GIFTTICKETS = "/giftTickets";
    public static final String ID_ID = "/{id}";
    public static final String RECEIPT = "/receipt";

    private GiftTicketService giftTicketService;

    @Autowired
    public GiftTicketResource(GiftTicketService giftTicketService) {
        this.giftTicketService = giftTicketService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<GiftTicket> create(@Valid @RequestBody GiftTicket giftTicket) {
        return this.giftTicketService.create(giftTicket);
    }

    @GetMapping(value = ID_ID + RECEIPT, produces = {"application/pdf", "application/json"})
    public Mono< byte[] > readReceipt(@PathVariable String id) {
        return this.giftTicketService.readReceipt(id);
    }
}
