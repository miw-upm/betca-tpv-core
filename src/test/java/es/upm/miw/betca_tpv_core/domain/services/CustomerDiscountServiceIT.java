package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import es.upm.miw.betca_tpv_core.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
public class CustomerDiscountServiceIT {
    @Autowired
    private CustomerDiscountService customerDiscountService;
    @Test
    void testCreateCustomerDiscount() {
        User user = new User("666999666", "lilyXiang", "Wuli", "321123@gmail.com", "y88888888x", "calle techo");
        CustomerDiscount customerDiscount = new CustomerDiscount(user, "Vip Customer", LocalDateTime.now(), 25, 100);
        StepVerifier
                .create(this.customerDiscountService.createCustomerDiscount(customerDiscount))
                .expectNextMatches(dbCustomerDiscount -> {
                    assertNotNull(dbCustomerDiscount.getRegistrationDate());
                    assertEquals(25, dbCustomerDiscount.getDiscount());
                    assertEquals("666999666", dbCustomerDiscount.getUser().getMobile());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testReadByUserMobile() {
        StepVerifier
                .create(this.customerDiscountService.readByUserMobile("699999999"))
                .expectNextMatches(customerDiscount -> {
                    assertNotNull(customerDiscount.getRegistrationDate());
                    assertEquals(99, customerDiscount.getDiscount());
                    assertEquals("699999999", customerDiscount.getUser().getMobile());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testUpdateCustomerDiscount() {
        User user = new User("666666666", "lilyXiang", "Wuli", "321123@gmail.com", "y88888888x", "calle techo");
        CustomerDiscount customerDiscount = new CustomerDiscount(user, "Vip Customer", LocalDateTime.now(), 66, 100);

        StepVerifier
                .create(this.customerDiscountService.updateCustomerDiscount("666666666",customerDiscount))
                .expectNextMatches(dbCustomerDiscount -> {
                    assertNotNull(dbCustomerDiscount.getRegistrationDate());
                    assertEquals(66, dbCustomerDiscount.getDiscount());
                    assertEquals("666666666", dbCustomerDiscount.getUser().getMobile());
                    return true;
                })
                .expectComplete()
                .verify();
    }
}
