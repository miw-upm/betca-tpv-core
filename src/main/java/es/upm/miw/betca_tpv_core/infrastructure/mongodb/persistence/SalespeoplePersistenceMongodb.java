package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
import es.upm.miw.betca_tpv_core.domain.persistence.SalespeoplePersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.SalespeopleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.SalespeopleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public class SalespeoplePersistenceMongodb implements SalespeoplePersistence {

    private SalespeopleReactive salespeopleReactive;

    @Autowired
    public SalespeoplePersistenceMongodb(SalespeopleReactive salespeopleReactive) {
        this.salespeopleReactive = salespeopleReactive;
    }


    @Override
    public Mono<Salespeople> creat(Salespeople salespeople) {
        SalespeopleEntity salespeopleEntity = new SalespeopleEntity(salespeople);
        return this.salespeopleReactive.save(salespeopleEntity).map(SalespeopleEntity::toSalespeople);
    }

    @Override
    public Flux<Salespeople> findBySalespersonAndSalesDateBetween(String salesperson, LocalDate dateBegin, LocalDate dateEnd) {
        return this.salespeopleReactive.findBySalespersonAndSalesDateBetween(salesperson, dateBegin, dateEnd)
                .map(SalespeopleEntity::toSalespeople);
    }

    @Override
    public Flux<Salespeople> findBySalesDateBetween(LocalDate dateBegin, LocalDate dateEnd) {
        return this.salespeopleReactive.findBySalesDateBetween(dateBegin, dateEnd)
                .map(SalespeopleEntity::toSalespeople);
    }

}
