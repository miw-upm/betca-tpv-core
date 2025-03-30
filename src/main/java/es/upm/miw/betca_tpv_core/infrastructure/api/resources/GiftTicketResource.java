package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import es.upm.miw.betca_tpv_core.domain.services.GiftTicketService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.GiftTicketDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(GiftTicketResource.GIFTTICKETS)
public class GiftTicketResource {
    public static final String GIFTTICKETS = "/gift_tickets";
    public static final String REFERENCE_GIFTTICKETS = "/{referenceGiftTickets}";
    public static final String RECEIPT = "/receipt";


    private final GiftTicketService giftTicketService;

    @Autowired
    public GiftTicketResource(GiftTicketService giftTicketService) {
        this.giftTicketService = giftTicketService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<GiftTicket> create(@Valid @RequestBody GiftTicketDto giftTicketDto) {
        return this.giftTicketService.create(giftTicketDto);
    }

    @GetMapping(value = REFERENCE_GIFTTICKETS + RECEIPT, produces = {"application/pdf", "application/json"})
    public Mono<byte[]> readReceiptForReceiptByReference(@PathVariable String referenceGiftTickets) {
        return this.giftTicketService.readForReceiptByReference(referenceGiftTickets);
    }

}