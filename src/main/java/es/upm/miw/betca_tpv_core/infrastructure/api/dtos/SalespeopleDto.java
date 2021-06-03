package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
import es.upm.miw.betca_tpv_core.domain.model.validations.PositiveBigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalespeopleDto {
    private String userMobile;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate localDate;
    private BigDecimal total;

    public SalespeopleDto(Salespeople salespeople) {
        this.userMobile = salespeople.getUserMobile();
        this.localDate = salespeople.getSalesDate();
        this.total = salespeople.getTotal();
    }

}
