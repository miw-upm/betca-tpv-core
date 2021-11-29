package es.upm.miw.betca_tpv_core.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CashierState {
    private BigDecimal totalSales;
    private BigDecimal totalCash;
    private BigDecimal totalCard;
    private BigDecimal totalVoucher;
    private Boolean opened;

    public CashierState(Cashier cashier) {
        this.totalSales = cashier.getCashSales().add(cashier.getCardSales())
                .add(cashier.getUsedVouchers());
        this.totalCash = cashier.getInitialCash().add(cashier.getCashSales())
                .add(cashier.getDeposit()).subtract(cashier.getWithdrawal());
        this.totalCard = cashier.getCardSales();
        this.totalVoucher = cashier.getUsedVouchers();
        this.opened = !cashier.isClosed();
    }
}
