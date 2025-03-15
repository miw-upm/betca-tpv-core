package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
public class BudgetServiceIT {

    @Autowired
    private IBudgetService budgetService;

    @Autowired
    private CashierService cashierService;

    @Test
    void testCreate() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000017").amount(2)
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000024").amount(3)
                .discount(BigDecimal.TEN).state(ShoppingState.NOT_COMMITTED).build();

        Budget budget = Budget.builder().creationDate(LocalDateTime.now()).shoppingList(List.of(shopping1, shopping2)).build();

        StepVerifier
                .create(this.budgetService.create(budget))
                .expectNextMatches(dbBudget -> {
                    assertNotNull(dbBudget.getId());
                    assertNotNull(dbBudget.getCreationDate());
                    assertNotNull(dbBudget.getReference());
                    assertEquals(2, dbBudget.getShoppingList().size());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testRead() {
        StepVerifier
                .create(this.budgetService.read("1"))
                .expectNextMatches(dbBudget -> {
                    assertEquals("1", dbBudget.getId());
                    assertEquals("1", dbBudget.getReference());
                    assertEquals(LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10), dbBudget.getCreationDate());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testReadNotFound() {
        StepVerifier
                .create(this.budgetService.read("44545"))
                .expectComplete()
                .verify();
    }

    @Test
    void testDelete() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000017").amount(2)
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000024").amount(3)
                .discount(BigDecimal.TEN).state(ShoppingState.NOT_COMMITTED).build();

        Budget budget = Budget.builder().creationDate(LocalDateTime.now()).shoppingList(List.of(shopping1, shopping2)).build();

        StepVerifier
                .create(this.budgetService.create(budget)
                        .flatMap(savedBudget -> this.budgetService.delete(savedBudget.getId())))
                .verifyComplete();

        StepVerifier
                .create(this.budgetService.read("3545"))
                .verifyComplete();
    }

    @Test
    void testUpdate() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000017").amount(2)
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000024").amount(3)
                .discount(BigDecimal.TEN).state(ShoppingState.NOT_COMMITTED).build();

        Budget budget = Budget.builder().creationDate(LocalDateTime.now()).shoppingList(List.of(shopping1, shopping2)).build();

        StepVerifier
                .create(this.budgetService.create(budget)
                        .flatMap(createdBudget -> {
                            Budget updatedBudget = Budget.builder().id(createdBudget.getId()).shoppingList(List.of(shopping1)).build();
                            return this.budgetService.update(createdBudget.getId(), updatedBudget);
                        })
                )
                .expectNextMatches(dbBudget -> {
                    assertNotNull(dbBudget.getId());
                    assertNotNull(dbBudget.getCreationDate());
                    assertEquals(1, dbBudget.getShoppingList().size());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testSearch() {
        StepVerifier
                .create(this.budgetService.findByReferenceLike("55"))
                .expectNextMatches(budget -> budget.getReference().equals("2323558888"))
                .expectNextMatches(budget -> budget.getReference().equals("8323558811"))
                .expectNextMatches(budget -> budget.getReference().equals("2323553433"))
                .verifyComplete();
    }
}
