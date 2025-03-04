package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAuditPersistence;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StockAuditService {

    private final StockAuditPersistence stockAuditPersistence;

    public StockAuditService(StockAuditPersistence stockAuditPersistence) {
        this.stockAuditPersistence = stockAuditPersistence;
    }

    public Flux<StockAudit> findAll() {
        return stockAuditPersistence.findAll();
    }

    public Mono<StockAudit> read(String id){
        return stockAuditPersistence.read(id);
    }
}
