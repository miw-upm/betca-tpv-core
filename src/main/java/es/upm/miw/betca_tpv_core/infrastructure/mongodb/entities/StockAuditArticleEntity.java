package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.ArticleLoss;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class StockAuditArticleEntity {
    @Indexed(unique = true)
    private String barcode;
    private Integer amount;
    private Boolean audited;

    public ArticleLoss toArticleLoss(){
        ArticleLoss articleLoss = new ArticleLoss();
        BeanUtils.copyProperties(this, articleLoss);
        return articleLoss;
    }
}
