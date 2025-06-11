package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ArticleAuditDto {
    private String barcode;
    private Integer stock;
    private Integer real;
    private String description;
    private BigDecimal retailPrice; // Añadido para cálculo de pérdidas
}