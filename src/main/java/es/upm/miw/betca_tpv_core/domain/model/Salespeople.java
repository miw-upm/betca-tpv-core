package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.domain.model.validations.ListNotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    @ListNotEmpty
    private List<Ticket> ticketList;

    public static Salespeople ofSalespeopleSalesDateFinalValue(Salespeople salespeople){
        return Salespeople.builder()
                .salesperson(salespeople.getSalesperson())
                .salesDate(salespeople.getSalesDate())
                .ticketList(salespeople.getTicketList())
                .build();
    }

    public BigDecimal total(){
        return this.getTicketList().stream().flatMap(ticket -> ticket.getShoppingList().stream())
                .map(shopping -> shopping.getRetailPrice()).reduce(BigDecimal.ZERO,BigDecimal::add);
    }
}
