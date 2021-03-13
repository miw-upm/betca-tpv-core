package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.validations.PositiveBigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ArticleNewDto {
    @NotNull
    private String barcode;
    @NotBlank
    private String description;
    @PositiveBigDecimal
    private BigDecimal retailPrice;

    public ArticleNewDto(Shopping shopping) {
        this.barcode = shopping.getBarcode();
        this.description = shopping.getDescription();
        this.retailPrice = shopping.getRetailPrice();
    }

}
