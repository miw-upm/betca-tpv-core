package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.domain.model.validations.PositiveBigDecimal;
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
    private String[] articleBarcodes;
    private String[] ticketBarcodes;
    private Integer numArticle;
    @PositiveBigDecimal
    private BigDecimal finalValue;

    public static Salespeople ofSalespeopleSalesDateFinalValue(Salespeople salespeople){
        return Salespeople.builder()
                .salesperson(salespeople.getSalesperson())
                .salesDate(salespeople.getSalesDate())
                .articleBarcodes(salespeople.getArticleBarcodes())
                .ticketBarcodes(salespeople.getTicketBarcodes())
                .numArticle(salespeople.getNumArticle())
                .finalValue(salespeople.getFinalValue())
                .build();
    }
}
