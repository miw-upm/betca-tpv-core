package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Budget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class BudgetEntity {
    @Id
    private String id;
    private String reference;
    private LocalDateTime creationDate;
    private List<ShoppingEntity> shoppingEntityList;

    public BudgetEntity(Budget budget) {
        BeanUtils.copyProperties(budget, this);
        this.shoppingEntityList = new ArrayList<>();
    }

    public void add(ShoppingEntity shoppingEntity) {
        this.shoppingEntityList.add(shoppingEntity);
    }

    public Budget toBudget() {
        Budget budget = new Budget();
        BeanUtils.copyProperties(this, budget);
        budget.setShoppingList(this.getShoppingEntityList().stream()
                .map(ShoppingEntity::toShopping)
                .collect(Collectors.toList()));
        return budget;
    }


}
