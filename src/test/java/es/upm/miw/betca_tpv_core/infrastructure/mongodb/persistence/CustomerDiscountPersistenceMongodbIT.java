package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

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

    @Test
    void testUpdate() {
        StepVerifier
                .create(this.customerDiscountPersistenceMongodb.update("1", CustomerDiscount.builder().note("test update").discount(40.0).build()))
                .expectNextMatches(customerDiscount -> {
                    assertEquals("test update", customerDiscount.getNote());
                    assertEquals(40.0, customerDiscount.getDiscount());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testReadById() {
        StepVerifier
                .create(this.customerDiscountPersistenceMongodb.readById("2"))
                .expectNextMatches(customerDiscount -> {
                    assertEquals("discount2", customerDiscount.getNote());
                    assertEquals(10.0, customerDiscount.getDiscount());
                    assertEquals(40.0, customerDiscount.getMinimumPurchase());
                    assertEquals("77", customerDiscount.getUser());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testCreateAndDelete() {
        AtomicReference<String> idCustomer = new AtomicReference<>("");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StepVerifier
                .create(this.customerDiscountPersistenceMongodb.create(CustomerDiscount.builder().note("test").registrationDate(LocalDateTime.now().format(formatter)).discount(10.0).minimumPurchase(100.0).user("6").build()))
                .expectNextMatches(customerDiscount -> {
                    assertEquals(10.0, customerDiscount.getDiscount());
                    assertEquals(100.0, customerDiscount.getMinimumPurchase());
                    assertEquals("6", customerDiscount.getUser());
                    assertEquals("test", customerDiscount.getNote());
                    assertNotNull(customerDiscount.getRegistrationDate());
                    idCustomer.set(customerDiscount.getId());
                    return true;
                })
                .expectComplete()
                .verify();
        StepVerifier
                .create(this.customerDiscountPersistenceMongodb.delete(idCustomer.get()))
                .expectComplete()
                .verify();
    }
}
