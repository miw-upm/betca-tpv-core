package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CustomerDiscountResource.CUSTOMERS_DISCOUNTS;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTestConfig
public class CustomerDiscountResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate(){
        /*CustomerDiscount customerDiscount = CustomerDiscount.builder().note("test").registrationDate(LocalDateTime.now().toString()).discount(30.0).minimumPurchase(50.0).user("11").build();
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
}
