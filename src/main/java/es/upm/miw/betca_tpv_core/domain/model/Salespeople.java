package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Salespeople {
    @NotBlank
    private String salesperson;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate salesDate;
    @NotBlank
    private String ticketId;
    private String[] articleBarcode;
    private Integer amount;
    private BigDecimal total;

    public static Salespeople ofSalespeopleSalesDateFinalValue(Salespeople salespeople) {
        return Salespeople.builder()
                .salesperson(salespeople.getSalesperson())
                .salesDate(salespeople.getSalesDate())
                .ticketId(salespeople.getTicketId())
                .articleBarcode(salespeople.getArticleBarcode())
                .amount(salespeople.getAmount())
                .total(salespeople.getTotal())
                .build();
    }

}
