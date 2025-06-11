package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAuditPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.StockAuditReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Repository
public class StockAuditPersistenceMongodb implements StockAuditPersistence {

    private final StockAuditReactive stockAuditReactive;
    private final ArticleReactive articleReactive;

    @Autowired
    public StockAuditPersistenceMongodb(StockAuditReactive stockAuditReactive,
                                        ArticleReactive articleReactive) {
        this.stockAuditReactive = stockAuditReactive;
        this.articleReactive = articleReactive;
    }

    @Override
    public Flux<StockAudit> findAll() {
        return this.stockAuditReactive.findAll()
                .map(StockAuditEntity::toStockAudit);
    }

    @Override
    public Mono<StockAudit> read(String id) {
        return this.stockAuditReactive.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent StockAudit: " + id)))
                .map(StockAuditEntity::toStockAudit);
    }

    @Override
    public Mono<StockAudit> create(StockAudit stockAudit) {
        return articleReactive.findByDiscontinuedIsFalse()
                .collectList()
                .flatMap(articles -> {
                    List<ArticleAudit> articleAudits = articles.stream()
                            .map(article -> ArticleAudit.builder()
                                    .barcode(article.getBarcode())
                                    .description(article.getDescription())
                                    .stock(article.getStock())
                                    .real(null)
                                    .retailPrice(article.getRetailPrice())
                                    .build())
                            .collect(Collectors.toList());

                    stockAudit.setArticlesWithoutAudit(articleAudits);
                    stockAudit.setArticlesAudited(Collections.emptyList());

                    return stockAuditReactive.save(new StockAuditEntity(stockAudit))
                            .map(StockAuditEntity::toStockAudit);
                });
    }

    @Override
    public Mono<StockAudit> update(StockAudit stockAudit) {
        return stockAuditReactive.findById(stockAudit.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent StockAudit: " + stockAudit.getId())))
                .flatMap(entity -> {
                    entity.setArticlesAudited(toArticleAuditEntities(stockAudit.getArticlesAudited()));
                    entity.setArticlesWithoutAudit(toArticleAuditEntities(stockAudit.getArticlesWithoutAudit()));
                    entity.setLosses(toArticleLossEntities(stockAudit.getLosses()));
                    entity.setLossValue(stockAudit.getLossValue());
                    entity.setCloseDate(stockAudit.getCloseDate());
                    entity.setUpdateDate(LocalDateTime.now());
                    return stockAuditReactive.save(entity);
                })
                .map(StockAuditEntity::toStockAudit);
    }

    @Override
    public Mono<Void> delete(String id) {
        return stockAuditReactive.deleteById(id);
    }

    // Métodos auxiliares de conversión
    private List<ArticleAuditEntity> toArticleAuditEntities(List<ArticleAudit> audits) {
        if (audits == null) {
            return Collections.emptyList();
        }
        return audits.stream()
                .map(ArticleAuditEntity::new)
                .collect(Collectors.toList());
    }

    private List<ArticleLossEntity> toArticleLossEntities(List<ArticleLoss> losses) {
        if (losses == null) {
            return Collections.emptyList();
        }
        return losses.stream()
                .map(ArticleLossEntity::new)
                .collect(Collectors.toList());
    }
}