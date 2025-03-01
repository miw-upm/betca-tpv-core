package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import es.upm.miw.betca_tpv_core.domain.services.InvoiceService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(InvoiceResource.INVOICES)
public class InvoiceResource {
    public static final String INVOICES = "/invoices";
    public static final String TICKET_SEARCH = "/ticket-search";

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceResource(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Invoice> create(@Valid @RequestBody Invoice invoice) {
        return this.invoiceService.create(invoice);
    }

    @GetMapping(TICKET_SEARCH)
    public Mono<Invoice> findByTicket(@RequestParam() String ticketId) {
        return this.invoiceService.findByTicketId(ticketId);
    }
}