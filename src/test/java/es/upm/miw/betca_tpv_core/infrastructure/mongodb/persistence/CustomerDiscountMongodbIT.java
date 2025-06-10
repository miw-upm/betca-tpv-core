package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

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
public class CustomerDiscountMongodbIT {

    @Autowired
    private CustomerDiscountPersistenceMongodb customerDiscountPersistenceMongodb;

    @Test
    void testCreateCustomerDiscount() {
        User user = new User("666999666", "lilyXiang", "Wuli", "321123@gmail.com", "y88888888x", "calle techo");
        CustomerDiscount customerDiscount = new CustomerDiscount(user, "Vip Customer", LocalDateTime.now(), 25, 100);
        StepVerifier
                .create(this.customerDiscountPersistenceMongodb.createCustomerDiscount(customerDiscount))
                .expectNextMatches(dbCustomerDiscount -> {
                    assertNotNull(dbCustomerDiscount.getRegistrationDate());
                    assertEquals(25, dbCustomerDiscount.getDiscount());
                    assertEquals("666999666", dbCustomerDiscount.getUser().getMobile());
                    return true;
                })
                .expectComplete()
                .verify();
    }
}
