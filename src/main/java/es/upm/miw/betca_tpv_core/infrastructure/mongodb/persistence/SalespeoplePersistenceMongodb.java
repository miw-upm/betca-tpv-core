package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
import es.upm.miw.betca_tpv_core.domain.persistence.SalespeoplePersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.SalespeopleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.SalespeopleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public class SalespeoplePersistenceMongodb implements SalespeoplePersistence {

    private SalespeopleReactive salespeopleReactive;
    private ArticleReactive articleReactive;
    private TicketReactive ticketReactive;

    @Autowired
    public SalespeoplePersistenceMongodb(SalespeopleReactive salespeopleReactive, ArticleReactive articleReactive, TicketReactive ticketReactive) {
        this.salespeopleReactive = salespeopleReactive;
        this.articleReactive=articleReactive;
        this.ticketReactive = ticketReactive;
    }

    @Override
    public Flux<Salespeople> findBySalespersonAndSalesDate(String salesperson, LocalDate localDate) {
        return this.salespeopleReactive.findBySalespersonAndSalesDate(salesperson, localDate)
                .map(SalespeopleEntity::toSalespeople);
    }

    @Override
    public Flux<Salespeople> findBySalesDate(LocalDate localDate) {
        return this.salespeopleReactive.findBySalesDate(localDate)
                .map(SalespeopleEntity::toSalespeople);
    }
}
