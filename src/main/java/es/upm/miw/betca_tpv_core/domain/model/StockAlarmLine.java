package es.upm.miw.betca_tpv_core.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class StockAlarmLine {
    @NotBlank
    private Article article;
    private Integer warning;
    private Integer number;
}
