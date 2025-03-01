package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.persistence.InvoicePersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.InvoiceReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public class InvoicePersistenceMongodb implements InvoicePersistence {

    private final InvoiceReactive invoiceReactive;

    @Autowired
    public InvoicePersistenceMongodb(InvoiceReactive invoiceReactive) {
        this.invoiceReactive = invoiceReactive;
    }

    private Mono<Integer> createInvoiceIdentity() {
        int actualYear = LocalDate.now().getYear();
        return invoiceReactive.findTopByOrderByIdentityDesc()
                .map(invoiceEntity -> invoiceEntity == null ?
                        actualYear * 10 + 1 :
                        Integer.parseInt(actualYear + Integer.toString(
                                Integer.parseInt(invoiceEntity
                                        .getIdentity()
                                        .toString()
                                        .substring(4))+1)));
    }

    @Override
    public Mono<Invoice> create(Invoice invoice) {
        return this.createInvoiceIdentity()
                .map(identity -> {
                    invoice.setIdentity(identity);
                    InvoiceEntity invoiceEntity = new InvoiceEntity();
                    BeanUtils.copyProperties(invoice, invoiceEntity);
                    invoiceEntity.setUserMobile(invoice.getUser().getMobile());
                    invoiceEntity.setTicketId(invoice.getTicket().getId());
                    return invoiceEntity;
                })
                .flatMap(this.invoiceReactive::save)
                .map(InvoiceEntity::toInvoice);
    }

    @Override
    public Mono<Invoice> findByTicketId(String ticketId) {
        return this.invoiceReactive.findByTicketId(ticketId)
                .switchIfEmpty(Mono.error(new NotFoundException("Invoice not found for ticket id: " + ticketId)))
                .map(InvoiceEntity::toInvoice);
    }

    @Override
    public Mono<Invoice> readByIdentity(Integer identity) {
        return this.invoiceReactive.findByIdentity(identity)
                .switchIfEmpty(Mono.error(new NotFoundException("Invoice not found with identity: " + identity)))
                .map(InvoiceEntity::toInvoice);
    }

    @Override
    public Flux<Invoice> findByUserMobile(String mobile) {
        return this.invoiceReactive.findByUserMobile(mobile)
                .switchIfEmpty(Mono.error(new NotFoundException("User mobile not found: " + mobile)))
                .map(InvoiceEntity::toInvoice);
    }

    @Override
    public Flux<Invoice> findAll() {
        return this.invoiceReactive.findAll().map(InvoiceEntity::toInvoice);
    }
}