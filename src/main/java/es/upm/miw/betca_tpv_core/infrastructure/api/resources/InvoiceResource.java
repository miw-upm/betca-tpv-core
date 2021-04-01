package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.services.InvoiceService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.InvoiceItemDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.TicketBasicDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(InvoiceResource.INVOICES)
public class InvoiceResource {
    public static final String INVOICES = "/invoices";
    public static final String SEARCH = "/search";
    public static final String TICKET_REF = "/ticketRef";

    private InvoiceService invoiceService;

    @Autowired
    public InvoiceResource(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping(produces = {"application/pdf", "application/json"})
    public Mono<byte[]> createInvoiceAndPrint(@RequestBody TicketBasicDto ticketDto) {

        return this.invoiceService.createInvoiceAndPrint(ticketDto.getReference());
    }

    @GetMapping(SEARCH)
    public Flux<InvoiceItemDto> findByPhoneAndTicketIdNullSafe(@RequestParam(required = false) String userPhone,
                                                        @RequestParam(required = false) String ticketId){
        return this.invoiceService.findByPhoneAndTicketIdNullSafe(userPhone, ticketId)
                .map(InvoiceItemDto::new);
    }

    @PostMapping(TICKET_REF)
    public Mono<InvoiceItemDto> createFromTicketRef(@RequestBody TicketBasicDto ticketDto){
        return this.invoiceService.createFromTicketRef(ticketDto.getReference())
                .map(InvoiceItemDto::new);
    }
}
