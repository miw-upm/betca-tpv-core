package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import es.upm.miw.betca_tpv_core.domain.services.GiftTicketService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(GiftTicketResource.GIFTTICKETS)
public class GiftTicketResource {

    public static final String GIFTTICKETS = "/giftTickets";

    private GiftTicketService giftTicketService;

    @Autowired
    public GiftTicketResource(GiftTicketService giftTicketService) {
        this.giftTicketService = giftTicketService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Void> create(@Valid @RequestBody GiftTicket giftTicket) {
        //return  Mono.empty();
        return this.giftTicketService.create(giftTicket);
    }
}
