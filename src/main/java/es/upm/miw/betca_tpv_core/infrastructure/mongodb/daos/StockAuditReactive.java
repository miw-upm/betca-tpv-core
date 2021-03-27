package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAuditEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

public interface StockAuditReactive extends ReactiveSortingRepository<StockAuditEntity, String> {
    Mono<StockAuditEntity> findFirstByCloseDateNull();
}
