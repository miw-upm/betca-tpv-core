package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.CustomerPoints;
import es.upm.miw.betca_tpv_core.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class CustomerPointsPersistenceMongodbIT {

    @Autowired
    private CustomerPointsPersistenceMongodb customerPointsPersistenceMongodb;

    @Test
    void testCreateCustomerPoints() {
        User user = User.builder().mobile("666666004").firstName("John").build();
        CustomerPoints customerPoints = new CustomerPoints();
        customerPoints.setValue(150);
        customerPoints.setLastDate(LocalDateTime.now());
        customerPoints.setUser(user);

        StepVerifier
                .create(this.customerPointsPersistenceMongodb.createCustomerPoints(customerPoints))
                .expectNextMatches(dbCustomerPoints -> {
                    assertNotNull(dbCustomerPoints.getLastDate());
                    assertEquals(150, dbCustomerPoints.getValue());
                    assertEquals("666666004", dbCustomerPoints.getUser().getMobile());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testReadCustomerPointsByMobile() {
        User user = User.builder().mobile("666666004").firstName("John").build();

        StepVerifier
                .create(this.customerPointsPersistenceMongodb.readCustomerPointsByMobile("666666004", user))
                .expectNextMatches(dbCustomerPoints -> {
                    assertNotNull(dbCustomerPoints.getLastDate());
                    assertEquals(50, dbCustomerPoints.getValue());
                    assertEquals("666666004", dbCustomerPoints.getUser().getMobile());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testUpdateCustomerPoints() {
        User user = User.builder().mobile("666666004").firstName("John").build();
        CustomerPoints customerPoints = new CustomerPoints();
        customerPoints.setValue(200);
        customerPoints.setLastDate(LocalDateTime.now());
        customerPoints.setUser(user);

        StepVerifier
                .create(this.customerPointsPersistenceMongodb.updateCustomerPoints("666666004", customerPoints))
                .expectNextMatches(dbCustomerPoints -> {
                    assertNotNull(dbCustomerPoints.getLastDate());
                    assertEquals(200, dbCustomerPoints.getValue());
                    assertEquals("666666004", dbCustomerPoints.getUser().getMobile());
                    return true;
                })
                .expectComplete()
                .verify();
    }
}