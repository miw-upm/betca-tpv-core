package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
import es.upm.miw.betca_tpv_core.domain.persistence.SalespeoplePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Service
public class SalespeopleService {
    private SalespeoplePersistence salespeoplePersistence;

    @Autowired
    public SalespeopleService(SalespeoplePersistence salespeoplePersistence) {
        this.salespeoplePersistence = salespeoplePersistence;
    }

    public Flux<Salespeople> findBySalespersonAndSalesDateBetween(String salesperson, LocalDate dateBegin,LocalDate dateEnd){
        return this.salespeoplePersistence.findBySalespersonAndSalesDateBetween(salesperson,dateBegin,dateEnd);
    }

    public Flux<Salespeople> findBySalesDateBetween(LocalDate dateBegin,LocalDate dateEnd){
        return this.salespeoplePersistence.findBySalesDateBetween(dateBegin,dateEnd);
    }


}
