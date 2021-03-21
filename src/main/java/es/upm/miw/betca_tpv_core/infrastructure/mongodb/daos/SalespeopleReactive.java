package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.SalespeopleEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.Month;

public interface SalespeopleReactive extends ReactiveSortingRepository<SalespeopleEntity,String> {
    Flux<SalespeopleEntity>  findBySalespersonAndSalesDate(String salesperson, LocalDate localDate);
    Flux<SalespeopleEntity> findBySalesDate(LocalDate localDate);
}
