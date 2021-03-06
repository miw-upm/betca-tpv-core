package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CreditEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

public interface CreditReactive extends ReactiveSortingRepository<CreditEntity, String > {
    Mono<CreditEntity> findByUserReference(String userReference);
}
