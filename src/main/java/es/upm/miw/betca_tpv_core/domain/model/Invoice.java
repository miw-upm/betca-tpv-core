package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Invoice {

    @Positive
    private Integer identity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @NotNull
    private Ticket ticket;

    private BigDecimal baseTax;

    private BigDecimal taxValue;

    private User user;

    public void doDefault() {
        if (Objects.isNull(baseTax)) {
            this.baseTax = BigDecimal.ZERO;
        }

        if (Objects.isNull(taxValue)) {
            this.taxValue = BigDecimal.ZERO;
        }
    }
}