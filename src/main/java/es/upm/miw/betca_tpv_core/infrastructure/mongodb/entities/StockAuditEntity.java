package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.ArticleAudit;
import es.upm.miw.betca_tpv_core.domain.model.ArticleLoss;
import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class StockAuditEntity {

    @Id
    private String id;
    private LocalDateTime creationDate;
    private LocalDateTime closeDate;
    private LocalDateTime updateDate;
    private List<ArticleAuditEntity> articlesWithoutAudit;
    private BigDecimal lossValue;
    private List<ArticleLossEntity> losses;
    private List<ArticleAuditEntity> articlesAudited;

    public StockAuditEntity(StockAudit stockAudit) {
        BeanUtils.copyProperties(stockAudit, this);
        this.articlesWithoutAudit = toEntities(stockAudit.getArticlesWithoutAudit());
        this.articlesAudited = toEntities(stockAudit.getArticlesAudited());
    }

    public StockAudit toStockAudit() {
        StockAudit stockAudit = new StockAudit();
        BeanUtils.copyProperties(this, stockAudit);
        stockAudit.setArticlesWithoutAudit(toDomain(this.articlesWithoutAudit));
        stockAudit.setArticlesAudited(toDomain(this.articlesAudited));
        stockAudit.setLosses(toLosses(this.losses));
        return stockAudit;
    }

    private List<ArticleAuditEntity> toEntities(List<ArticleAudit> audits) {
        if (audits == null) return Collections.emptyList();
        return audits.stream().map(ArticleAuditEntity::new).toList();
    }

    private List<ArticleAudit> toDomain(List<ArticleAuditEntity> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(ArticleAuditEntity::toArticleAudit).toList();
    }

    private List<ArticleLoss> toLosses(List<ArticleLossEntity> losses) {
        if (losses == null) return Collections.emptyList();
        return losses.stream().map(ArticleLossEntity::toArticleLoss).toList();
    }

    // NUEVO: Método para convertir lista de ArticleEntity a ArticleAuditEntity
    public static List<ArticleAuditEntity> fromArticleEntities(List<ArticleEntity> articleEntities) {
        if (articleEntities == null) return Collections.emptyList();
        return articleEntities.stream()
                .map(ArticleAuditEntity::new)
                .toList();
    }
}