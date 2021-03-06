package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Budget;
import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.ShoppingState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class BudgetPersistenceMongodbIT {

    @Autowired
    private BudgetPersistenceMongodb budgetPersistenceMongodb;

    @Test
    void testCreate() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000017").amount(2)
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000024").amount(3)
                .discount(BigDecimal.TEN).state(ShoppingState.NOT_COMMITTED).build();
        Budget budget = Budget.builder().id("1").creationDate(LocalDateTime.now())
                .shoppingList(List.of(shopping1, shopping2)).build();
        StepVerifier
                .create(this.budgetPersistenceMongodb.create(budget))
                .expectNextMatches(dbBudget -> {
                    assertNotNull(dbBudget.getId());
                    assertNotNull(dbBudget.getCreationDate());
                    assertEquals(2, dbBudget.getShoppingList().size());
                    return true;
                })
                .expectComplete()
                .verify();
    }


    @Test
    void testFindByIdNotFound() {
        StepVerifier
                .create(this.budgetPersistenceMongodb.findById("qwer"))
                .expectError(NotFoundException.class)
                .verify();
    }



}
