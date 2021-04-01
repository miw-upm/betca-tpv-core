package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.SalespeopleEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface SalespeopleReactive extends ReactiveSortingRepository<SalespeopleEntity,String> {
    Flux<SalespeopleEntity>  findBySalespersonAndSalesDateBetween(String salesperson, LocalDate dateBegin,LocalDate dateEnd);
    Flux<SalespeopleEntity> findBySalesDateBetween(LocalDate dateBegin,LocalDate dateEnd);
}
