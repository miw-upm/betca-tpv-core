package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.InvoicePersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
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

    @Autowired
    public InvoiceService(InvoicePersistence invoicePersistence, ArticlePersistence articlePersistence, TicketPersistence ticketPersistence) {
        this.invoicePersistence = invoicePersistence;
        this.articlePersistence = articlePersistence;
        this.ticketPersistence = ticketPersistence;
    }

    public Mono<Invoice> findByTicketId(String ticketId) {
        return invoicePersistence.findByTicketId(ticketId);
    }

    private Mono<Void> validateExistingInvoice(Ticket ticket) {
        return this.findByTicketId(ticket.getId()).flatMap(exists -> Mono.error(new ConflictException("Invoice already exists in database")));
    }

    public Mono<Invoice> create(Invoice invoice) {
        List<Shopping> shoppingList = invoice.getTicket().getShoppingList();

        return ticketPersistence.readById(invoice.getTicket().getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Ticket not found")))
                .flatMap(ticket -> validateExistingInvoice(ticket)
                        .then(getTotalTaxes(shoppingList, invoice)
                                .flatMap(invoice1 -> {
                                    invoice1.setCreationDate(LocalDateTime.now());
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
}