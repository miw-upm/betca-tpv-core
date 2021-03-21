package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Invoice;
import es.upm.miw.betca_tpv_core.domain.persistence.InvoicePersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.InvoiceReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.InvoiceEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ShoppingEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TicketEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public class InvoicePersistenceMongodb implements InvoicePersistence {

    private InvoiceReactive invoiceReactive;
    private TicketReactive ticketReactive;

    @Autowired
    public InvoicePersistenceMongodb(InvoiceReactive invoiceReactive, TicketReactive ticketReactive) {
        this.invoiceReactive = invoiceReactive;
        this.ticketReactive = ticketReactive;
    }


    private Mono<BigDecimal> totalBaseTax(TicketEntity ticketEntity){
        return Flux.fromStream(ticketEntity.getShoppingEntityList().stream())
                .map(ShoppingEntity::baseTaxValue)
                .reduce(BigDecimal::add);
    }

    private Mono<BigDecimal> totalTaxValue(TicketEntity ticketEntity){
        return Flux.fromStream(ticketEntity.getShoppingEntityList().stream())
                .map(ShoppingEntity::taxValue)
                .reduce(BigDecimal::add);
    }

    @Override
    public Mono<Invoice> create(Invoice invoice) {
        InvoiceEntity invoiceEntity = new InvoiceEntity(invoice);
        String ticketRef = invoice.getTicket() != null ? invoice.getTicket().getReference() : null;
        return this.ticketReactive.findByReference(ticketRef)
                .switchIfEmpty(Mono.error(new NotFoundException("Ticket Ref: " + ticketRef)))
                .doOnNext(invoiceEntity::addTicket)
                .flatMap(ticketEntity -> totalBaseTax(ticketEntity)
                        .map(totalBaseTax -> {
                            invoiceEntity.setBaseTax(totalBaseTax);
                            return invoiceEntity;
                        }))
                .flatMap(invoiceEntity1 -> totalTaxValue(invoiceEntity1.getTicketEntity())
                        .map(totalBaseTax -> {
                            invoiceEntity.setTaxValue(totalBaseTax);
                            return invoiceEntity;
                        }))
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
