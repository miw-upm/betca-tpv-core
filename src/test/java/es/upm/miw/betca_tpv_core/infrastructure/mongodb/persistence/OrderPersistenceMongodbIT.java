package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Order;
import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
public class OrderPersistenceMongodbIT {

    @Autowired
    private OrderPersistenceMongodb orderPersistenceMongodb;

    @Test
    void testCreateExistingReference() {
        StepVerifier
                .create(this.orderPersistenceMongodb.create(
                        Order.builder()
                                .reference("ref1")
                                .description("error")
                                .providerCompany("prov1")
                                .orderLinesList(null)
                                .openingDate(LocalDateTime.now())
                                .closingDate(null)
                                .build()))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void testReadByReference() {
        StepVerifier
                .create(this.orderPersistenceMongodb.readByReference("ref1"))
                .expectNextMatches(order -> {
                    assertEquals("ref1", order.getReference());
                    assertEquals("desc1", order.getDescription());
                    assertEquals("pro1", order.getProviderCompany());
                    assertNotNull(order.getOpeningDate());
                    assertNotNull(order.getOrderLinesList());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testReadByReferenceNotFound() {
        StepVerifier
                .create(this.orderPersistenceMongodb.readByReference("kk"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testUpdate() {
        LocalDateTime openingDate = LocalDateTime.of(2020, Month.MARCH, 13, 22, 52);
        OrderLine[] orderLines = {
                OrderLine.builder().articleBarcode("barcode1").requiredAmount(1).finalAmount(2).build(),
                OrderLine.builder().articleBarcode("barcode2").requiredAmount(0).finalAmount(0).build()
        };

        Order order = Order.builder()
                .reference("ref1")
                .description("desc1")
                .providerCompany("pro1")
                .openingDate(openingDate)
                .orderLinesList(List.of(orderLines))
                .build();

        StepVerifier
                .create(this.orderPersistenceMongodb.update(order.getReference(), order))
                .expectNextMatches(returnedOrder -> {
                    assertNotNull(returnedOrder.getClosingDate());
                    assertNotNull(returnedOrder.getOrderLinesList().getFirst().getFinalAmount());
                    assertNotNull(returnedOrder.getOrderLinesList().getLast().getFinalAmount());
                    return true;
                })
                .verifyComplete();
    }
}
