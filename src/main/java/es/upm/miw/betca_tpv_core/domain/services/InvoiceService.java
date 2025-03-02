package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.InvoicePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.domain.services.utils.PdfInvoiceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceService {
    private final InvoicePersistence invoicePersistence;
    private final ArticlePersistence articlePersistence;
    private final TicketPersistence ticketPersistence;
    private final UserMicroservice userMicroservice;

    @Autowired
    public InvoiceService(InvoicePersistence invoicePersistence, ArticlePersistence articlePersistence, TicketPersistence ticketPersistence, UserMicroservice userMicroservice) {
        this.invoicePersistence = invoicePersistence;
        this.articlePersistence = articlePersistence;
        this.ticketPersistence = ticketPersistence;
        this.userMicroservice = userMicroservice;
    }

    public Mono<Invoice> findByTicketId(String ticketId) {
        return invoicePersistence.findByTicketId(ticketId);
    }

    public Flux<Invoice> findByUserMobile(String mobile) {
        return this.invoicePersistence.findByUserMobile(mobile);
    }

    public Flux<Invoice> findAll(){
        return this.invoicePersistence.findAll();
    }

    public Mono<Invoice> read(Integer identity){
        return this.invoicePersistence.readByIdentity(identity);
    }

    private Mono<Void> validateExistingInvoice(Ticket ticket) {
        return this.findByTicketId(ticket.getId())
                .onErrorResume(NotFoundException.class, e -> Mono.empty())
                .flatMap(exists -> Mono.error(new ConflictException("Invoice already exists in database")));
    }

    public Mono<Invoice> create(Invoice invoice) {
        return ticketPersistence.readById(invoice.getTicket().getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Ticket not found")))
                .flatMap(ticket -> validateExistingInvoice(ticket)
                                .then(getTotalTaxes(ticket.getShoppingList(), invoice)
                                        .flatMap(invoice1 -> {invoice1.setCreationDate(LocalDateTime.now());
                                            return invoicePersistence.create(invoice1);
                                        })));
    }

    public Mono<Invoice> getTotalTaxes(List<Shopping> shoppingList, Invoice invoice) {
        return Flux.fromIterable(shoppingList)
                .flatMap(shopping -> articlePersistence.readByBarcode(shopping.getBarcode())
                        .map(article -> {
                            invoice.doDefault();
                            BigDecimal baseTax = article.getArticleBaseTax();
                            BigDecimal taxValue = article.getArticleTaxValue();
                            invoice.setBaseTax(invoice.getBaseTax().add(baseTax));
                            invoice.setTaxValue(invoice.getTaxValue().add(taxValue));
                            return invoice;
                        }))
                .last();
    }

    public Mono<byte[]> readReceipt (Integer identity){
        return this.invoicePersistence.readByIdentity(identity)
                .flatMap(invoice -> ticketPersistence.readById(invoice.getTicket().getId())
                        .doOnNext(invoice::setTicket)
                        .thenReturn(invoice))
                .map(new PdfInvoiceBuilder()::generateInvoice);
    }

    public Mono<Invoice> updateUser(Integer identity, String mobile) {
        return this.userMicroservice.readByMobile(mobile)
                .flatMap(user -> invoicePersistence.updateUser(identity, user));
    }
}