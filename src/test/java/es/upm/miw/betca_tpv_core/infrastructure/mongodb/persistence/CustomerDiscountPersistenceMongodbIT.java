package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
public class CustomerDiscountPersistenceMongodbIT {

    @Autowired
    private CustomerDiscountPersistenceMongodb customerDiscountPersistenceMongodb;

    @Test
    void testCreate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StepVerifier
                .create(this.customerDiscountPersistenceMongodb.create(CustomerDiscount.builder().note("test").registrationDate(LocalDateTime.now().format(formatter)).discount(10.0).minimumPurchase(100.0).user("6").build()))
                .expectNextMatches(customerDiscount -> {
                    assertEquals(10.0, customerDiscount.getDiscount());
                    assertEquals(100.0, customerDiscount.getMinimumPurchase());
                    assertEquals("6", customerDiscount.getUser());
                    assertEquals("test", customerDiscount.getNote());
                    assertNotNull(customerDiscount.getRegistrationDate());
                    return true;
                })
                .expectComplete()
                .verify();
    }
}
