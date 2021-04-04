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
import reactor.core.publisher.Flux;
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


    protected Mono<Invoice> create(String ticketRef) {
        Invoice invoice = Invoice.builder()
                .number(UUIDBase64.URL.encode())
                .creationDate(LocalDateTime.now())
                .build();

        return this.ticketPersistence.findByReference(ticketRef)
                .switchIfEmpty(Mono.error(new NotFoundException("Ticket Ref: " + ticketRef)))
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

    public Mono<byte[]> createInvoiceAndPrint(String ticketRef) {
        return create(ticketRef)
                .flatMap(invoice -> print(invoice.getId()));
    }

    public Mono<byte[]> printByNumber(String number) {
        return this.invoicePersistence.findByNumber(number)
                .flatMap(invoice -> this.readUserByUserMobileNullSafe(invoice.getTicket().getUser())
                        .map(user -> {
                            invoice.getTicket().setUser(user);
                            return invoice;
                        })
                        .switchIfEmpty(Mono.just(invoice)))
                .map(new PdfInvoiceBuilder()::generateInvoice);
    }

    public Flux<Invoice> findByPhoneAndTicketIdNullSafe(String phoneUser, String ticketId) {
        return this.invoicePersistence.findByPhoneAndTicketIdNullSafe(phoneUser, ticketId);
    }

    public Mono<Invoice> createFromTicketRef(String ticketRef) {
        return this.create(ticketRef)
                .flatMap(invoice -> this.readUserByUserMobileNullSafe(invoice.getTicket().getUser())
                        .map(user -> {
                            invoice.getTicket().setUser(user);
                            return invoice;
                        })
                        .switchIfEmpty(Mono.just(invoice)));
    }

    public Mono<Invoice> findByNumber(String number) {
        return this.invoicePersistence.findByNumber(number)
                .flatMap(invoice -> this.readUserByUserMobileNullSafe(invoice.getTicket().getUser())
                        .map(user -> {
                            invoice.getTicket().setUser(user);
                            return invoice;
                        })
                        .switchIfEmpty(Mono.just(invoice)));
    }
}
