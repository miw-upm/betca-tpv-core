package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class Cashier {
    @Id
    private String id;
    private BigDecimal initialCash;
    private BigDecimal cashSales;
    private BigDecimal cardSales;
    private BigDecimal usedVouchers;
    private BigDecimal deposit;
    private BigDecimal withdrawal;
    private String comment;
    private BigDecimal lostCard;
    private BigDecimal lostCash;
    private BigDecimal finalCash;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime openingDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closureDate;

    public boolean isClosed() {
        return Objects.nonNull(getClosureDate());
    }

    private void assertIsOpened() {
        if (this.isClosed()) throw new ConflictException("Can not operate with a closed cashier");
    }

    public void addSale(BigDecimal cash, BigDecimal card, BigDecimal voucher) {
        this.assertIsOpened();
        this.cashSales = this.cashSales.add(cash);
        this.cardSales = this.cardSales.add(card);
        this.usedVouchers = this.usedVouchers.add(voucher);
    }

    public void deposit(BigDecimal cash, String comment) {
        this.assertIsOpened();
        this.deposit = this.deposit.add(cash);
        this.comment += "Deposit (" + cash.setScale(2, RoundingMode.HALF_UP).toString() + "): "
                + comment + ".\n";
    }

    public void withdrawal(BigDecimal cash, String comment) {
        this.assertIsOpened();
        this.withdrawal = this.withdrawal.add(cash);
        this.comment += "Withdrawal (" + cash.setScale(2, RoundingMode.HALF_UP).toString() + "): "
                + comment + ".\n";
    }

    public void close(BigDecimal finalCash, BigDecimal finalCard, String comment) {
        this.assertIsOpened();
        this.finalCash = finalCash;
        this.lostCash = this.initialCash.add(this.cashSales).add(this.deposit)
                .subtract(this.withdrawal).subtract(finalCash);
        this.lostCard = this.cardSales.subtract(finalCard);
        if (!comment.isEmpty()) {
            this.comment += comment + ".\n";
        }
        this.closureDate = LocalDateTime.now();
    }

}
