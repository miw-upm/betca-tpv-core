package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CustomerDiscountResource.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RestTestConfig
public class CustomerDiscountResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        /*CustomerDiscount customerDiscount = CustomerDiscount.builder().note("test").discount(30.0).minimumPurchase(50.0).user("6").build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(CUSTOMERS_DISCOUNTS)
                .body(Mono.just(customerDiscount), CustomerDiscount.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDiscount.class)
                .value(Assertions::assertNotNull)
                .value(returnCustomerDiscount -> {
                    assertEquals("test", returnCustomerDiscount.getNote());
                    assertEquals(30.0, returnCustomerDiscount.getDiscount());
                    assertEquals(50.0, returnCustomerDiscount.getMinimumPurchase());
                });*/
    }

    @Test
    void testRead() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(CUSTOMERS_DISCOUNTS + CUSTOMER_DISCOUNT_ID, "4")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDiscount.class)
                .value(customerDiscount -> {
                    assertEquals("discount4", customerDiscount.getNote());
                    assertEquals(80.0, customerDiscount.getDiscount());
                    assertEquals(100.0, customerDiscount.getMinimumPurchase());
                    assertEquals("99", customerDiscount.getUser());
                });
    }

    @Test
    void testFindByNoteAndDiscountAndMinimumPurchaseAndUserNullSafe() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CUSTOMERS_DISCOUNTS + SEARCH)
                        .queryParam("user", "99")
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDiscount.class)
                .value(customerDiscounts ->
                        assertTrue(customerDiscounts.stream().anyMatch(customerDiscount -> customerDiscount.getNote().equals("discount4")))
                );
    }

    @Test
    void testCreateUpdateDelete() {
        /*CustomerDiscount customerDiscount = CustomerDiscount.builder().note("test update").discount(10.0).minimumPurchase(100.0).user("6").build();
        AtomicReference<String> idCustomer = new AtomicReference<>("");
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(CUSTOMERS_DISCOUNTS)
                .body(Mono.just(customerDiscount), CustomerDiscount.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDiscount.class)
                .value(customerDiscount1 -> {
                            idCustomer.set(customerDiscount1.getId());
                        }
                );
        CustomerDiscount customerDiscountUpdate = CustomerDiscount.builder().note("customer update").discount(20.0).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(CUSTOMERS_DISCOUNTS + CUSTOMER_DISCOUNT_ID, idCustomer.get())
                .body(Mono.just(customerDiscountUpdate), CustomerDiscount.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDiscount.class)
                .value(Assertions::assertNotNull)
                .value(customerDiscount1 -> {
                            assertEquals("customer update", customerDiscount1.getNote());
                        }
                );

        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(CUSTOMERS_DISCOUNTS + CUSTOMER_DISCOUNT_ID, idCustomer.get())
                .exchange()
                .expectStatus().isOk();*/
    }
}
