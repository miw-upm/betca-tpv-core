package es.upm.miw.betca_tpv_core.infrastructure.api.resources;


import es.upm.miw.betca_tpv_core.domain.model.Budget;
import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.ShoppingState;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.BudgetResource.BUDGETS;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.BudgetResource.BUDGET_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RestTestConfig
public class BudgetResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000017").amount(1).retailPrice(new BigDecimal("2"))
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000024").amount(1).retailPrice(new BigDecimal("3"))
                .discount(BigDecimal.ZERO).state(ShoppingState.NOT_COMMITTED).build();

        Budget budget = Budget.builder().creationDate(LocalDateTime.now()).shoppingList(List.of(shopping1, shopping2)).build();

        Budget dbBudget = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(BUDGETS)
                .body(Mono.just(budget), Budget.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Budget.class)
                .value(Assertions::assertNotNull)
                .value(returnBudget -> {
                    assertNotNull(returnBudget.getId());
                    assertNotNull(returnBudget.getReference());
                    assertNotNull(returnBudget.getCreationDate());
                    assertEquals(0, new BigDecimal("5").compareTo(returnBudget.total()));
                }).returnResult().getResponseBody();
        assertNotNull(dbBudget);
    }

    @Test
    void testRead() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(BUDGETS + BUDGET_ID, "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Budget.class)
                .value(budget -> {
                    assertEquals("1", budget.getId());
                    assertEquals("1", budget.getReference());
                    assertEquals(LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10), budget.getCreationDate());
                });
    }

    @Test
    void testReadNotFound() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(BUDGETS + BUDGET_ID, "454545")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDelete() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000017").amount(1).retailPrice(new BigDecimal("2"))
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000024").amount(1).retailPrice(new BigDecimal("3"))
                .discount(BigDecimal.ZERO).state(ShoppingState.NOT_COMMITTED).build();

        Budget budget = Budget.builder().creationDate(LocalDateTime.now()).shoppingList(List.of(shopping1, shopping2)).build();

        Budget dbBudget = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(BUDGETS)
                .body(Mono.just(budget), Budget.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Budget.class)
                .value(Assertions::assertNotNull)
                .value(returnBudget -> {
                    assertNotNull(returnBudget.getId());
                    assertNotNull(returnBudget.getReference());
                    assertNotNull(returnBudget.getCreationDate());
                    assertEquals(0, new BigDecimal("5").compareTo(returnBudget.total()));
                }).returnResult().getResponseBody();
        assertNotNull(dbBudget);

        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(BUDGETS + BUDGET_ID, dbBudget.getId())
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(BUDGETS + BUDGET_ID, dbBudget.getId())
                .exchange()
                .expectStatus().isNotFound();
    }
}
