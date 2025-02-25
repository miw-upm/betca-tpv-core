package es.upm.miw.betca_tpv_core.domain.model;


import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}