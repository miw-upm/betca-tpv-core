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
    private List<ArticleEntity> articlesWithoutAudit;
    private BigDecimal lossValue;
    private List<ArticleLossEntity> losses;
    private List<ArticleEntity> articlesAudited;

    public StockAuditEntity(StockAudit stockAudit, List<ArticleEntity> articles) {
        BeanUtils.copyProperties(stockAudit, this);
        this.articlesAudited = articles;
    }

    public StockAudit toStockAudit() {
        StockAudit stockAudit = new StockAudit();
        BeanUtils.copyProperties(this, stockAudit);
        stockAudit.setLosses(toLosses());
        stockAudit.setArticlesWithoutAudit(toArticles(this.getArticlesWithoutAudit()));
        stockAudit.setArticlesAudited(toArticles(this.getArticlesAudited()));
        return stockAudit;
    }

    private List<ArticleLoss> toLosses() {
        if (losses == null)
            return Collections.emptyList();
        return losses.stream()
                .map(ArticleLossEntity::toArticleLoss)
                .toList();
    }

    private List<Article> toArticles(List<ArticleEntity> articles) {
        if (articles == null)
            return Collections.emptyList();
        return articles.stream()
                .map(ArticleEntity::toArticle)
                .toList();
    }
}
