package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.ArticleLoss;
import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    @Singular("stockAuditArticle")
    private List<StockAuditArticleEntity> stockAuditArticleList;

    public StockAuditEntity(StockAudit stockAudit) {
        BeanUtils.copyProperties(stockAudit, this);
        this.stockAuditArticleList = stockAudit.getBarcodesWithoutAudit()
                .stream()
                .map(barcode -> new StockAuditArticleEntity(barcode, 0, false))
                .collect(Collectors.toList());
    }

    public List<String> toBarcodesWithoutAudit() {
        List<StockAuditArticleEntity> stockAuditArticles = this.getStockAuditArticleList();
        return  stockAuditArticles
                .stream()
                .filter(stockAuditArticle -> !stockAuditArticle.getAudited())
                .map(StockAuditArticleEntity::getBarcode)
                .collect(Collectors.toList());
    }

    public List<ArticleLoss> toArticleLosses() {
        List<StockAuditArticleEntity> stockAuditArticles = this.getStockAuditArticleList();
        return  stockAuditArticles
                .stream()
                .filter(stockAuditArticle -> (stockAuditArticle.getAudited()) && (stockAuditArticle.getAmount() > 0))
                .map(StockAuditArticleEntity::toArticleLoss)
                .collect(Collectors.toList());

    }

    public StockAudit toStockAudit() {
        StockAudit stockAudit = new StockAudit();
        BeanUtils.copyProperties(this, stockAudit);
        stockAudit.setBarcodesWithoutAudit(this.toBarcodesWithoutAudit());
        stockAudit.setLosses(this.toArticleLosses());
        return stockAudit;
    }
}
