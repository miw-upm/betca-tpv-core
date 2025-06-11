package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTestConfig
public class CustomerDiscountResourceIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;


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

    @Test
    void testReadByUserMobile() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(CustomerDiscountResource.CUSTOMER_DISCOUNT + CustomerDiscountResource.MOBILE, "699999999")
                .exchange().expectStatus().isOk()
                .expectBody(CustomerDiscount.class)
                .value(Assertions::assertNotNull)
                .value(customerDiscount -> {
                    assertEquals("699999999", customerDiscount.getUser().getMobile());
                    assertEquals(99, customerDiscount.getDiscount());
                });
    }

    @Test
    void testUpdateCustomerDiscount() {
        User user = new User("666666666", "lilyXiang", "Wuli", "321123@gmail.com", "y88888888x", "calle techo");
        CustomerDiscount customerDiscount = new CustomerDiscount(user, "Vip Customer", LocalDateTime.now(), 66, 100);

        this.restClientTestService.loginAdmin(webTestClient)
                .put().uri(CustomerDiscountResource.CUSTOMER_DISCOUNT + CustomerDiscountResource.MOBILE, "666666666")
                .body(Mono.just(customerDiscount), CustomerDiscount.class)
                .exchange().expectStatus().isOk()
                .expectBody(CustomerDiscount.class)
                .value(Assertions::assertNotNull)
                .value(dbCustomerDiscount -> {
                    assertEquals("666666666", dbCustomerDiscount.getUser().getMobile());
                    assertEquals(66, dbCustomerDiscount.getDiscount());
                });
    }

}
