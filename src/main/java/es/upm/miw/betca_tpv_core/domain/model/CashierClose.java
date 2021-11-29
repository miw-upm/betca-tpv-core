package es.upm.miw.betca_tpv_core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashierClose {
    @NotNull
    private BigDecimal finalCash;
    @NotNull
    private BigDecimal finalCard;
    @NotNull
    private String comment;
}
