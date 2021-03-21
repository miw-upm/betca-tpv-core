package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import es.upm.miw.betca_tpv_core.domain.persistence.InvoicePersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.InvoiceReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.InvoiceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class InvoicePersistenceMongodb implements InvoicePersistence {

    private InvoiceReactive invoiceReactive;
    private TicketReactive ticketReactive;

    @Autowired
    public InvoicePersistenceMongodb(InvoiceReactive invoiceReactive, TicketReactive ticketReactive) {
        this.invoiceReactive = invoiceReactive;
        this.ticketReactive = ticketReactive;
    }

    @Override
    public Mono<Invoice> create(Invoice invoice) {
        InvoiceEntity invoiceEntity = new InvoiceEntity(invoice);
        String ticketRef = invoice.getTicket() != null ? invoice.getTicket().getReference() : null;
        return this.ticketReactive.findByReference(ticketRef)
                .switchIfEmpty(Mono.error(new NotFoundException("Ticket Ref: " + ticketRef)))
                .doOnNext(invoiceEntity::addTicket)
                .then(invoiceReactive.save(invoiceEntity))
                .map(InvoiceEntity::toInvoice);
    }

    @Override
    public Mono<Invoice> findById(String id) {
        return this.invoiceReactive.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Ticket Id: " + id)))
                .map(InvoiceEntity::toInvoice);
    }


}
