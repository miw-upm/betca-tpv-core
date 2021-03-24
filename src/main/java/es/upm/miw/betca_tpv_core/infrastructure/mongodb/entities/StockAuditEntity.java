package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

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
import java.util.ArrayList;
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
    private BigDecimal lossValue;
    private List<StockAuditArticleEntity> stockAuditArticleList;

    public StockAuditEntity(StockAudit stockAudit) {
        BeanUtils.copyProperties(stockAudit, this);
        this.stockAuditArticleList = new ArrayList<>();
    }

    public String[] toBarcodesWithoutAudit(){
        List<StockAuditArticleEntity> stockAuditArticles = this.getStockAuditArticleList();
        return (String[]) stockAuditArticles
                .stream()
                .filter(stockAuditArticle -> !stockAuditArticle.getAudited())
                .map(StockAuditArticleEntity::getBarcode)
                .toArray();
    }

    public ArticleLoss[] toArticleLosses(){
        List<StockAuditArticleEntity> stockAuditArticles = this.getStockAuditArticleList();
        return (ArticleLoss[]) stockAuditArticles
                .stream()
                .filter(stockAuditArticle -> (stockAuditArticle.getAudited()) && (stockAuditArticle.getAmount() > 0))
                .map(StockAuditArticleEntity::toArticleLoss)
                .toArray();
    }
}
