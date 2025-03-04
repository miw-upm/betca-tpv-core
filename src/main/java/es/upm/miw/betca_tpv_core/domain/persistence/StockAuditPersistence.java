package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StockAuditPersistence {
    Flux<StockAudit> findAll();

    Mono<StockAudit> read(String id);
}
