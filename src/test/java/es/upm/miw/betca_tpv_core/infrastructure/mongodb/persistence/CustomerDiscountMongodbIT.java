package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
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

    @Test
    void testReadByUserMobile() {
        StepVerifier
                .create(this.customerDiscountPersistenceMongodb.readByUserMobile("699999999"))
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
                .create(this.customerDiscountPersistenceMongodb.updateCustomerDiscount("666666666",customerDiscount))
                .expectNextMatches(dbCustomerDiscount -> {
                    assertNotNull(dbCustomerDiscount.getRegistrationDate());
                    assertEquals(66, dbCustomerDiscount.getDiscount());
                    assertEquals("666666666", dbCustomerDiscount.getUser().getMobile());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testDeleteByUserMobile() {
        StepVerifier
                .create(this.customerDiscountPersistenceMongodb.deleteByUserMobile("611111111"))
                .verifyComplete();
        StepVerifier
                .create(this.customerDiscountPersistenceMongodb.readByUserMobile("611111111"))
                .expectError(NotFoundException.class)
                .verify();
    }
}
