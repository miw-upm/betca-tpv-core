package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import es.upm.miw.betca_tpv_core.domain.persistence.CreditSalePersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CreditSaleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CreditSaleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class CreditSalePersistenceMongodb implements CreditSalePersistence {

    private TicketReactive ticketReactive;
    private CreditSaleReactive creditSaleReactive;

    @Autowired
    public CreditSalePersistenceMongodb(TicketReactive ticketReactive, CreditSaleReactive creditSaleReactive) {
        this.ticketReactive = ticketReactive;
        this.creditSaleReactive = creditSaleReactive;
    }

    @Override
    public Mono<CreditSale> create(CreditSale creditSale) {
        return Mono.justOrEmpty(creditSale.getTicketReference())
                .flatMap(ticketReference -> this.ticketReactive.findByReference(creditSale.getTicketReference())
                        .switchIfEmpty(Mono.error(
                                new NotFoundException("Non existent ticket with the reference " + creditSale.getTicketReference())
                        ))
                )
                .map(ticketEntity -> new CreditSaleEntity(creditSale, ticketEntity))
                .flatMap(this.creditSaleReactive::save)
                .map(CreditSaleEntity::toCreditSale);
    }

    @Override
    public Flux<CreditSale> findByPayed(Boolean payed) {
        return this.creditSaleReactive.findByPayed(payed)
                .map(CreditSaleEntity::toCreditSale);
    }

    @Override
    public Mono<CreditSale> findByReference(String reference) {
        return this.creditSaleReactive.findByReference(reference)
                .map(CreditSaleEntity::toCreditSale);
    }
}
