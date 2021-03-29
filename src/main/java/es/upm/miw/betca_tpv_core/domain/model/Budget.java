package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.domain.model.validations.ListNotEmpty;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Budget {
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;
    @ListNotEmpty
    private List< Shopping > shoppingList;

    public BigDecimal getBudgetTotal() {
        return this.shoppingList.stream()
                .map(Shopping::totalShopping)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
