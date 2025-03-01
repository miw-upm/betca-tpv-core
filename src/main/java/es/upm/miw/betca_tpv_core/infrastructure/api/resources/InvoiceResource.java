package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import es.upm.miw.betca_tpv_core.domain.services.InvoiceService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(InvoiceResource.INVOICES)
public class InvoiceResource {
    public static final String INVOICES = "/invoices";
    public static final String TICKET_SEARCH = "/ticket-search";
    public static final String RECEIPT = "/receipt";
    public static final String MOBILE_SEARCH = "/mobile-search";
    public static final String IDENTITY_ID = "/{identity}";

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

    @GetMapping(IDENTITY_ID)
    public Mono<Invoice> read(@PathVariable Integer identity) {
        return this.invoiceService.read(identity);
    }

    @GetMapping(value = IDENTITY_ID + RECEIPT, produces = {"application/pdf", "application/json"})
    public Mono<byte[]> readReceipt(@PathVariable Integer identity) {
        return this.invoiceService.readReceipt(identity);
    }

    @GetMapping(MOBILE_SEARCH)
    public Flux<Invoice> findByMobile(@RequestParam() String mobile) {
        return this.invoiceService.findByUserMobile(mobile);
    }

    @GetMapping
    public Flux<Invoice> findAll() {
        return this.invoiceService.findAll();
    }
}