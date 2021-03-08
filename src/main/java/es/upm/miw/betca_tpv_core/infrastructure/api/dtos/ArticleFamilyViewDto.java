package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.domain.model.validations.PositiveBigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleFamilyViewDto {
    private String reference;
    private String description;
    private TreeType type;
    //@PositiveBigDecimal
    private BigDecimal price;
}
