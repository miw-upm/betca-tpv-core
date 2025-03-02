package es.upm.miw.betca_tpv_core.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomerPointsTest {

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder().mobile("666666666").build();
        CustomerPoints customerPoints = CustomerPoints.builder().value(10).lastDate(now).user(user).build();
        assertEquals(10, customerPoints.getValue());
        assertEquals(now, customerPoints.getLastDate());
        assertEquals(user, customerPoints.getUser());
    }

    @Test
    void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder().mobile("666666666").build();
        CustomerPoints customerPoints = new CustomerPoints();
        customerPoints.setValue(20);
        customerPoints.setLastDate(now);
        customerPoints.setUser(user);

        assertEquals(20, customerPoints.getValue());
        assertEquals(now, customerPoints.getLastDate());
        assertEquals(user, customerPoints.getUser());
    }

    @Test
    void testNoArgsConstructor() {
        CustomerPoints customerPoints = new CustomerPoints();
        assertNotNull(customerPoints);
    }
}