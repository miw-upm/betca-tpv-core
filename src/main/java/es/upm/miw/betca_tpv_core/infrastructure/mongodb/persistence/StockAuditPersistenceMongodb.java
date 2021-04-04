package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.ArticleLoss;
import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAuditPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockAuditDto;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.StockAuditReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAuditArticleEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAuditEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public Mono<StockAuditDto> update(String id, StockAuditDto stockAuditDto, Boolean close) {
        return this.stockAuditReactive
                .findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent stock audit id: " + id)))
                .map(stockAuditEntity -> {
                    List<StockAuditArticleEntity> auditArticleEntityList = stockAuditEntity.getStockAuditArticleList();
                    auditArticleEntityList.forEach(auditArticle -> {
                        String articleBarcode = auditArticle.getBarcode();
                        List<String> barcodesWithoutAudit = stockAuditDto.getBarcodesWithoutAudit();
                        List<ArticleLoss> articleLosses = stockAuditDto.getLosses();
                        List<String> articleLossesBarcodes = articleLosses.stream().map(ArticleLoss::getBarcode).collect(Collectors.toList());

                        if (barcodesWithoutAudit.contains(articleBarcode)) {
                            auditArticle.setAmount(0);
                            auditArticle.setAudited(false);
                        } else if (articleLossesBarcodes.contains(articleBarcode)) {
                            Optional<ArticleLoss> lossArticle = articleLosses.stream()
                                    .filter(articleLoss -> articleLoss.getBarcode().equals(articleBarcode))
                                    .findFirst();
                            if (lossArticle.isPresent()) {
                                auditArticle.setAmount(lossArticle.get().getAmount());
                                auditArticle.setAudited(true);
                            }
                        } else {
                            auditArticle.setAmount(0);
                            auditArticle.setAudited(true);
                        }
                    });
                    if (close) {
                        stockAuditEntity.setCloseDate(LocalDateTime.now());
                    }
                    stockAuditEntity.setStockAuditArticleList(auditArticleEntityList);
                    return stockAuditEntity;
                })
                .flatMap(this.stockAuditReactive::save)
                .map(StockAuditEntity::toStockAudit)
                .map(stockAudit -> {
                    BeanUtils.copyProperties(stockAudit, stockAuditDto);
                    return stockAuditDto;
                });
    }
}
