package es.upm.miw.betca_tpv_core.domain.model;

import lombok.*;


import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor


public class ArticleAudit {
    private String barcode;
    private String description;
    private Integer stock;       // Stock teórico (BD)
    private Integer real;        // Stock real (usuario)
    private BigDecimal retailPrice; // Precio unitario para cálculo de pérdidas
}