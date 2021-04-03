package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
class OrderReactiveIT {
    @Autowired
    private OrderReactive orderReactive;

    @Test
    void testFindAll() {
        StepVerifier
                .create(this.orderReactive.findAll())
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByOrderReference() {
        StepVerifier
                .create(this.orderReactive.findByReference("ref-01"))
                .expectNextMatches(order -> {
                    assertEquals("ref-01", order.getReference());
                    assertEquals("order 1", order.getDescription());
                    assertEquals("pro1", order.getProviderEntity().getCompany());
                    assertEquals(LocalDateTime.of(2021, 3, 1, 15, 30, 0), order.getOpeningDate());
                    assertEquals(3, order.getOrderLineEntities().size());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByOrderDescriptionAndOpeningDateBetween() {

        LocalDateTime startDate = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2021, 3, 31, 23, 59, 59);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        startDate.format(formatter);
        endDate.format(formatter);
        StepVerifier
                .create(this.orderReactive.findByDescriptionAndOpeningDateBetween("order 1", startDate, endDate))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }
}
