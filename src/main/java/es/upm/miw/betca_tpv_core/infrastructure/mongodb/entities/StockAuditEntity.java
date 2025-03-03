package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.ArticleLoss;
import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
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
    private List<ArticleEntity> articlesWithoutAudit;
    private Integer lossValue;
    private List<ArticleLossEntity> losses;

    public StockAudit toStockAudit() {
        StockAudit stockAudit = new StockAudit();
        BeanUtils.copyProperties(this, stockAudit);
        stockAudit.setLosses(toLosses());
        stockAudit.setArticlesWithoutAudit(toArticlesWithoutAudit());
        return stockAudit;
    }

    private List<ArticleLoss> toLosses() {
        if (losses == null)
            return null;
        return losses.stream()
                .map(ArticleLossEntity::toArticleLoss)
                .toList();
    }

    private List<Article> toArticlesWithoutAudit() {
        if (articlesWithoutAudit == null)
            return null;
        return articlesWithoutAudit.stream()
                .map(ArticleEntity::toArticle)
                .toList();
    }
}
