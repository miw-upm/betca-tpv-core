package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.domain.model.validations.ListNotEmpty;
import es.upm.miw.betca_tpv_core.domain.model.validations.PositiveBigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ticket {
    private String id;
    private String reference;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;
    @ListNotEmpty
    private List< Shopping > shoppingList;
    @PositiveBigDecimal
    private BigDecimal cash;
    @PositiveBigDecimal
    private BigDecimal card;
    @PositiveBigDecimal
    private BigDecimal voucher;
    @NotNull
    @NotBlank
    private String note;
    private User user;

    public BigDecimal total() {
        return this.shoppingList.stream()
                .map(Shopping::totalShopping)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal pay() {
        return this.cash.add(this.card).add(this.voucher);
    }

    public BigDecimal debt() {
        return this.total().subtract(this.pay());
    }

    public boolean hasDebt() {
        return this.pay().compareTo(this.total()) < 0;
    }

    public int itemsNotCommitted() {
        return this.getShoppingList().stream()
                .filter(shopping -> ShoppingState.NOT_COMMITTED.equals(shopping.getState()))
                .mapToInt(Shopping::getAmount)
                .sum();
    }

}
