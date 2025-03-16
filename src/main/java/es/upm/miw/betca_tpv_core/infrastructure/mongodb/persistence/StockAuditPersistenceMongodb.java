package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.ArticleLoss;
import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAuditPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.StockAuditReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticleEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticleLossEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAuditEntity;

import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
        return this.stockAuditReactive.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent StockAudit: " + id)))
                .map(StockAuditEntity::toStockAudit);
    }

    @Override
    public Mono<String> save(StockAudit stockAudit) {
        return articleReactive.findByDiscontinuedIsFalse()
                .collectList()
                .flatMap(articles -> {
                    StockAuditEntity stockAuditEntity = new StockAuditEntity(stockAudit, articles);
                    return stockAuditReactive.save(stockAuditEntity);
                }).map(StockAuditEntity::getId);
    }

    @Override
    public Mono<Void> close(StockAudit stockAudit) {
        return stockAuditReactive.findById(stockAudit.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent StockAudit: " + stockAudit.getId())))
                .flatMap(data -> {
                    data.setLosses(getLossesEntity(stockAudit.getLosses()));
                    data.setCloseDate(stockAudit.getCloseDate());
                    data.setLossValue(stockAudit.getLossValue());
                    if(stockAudit.getArticlesAudited() != null){
                        data.setArticlesAudited(stockAudit.getArticlesAudited()
                                .stream().map(ArticleEntity::new).toList());
                    }
                    if(stockAudit.getArticlesWithoutAudit() != null){
                        data.setArticlesWithoutAudit(stockAudit.getArticlesWithoutAudit()
                                .stream().map(ArticleEntity::new).toList());
                    }
                    return Mono.just(data);
                })
                .flatMap(stockAuditReactive::save)
                .then();
    }

    private List<ArticleLossEntity> getLossesEntity(List<ArticleLoss> losses) {
        if (losses == null) {
            return Collections.emptyList();
        }
        return losses.stream()
                .map(ArticleLossEntity::new)
                .toList();
    }

    @Override
    public Mono<Void> update(String id) {
        return stockAuditReactive.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent StockAudit: " + id)))
                .flatMap(stockAudit -> {
                    if (stockAudit.getCloseDate() != null) {
                        return Mono.error(new IllegalStateException("A closed audit cannot be updated."));
                    }
                    stockAudit.setUpdateDate(LocalDateTime.now());
                    return articleReactive.findByDiscontinuedIsFalse()
                            .collectList()
                            .map(articles -> {
                                stockAudit.setArticlesWithoutAudit(articles);
                                return stockAudit;
                            });
                })
                .flatMap(stockAuditReactive::save)
                .then();
    }
}
