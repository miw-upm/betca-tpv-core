package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAuditPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.StockAuditReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class StockAuditPersistenceMongodb implements StockAuditPersistence {

    private final StockAuditReactive stockAuditReactive;

    private final ArticleReactive articleReactive;

    @Autowired
    public StockAuditPersistenceMongodb(StockAuditReactive stockAuditReactive, ArticleReactive articleReactive) {
        this.stockAuditReactive = stockAuditReactive;
        this.articleReactive = articleReactive;
    }

    @Override
    public Flux<StockAudit> findAll() {
        return this.stockAuditReactive.findAll().map(StockAuditEntity::toStockAudit);
    }

    @Override
    public Mono<StockAudit> read(String id) {
        return this.stockAuditReactive.findById(id).map(StockAuditEntity::toStockAudit);
    }

    @Override
    public Mono<Void> save(StockAudit stockAudit) {
        return articleReactive.findByDiscontinuedIsFalse()
                .collectList()
                .flatMap(articles -> {
                    StockAuditEntity stockAuditEntity = new StockAuditEntity(stockAudit, articles);
                    return stockAuditReactive.save(stockAuditEntity);
                }).then();
    }
}
