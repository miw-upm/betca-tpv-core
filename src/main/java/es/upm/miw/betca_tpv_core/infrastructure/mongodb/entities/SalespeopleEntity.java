package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Document
@Builder
@AllArgsConstructor
public class SalespeopleEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String salesperson;
    private LocalDate salesDate;

    private String ticketBarcode;
    private String[] articleBarcode;
    private Integer amount;
    private BigDecimal total;


    public SalespeopleEntity(Salespeople salespeople) {
        BeanUtils.copyProperties(salespeople, this);
    }

    public Salespeople toSalespeople() {
        Salespeople salespeople = new Salespeople();
        BeanUtils.copyProperties(this, salespeople);
        return salespeople;
    }
}
