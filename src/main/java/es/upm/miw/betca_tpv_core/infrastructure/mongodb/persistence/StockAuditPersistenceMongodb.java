package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAuditPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.StockAuditReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class StockAuditPersistenceMongodb implements StockAuditPersistence {
    private StockAuditReactive stockAuditReactive;

    @Autowired
    public StockAuditPersistenceMongodb(StockAuditReactive stockAuditReactive) {
        this.stockAuditReactive = stockAuditReactive;
    }

    @Override
    public Mono<StockAudit> findFirstByCloseDateNull() {
        return this.stockAuditReactive
                .findFirstByCloseDateNull()
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent Audit Opened")))
                .map(StockAuditEntity::toStockAudit);
    }
}