package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockAuditCreateDto {
    String id;

    public StockAuditCreateDto(String id) {
        this.id = id;
    }
}
