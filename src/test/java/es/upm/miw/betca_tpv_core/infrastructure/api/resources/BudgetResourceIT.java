package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.BudgetDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.BudgetResource.*;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CashierResource.CASHIERS;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CashierResource.LAST;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.TicketResource.RECEIPT;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@RestTestConfig
class BudgetResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;
    @MockBean
    private UserMicroservice userMicroservice;

    @BeforeEach
    void openCashier() {
        System.setProperty("miw.slack.uri", "");
        this.restClientTestService.loginAdmin(webTestClient)
                .post().uri(CASHIERS)
                .exchange()
                .expectStatus().isOk();
        BDDMockito.given(this.userMicroservice.readByMobile(anyString()))
                .willAnswer(arguments ->
                        Mono.just(User.builder().mobile(arguments.getArgument(0)).firstName("mock").build()));
    }

    @Test
    void testCreate() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000017").retailPrice(new BigDecimal("20")).amount(1)
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000024").retailPrice(new BigDecimal("27.8")).amount(3)
                .discount(BigDecimal.TEN).state(ShoppingState.NOT_COMMITTED).build();
        Budget budget = Budget.builder().id("1").creationDate(LocalDateTime.now())
                .shoppingList(List.of(shopping1, shopping2)).build();
        Budget dbBudget = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(BUDGETS)
                .body(Mono.just(budget), Budget.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Budget.class)
                .value(Assertions::assertNotNull)
                .value(returnBudget -> {
                    System.out.println(">>>>> budget: " + returnBudget + " total: " + returnBudget.getBudgetTotal());
                    assertNotNull(returnBudget.getId());
                    assertNotNull(returnBudget.getCreationDate());
                    assertEquals(0, new BigDecimal("95.06").compareTo(returnBudget.getBudgetTotal()));
                }).returnResult().getResponseBody();
        assertNotNull(dbBudget);
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(BUDGETS + ID_ID +RECEIPT, dbBudget.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testCreateNotFoundArticleException() {
        Shopping shopping1 = Shopping.builder().barcode("kkkk").retailPrice(new BigDecimal("20")).amount(1)
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Budget budget = Budget.builder().id("1").creationDate(LocalDateTime.now())
                .shoppingList(List.of(shopping1)).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(BUDGETS)
                .body(Mono.just(budget), Budget.class)
                .exchange()
                .expectStatus().isNotFound();
    }
    @Test
    void testRead() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(BUDGETS + BudgetResource.ID_ID + RECEIPT, "b600b5c9cac1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
    }
    @Test
    void testFindById(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(BUDGETS + ID_ID, "b600b5c9cac1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BudgetDto.class)
                .value(Assertions::assertNotNull)
                .value(budget -> assertEquals("b600b5c9cac1", budget.getId()));
    }


    @Test
    void testFindByIdNotFoundException(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(BUDGETS + ID_ID, "kk")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testFindByIdUnauthorizedException() {
        this.webTestClient
                .get()
                .uri(BUDGETS + ID_ID, "kk")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @AfterEach
    void closeCashier() {
        this.restClientTestService.loginAdmin(webTestClient)
                .patch().uri(CASHIERS + LAST)
                .body(Mono.just(new CashierClose(ZERO, ZERO, "test")), CashierClose.class)
                .exchange()
                .expectStatus().isOk();
    }

}



