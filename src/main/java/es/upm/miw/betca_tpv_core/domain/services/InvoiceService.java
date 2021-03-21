package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.persistence.InvoicePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.domain.services.utils.PdfInvoiceBuilder;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class InvoiceService {

    private InvoicePersistence invoicePersistence;
    private TicketPersistence ticketPersistence;
    private UserMicroservice userMicroservice;

    @Autowired
    public InvoiceService(InvoicePersistence invoicePersistence, TicketPersistence ticketPersistence,
                          UserMicroservice userMicroservice) {
        this.invoicePersistence = invoicePersistence;
        this.ticketPersistence = ticketPersistence;
        this.userMicroservice = userMicroservice;
    }

    private Mono<User> readUserByUserMobileNullSafe(User user) {
        if (user != null) {
            return this.userMicroservice.readByMobile(user.getMobile());
        } else {
            return Mono.empty();
        }
    }


    protected Mono<Invoice> create(String idTicket) {
        Invoice invoice = Invoice.builder()
                .number(UUIDBase64.URL.encode())
                .creationDate(LocalDateTime.now())
                .build();

        return this.ticketPersistence.findByReference(idTicket)
                .switchIfEmpty(Mono.error(new NotFoundException("Ticket: " + idTicket)))
                .doOnNext(invoice::setTicket)
                .flatMap(ticket -> this.invoicePersistence.create(invoice));
    }

    protected Mono<byte[]> print(String invoiceId) {
        return this.invoicePersistence.findById(invoiceId)
                .flatMap(invoice -> this.readUserByUserMobileNullSafe(invoice.getTicket().getUser())
                        .map(user -> {
                            invoice.getTicket().setUser(user);
                            return invoice;
                        })
                        .switchIfEmpty(Mono.just(invoice)))
                .map(new PdfInvoiceBuilder()::generateInvoice);
    }

    public Mono<byte[]> createInvoiceAndPrint(String idTicket) {
        return create(idTicket)
                .flatMap(invoice -> print(invoice.getId()));
    }
}
