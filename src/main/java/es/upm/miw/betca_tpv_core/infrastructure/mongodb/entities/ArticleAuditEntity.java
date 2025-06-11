package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.ArticleAudit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ArticleAuditEntity {
    private String barcode;
    private String description;
    private Integer stock;
    private Integer real;
    private java.math.BigDecimal retailPrice; // si lo usas

    public ArticleAuditEntity(ArticleAudit articleAudit) {
        this.barcode = articleAudit.getBarcode();
        this.description = articleAudit.getDescription();
        this.stock = articleAudit.getStock();
        this.real = articleAudit.getReal();
        this.retailPrice = articleAudit.getRetailPrice();
    }

    // NUEVO: Constructor desde ArticleEntity
    public ArticleAuditEntity(ArticleEntity articleEntity) {
        this.barcode = articleEntity.getBarcode();
        this.description = articleEntity.getDescription();
        this.stock = articleEntity.getStock();
        this.real = null; // o el valor deseado
        this.retailPrice = articleEntity.getRetailPrice();
    }

    public ArticleAudit toArticleAudit() {
        return ArticleAudit.builder()
                .barcode(this.barcode)
                .description(this.description)
                .stock(this.stock)
                .real(this.real)
                .retailPrice(this.retailPrice)
                .build();
    }
}