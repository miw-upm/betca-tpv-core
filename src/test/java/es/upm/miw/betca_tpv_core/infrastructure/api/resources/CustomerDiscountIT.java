package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTestConfig
public class CustomerDiscountIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;
    @LocalServerPort
    int port;

    @Test
    void testCreate() {
        User user = new User("666999666", "lilyXiang", "Wuli", "321123@gmail.com", "y88888888x", "calle techo");
        CustomerDiscount customerDiscount = new CustomerDiscount(user, "Vip Customer", LocalDateTime.now(), 25, 100);

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(CustomerDiscountResource.CUSTOMER_DISCOUNT)
                .body(Mono.just(customerDiscount), CustomerDiscount.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDiscount.class)
                .value(Assertions::assertNotNull)
                .value(dbCustomerDiscount -> {
                    assertEquals("666999666", dbCustomerDiscount.getUser().getMobile());
                    assertEquals(25, dbCustomerDiscount.getDiscount());
                });
    }

}
