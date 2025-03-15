package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.ArticleLoss;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class ArticleLossEntity {

    private String barcode;
    private Double amount;

    public ArticleLoss toArticleLoss(){
        ArticleLoss articleLoss = new ArticleLoss();
        articleLoss.setAmount(amount);
        articleLoss.setBarcode(barcode);
        return articleLoss;
    }

}
