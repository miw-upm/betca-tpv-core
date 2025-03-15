package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Order;
import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class OrderServiceIT {

    @Autowired
    private OrderService orderService;

    @Test
    void testCreateOrder() {
        LocalDateTime openingDate = LocalDateTime.of(2019, Month.DECEMBER, 23, 4, 52);
        OrderLine[] orderLines = {
                OrderLine.builder().articleBarcode("barcode44").requiredAmount(1).finalAmount(null).build(),
                OrderLine.builder().articleBarcode("barcode55").requiredAmount(2).finalAmount(null).build()
        };

        Order order = Order.builder()
                .reference("ref4")
                .description("order1desc")
                .providerCompany("protest1")
                .openingDate(openingDate)
                .orderLinesList(List.of(orderLines))
                .build();

        StepVerifier.create(orderService.create(order)).expectNextMatches(returnedOrder -> {
            assertNotNull(returnedOrder);
            assertEquals(returnedOrder.getReference(), order.getReference());
            assertEquals(returnedOrder.getDescription(), order.getDescription());
            assertEquals(returnedOrder.getProviderCompany(), order.getProviderCompany());
            assertEquals(returnedOrder.getOpeningDate(), order.getOpeningDate());
            assertNull(returnedOrder.getClosingDate());
            assertNotNull(returnedOrder.getOrderLinesList());
            assertEquals(2, returnedOrder.getOrderLinesList().size());
            assertEquals("barcode44", returnedOrder.getOrderLinesList().getFirst().getArticleBarcode());
            return true;
        }).verifyComplete();
    }

    @Test
    void testReadByReference() {
        StepVerifier
                .create(this.orderService.read("ref1"))
                .expectNextMatches(order -> {
                    assertEquals("ref1", order.getReference());
                    assertEquals("desc1", order.getDescription());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testReadByReferenceNotFound() {
        StepVerifier
                .create(this.orderService.read("kk"))
                .expectError(NotFoundException.class)
                .verify();
    }
}
