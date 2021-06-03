package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
import es.upm.miw.betca_tpv_core.domain.persistence.SalespeoplePersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.SalespeopleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.SalespeopleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public class SalespeoplePersistenceMongodb implements SalespeoplePersistence {

    private SalespeopleReactive salespeopleReactive;
    private TicketReactive ticketReactive;

    @Autowired
    public SalespeoplePersistenceMongodb(SalespeopleReactive salespeopleReactive, TicketReactive ticketReactive) {
        this.salespeopleReactive = salespeopleReactive;
        this.ticketReactive = ticketReactive;
    }


    @Override
    public Mono<Salespeople> creat(Salespeople salespeople) {
        return Mono.justOrEmpty(salespeople.getTicketId())
                .flatMap(ticketId -> this.ticketReactive.findById(salespeople.getTicketId())
                        .switchIfEmpty(Mono.error(new NotFoundException("This ticketID not existent" + salespeople.getTicketId())
                        )))
                .map(ticketEntity -> new SalespeopleEntity(salespeople, ticketEntity))
                .flatMap(this.salespeopleReactive::save)
                .map(SalespeopleEntity::toSalespeople);
    }

    @Override
    public Flux<Salespeople> findBySalespersonAndSalesDateBetween(String userMobile, LocalDate dateBegin, LocalDate dateEnd) {
        return this.salespeopleReactive.findByUserMobileAndSalesDateBetween(userMobile, dateBegin, dateEnd)
                .map(SalespeopleEntity::toSalespeople);
    }

    @Override
    public Flux<Salespeople> findBySalesDateBetween(LocalDate dateBegin, LocalDate dateEnd) {
        return this.salespeopleReactive.findBySalesDateBetween(dateBegin, dateEnd)
                .map(SalespeopleEntity::toSalespeople);
    }

}
