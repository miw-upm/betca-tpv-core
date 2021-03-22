package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.services.InvoiceService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.TicketBasicDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(InvoiceResource.INVOICES)
public class InvoiceResource {
    public static final String INVOICES = "/invoices";

    private InvoiceService invoiceService;

    @Autowired
    public InvoiceResource(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping(produces = {"application/pdf", "application/json"})
    public Mono<byte[]> createInvoiceAndPrint(@RequestBody TicketBasicDto ticketDto) {

        return this.invoiceService.createInvoiceAndPrint(ticketDto.getReference());
    }

}
