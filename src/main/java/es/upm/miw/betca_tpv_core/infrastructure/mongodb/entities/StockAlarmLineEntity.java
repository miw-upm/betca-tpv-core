package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAlarmLineEntity {
    private String barcode;
    private Integer warning;
    private Integer critical;
}
