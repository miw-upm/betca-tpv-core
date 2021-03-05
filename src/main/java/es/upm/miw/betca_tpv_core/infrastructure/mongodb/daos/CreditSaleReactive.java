package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CreditSaleEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface CreditSaleReactive extends ReactiveSortingRepository<CreditSaleEntity, String > {
    Flux<CreditSaleEntity> findByPayed(Boolean payed);
}
