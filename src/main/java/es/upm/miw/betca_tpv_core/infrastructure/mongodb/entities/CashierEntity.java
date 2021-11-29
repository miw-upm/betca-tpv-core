package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Cashier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class CashierEntity {
    @Id
    private String id;
    private BigDecimal initialCash;
    private BigDecimal cashSales;
    private BigDecimal cardSales;
    private BigDecimal usedVouchers;
    private BigDecimal deposit;
    private BigDecimal withdrawal;
    private BigDecimal lostCard;
    private BigDecimal lostCash;
    private BigDecimal finalCash;
    private String comment;
    private LocalDateTime closureDate;
    private LocalDateTime openingDate;

    public CashierEntity(Cashier cashier) {
        BeanUtils.copyProperties(cashier, this);
    }

    public CashierEntity from(Cashier cashier) {
        BeanUtils.copyProperties(cashier, this, "id");
        return this;
    }

    public Cashier toCashier() {
        Cashier cashier = new Cashier();
        BeanUtils.copyProperties(this, cashier);
        return cashier;
    }
}
