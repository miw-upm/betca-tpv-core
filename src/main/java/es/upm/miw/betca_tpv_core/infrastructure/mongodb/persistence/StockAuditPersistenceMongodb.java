package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAuditPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.StockAuditReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

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
                //.switchIfEmpty(Mono.just(new StockAuditEntity(new StockAudit())))
                .map(StockAuditEntity::toStockAudit);
    }

    @Override
    public Mono<StockAudit> create(List<String> barcodesWithoutAudit) {
        StockAudit stockAudit = new StockAudit();
        stockAudit.setCreationDate(LocalDateTime.now());
        stockAudit.setBarcodesWithoutAudit(barcodesWithoutAudit);

        StockAuditEntity stockAuditEntity = new StockAuditEntity(stockAudit);
        return this.stockAuditReactive
                .save(stockAuditEntity)
                .map(StockAuditEntity::toStockAudit);
    }
}
