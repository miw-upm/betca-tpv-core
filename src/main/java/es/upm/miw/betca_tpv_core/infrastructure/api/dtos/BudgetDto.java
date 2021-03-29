package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import es.upm.miw.betca_tpv_core.domain.model.Budget;
import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDto {
    private String id;
    private String reference;
    private String creationDate;
    private List<Shopping> shoppingList;

    public BudgetDto(Budget budget) {
        this.id = budget.getId();
        this.creationDate =  budget.getCreationDate().toString();
        this.shoppingList = budget.getShoppingList();
    }


}
