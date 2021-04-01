package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import es.upm.miw.betca_tpv_core.domain.model.ArticleLoss;
import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockAuditDto {
    private String id;
    private List<String> barcodesWithoutAudit;
    private BigDecimal lossValue;
    private List<ArticleLoss> losses;

    public StockAuditDto(StockAudit stockAudit) {
        this.id = stockAudit.getId();
        this.barcodesWithoutAudit = stockAudit.getBarcodesWithoutAudit();
        this.lossValue = stockAudit.getLossValue();
        this.losses = stockAudit.getLosses();
    }

}
