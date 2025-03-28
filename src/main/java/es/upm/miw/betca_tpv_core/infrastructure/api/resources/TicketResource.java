package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.services.TicketService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(TicketResource.TICKETS)
public class TicketResource {
    public static final String TICKETS = "/tickets";

    public static final String ID_ID = "/{id}";
    public static final String RECEIPT = "/receipt";

    public static final String  REFERENCE_REFERENCE = "/{reference}";
    public static final String REFERENCE = "/reference";

    public static final String DATA = "/data";

    private final TicketService ticketService;

    @Autowired
    public TicketResource(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Ticket> create(@Valid @RequestBody Ticket ticket) {
        return this.ticketService.create(ticket);
    }

    @GetMapping(value = ID_ID + RECEIPT, produces = {"application/pdf", "application/json"})
    public Mono<byte[]> readReceipt(@PathVariable String id) {
        return this.ticketService.readReceipt(id);
    }

    @GetMapping(value = REFERENCE_REFERENCE + REFERENCE, produces = {"application/pdf","application/json"})
    public Mono<byte[]> readReceiptByReference(@PathVariable String reference) {

        return this.ticketService.readByReference(reference);
    }

    @GetMapping(value = REFERENCE_REFERENCE + REFERENCE + DATA, produces = {"application/json"})
    public Mono<Ticket> readReceiptByReferenceData(@PathVariable String reference) {
        return this.ticketService.readByReferenceData(reference);
    }
}
