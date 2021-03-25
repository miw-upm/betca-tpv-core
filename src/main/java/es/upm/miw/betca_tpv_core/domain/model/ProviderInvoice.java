package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.upm.miw.betca_tpv_core.domain.model.validations.PositiveBigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderInvoice {
    @NotNull
    private Integer number;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;
    @PositiveBigDecimal
    private BigDecimal baseTax;
    @PositiveBigDecimal
    private BigDecimal taxValue;
    @NotNull
    private String providerCompany;
    @NotNull
    private String orderId;
}
