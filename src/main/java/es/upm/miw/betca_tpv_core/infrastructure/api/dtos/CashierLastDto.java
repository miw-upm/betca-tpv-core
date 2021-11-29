package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import es.upm.miw.betca_tpv_core.domain.model.Cashier;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CashierLastDto {
    private Boolean closed;
    private BigDecimal finalCash;
    @JsonInclude(Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closureDate;

    public CashierLastDto(Cashier cashier) {
        this.closed = cashier.isClosed();
        this.finalCash = cashier.getFinalCash();
        this.closureDate = cashier.getClosureDate();
    }

}
