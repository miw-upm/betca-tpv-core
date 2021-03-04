package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.services.TicketService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.TicketBasicDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(TicketResource.TICKETS)
public class TicketResource {
    public static final String TICKETS = "/tickets";

    public static final String ID_KEY = "/{key}";
    public static final String ID_ID = "/{id}";
    public static final String RECEIPT = "/receipt";

    private TicketService ticketService;

    @Autowired
    public TicketResource(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono< Ticket > create(@Valid @RequestBody Ticket ticket) {
        return this.ticketService.create(ticket);
    }

    @GetMapping(value = ID_ID + RECEIPT, produces = {"application/pdf", "application/json"})
    public Mono< byte[] > readReceipt(@PathVariable String id) {
        return this.ticketService.readReceipt(id);
    }

    @GetMapping(ID_KEY)
    public Flux<TicketBasicDto> findByIdLikeOrReferenceLikeOrUserMobileLike(@PathVariable String key) {
        return this.ticketService.findByIdLikeOrReferenceLikeOrUserMobileLike(key, key, key)
                .map(TicketBasicDto::new);
    }

}
