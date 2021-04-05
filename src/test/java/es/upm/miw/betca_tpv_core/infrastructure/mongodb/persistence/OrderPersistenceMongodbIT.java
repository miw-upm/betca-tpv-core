package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Order;
import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class OrderPersistenceMongodbIT {

    @Autowired
    private OrderPersistenceMongodb orderPersistenceMongodb;

    @Test
    void testOrderReadAll() {
        StepVerifier
                .create(orderPersistenceMongodb.findByAll())
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByReference() {
        StepVerifier
                .create(this.orderPersistenceMongodb.findByReference("ref-01"))
                .expectNextMatches(order -> {
                    assertEquals("ref-01", order.getReference());
                    assertEquals("order 1", order.getDescription());
                    assertEquals("pro1", order.getProviderCompany());
                    assertEquals(LocalDateTime.of(2021, 3, 1, 15, 30, 0), order.getOpeningDate());
                    assertEquals(3, order.getOrderLines().size());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindByDescriptionAndOpeningDateBetween() {

        LocalDateTime startDate = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2021, 3, 31, 23, 59, 59);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        startDate.format(formatter);
        endDate.format(formatter);
        StepVerifier
                .create(this.orderPersistenceMongodb.findByDescriptionAndOpeningDateBetween("order 1", startDate, endDate))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testCreate() {

        StepVerifier
                .create(this.orderPersistenceMongodb.create(Order.builder().reference("ref-06")
                        .providerCompany("pro3")
                        .description("order 5")
                        .openingDate(LocalDateTime.of(2021, 2, 20, 9, 30, 0))
                        .orderLines(List.of(OrderLine.builder().articleBarcode("8400000000024")
                                .requireAmount(30)
                                .finalAmount(25)
                                .build()))
                        .closingDate(LocalDateTime.of(2021, 2, 25, 9, 30, 0))
                        .build()))
                .expectNextMatches(order -> {
                    assertEquals("ref-06", order.getReference());
                    assertEquals("order 5", order.getDescription());
                    assertEquals("pro3", order.getProviderCompany());
                    assertEquals(LocalDateTime.of(2021, 2, 20, 9, 30, 0), order.getOpeningDate());
                    assertEquals(1, order.getOrderLines().size());
                    return true;
                })
                .expectComplete()
                .verify();
    }


    @Test
    void testCreateExistReference() {
        StepVerifier
                .create(this.orderPersistenceMongodb.create(Order.builder().reference("ref-01")
                        .providerCompany("pro1")
                        .description("order 1")
                        .openingDate(LocalDateTime.of(2021, 2, 20, 9, 30, 0))
                        .build()))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void testCreateNotExistBarcode() {
        StepVerifier
                .create(this.orderPersistenceMongodb.create(Order.builder().reference("ref-08")
                        .providerCompany("pro1")
                        .description("order 1")
                        .openingDate(LocalDateTime.of(2021, 2, 20, 9, 30, 0))
                        .orderLines(List.of(OrderLine.builder().articleBarcode("8800000000017")
                                .requireAmount(30)
                                .finalAmount(25)
                                .build()))
                        .closingDate(LocalDateTime.of(2021, 2, 25, 9, 30, 0))
                        .build()))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testUpdate() {
        StepVerifier
                .create(this.orderPersistenceMongodb.update("ref-02",
                        Order.builder().reference("ref-02")
                                .providerCompany("pro3")
                                .description("order update")
                                .closingDate(LocalDateTime.of(2021, 2, 20, 9, 30, 0))
                                .orderLines(List.of(OrderLine.builder().articleBarcode("8400000000017")
                                        .finalAmount(8)
                                        .build()))
                                .build()))
                .expectNextMatches(order -> {
                    assertEquals("order update", order.getDescription());
                    assertEquals(LocalDateTime.of(2021, 2, 20, 9, 30, 0), order.getClosingDate());
                    assertEquals(order.getOrderLines().size(), 1);
                    assertNotNull(order.getOrderLines().get(0));
                    assertEquals("8400000000017", order.getOrderLines().get(0).getArticleBarcode());
                    assertEquals(8, order.getOrderLines().get(0).getFinalAmount());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testUpdateNotExistReference() {
        StepVerifier
                .create(this.orderPersistenceMongodb.update("ref-15",
                        Order.builder().reference("ref-15")
                                .providerCompany("pro1")
                                .description("order 15")
                                .openingDate(LocalDateTime.of(2021, 2, 20, 9, 30, 0))
                                .build()))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testUpdateNotExisBarcode() {
        StepVerifier
                .create(this.orderPersistenceMongodb.update("ref-01",
                        Order.builder().reference("ref-01")
                                .providerCompany("pro1")
                                .description("order 1")
                                .openingDate(LocalDateTime.of(2021, 2, 20, 9, 30, 0))
                                .orderLines(List.of(OrderLine.builder().articleBarcode("8800000000017")
                                        .requireAmount(30)
                                        .finalAmount(25)
                                        .build()))
                                .closingDate(LocalDateTime.of(2021, 2, 25, 9, 30, 0))
                                .build())
                )
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testDelete() {
        Mono<Void> orderDel = this.orderPersistenceMongodb.create(
                Order.builder().reference("ref-07")
                        .providerCompany("pro3")
                        .description("order 5")
                        .openingDate(LocalDateTime.of(2021, 2, 20, 9, 30, 0))
                        .orderLines(List.of(OrderLine.builder().articleBarcode("8400000000024")
                                .requireAmount(30)
                                .finalAmount(25)
                                .build()))
                        .closingDate(LocalDateTime.of(2021, 2, 25, 9, 30, 0))
                        .build())
                .flatMap(order -> this.orderPersistenceMongodb.delete(order.getReference()));
        StepVerifier
                .create(orderDel)
                .expectComplete()
                .verify();
    }

    @Test
    void testDeleteNotFound() {
        StepVerifier
                .create(this.orderPersistenceMongodb.delete("order-notfound"))
                .expectError(NotFoundException.class)
                .verify();
    }
}
