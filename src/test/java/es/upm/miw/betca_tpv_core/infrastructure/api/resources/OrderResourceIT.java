package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Order;
import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.OrderResource.*;
import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
class OrderResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        OrderLine[] orderLines = {
                OrderLine.builder().articleBarcode("barcode44api").requiredAmount(1).finalAmount(null).build(),
                OrderLine.builder().articleBarcode("barcode55api").requiredAmount(2).finalAmount(null).build()
        };

        Order order = Order.builder().description("desc1").providerCompany("providerapi").openingDate(LocalDateTime.now()).orderLinesList(List.of(orderLines)).closingDate(null).build();

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ORDERS)
                .body(Mono.just(order), Order.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .value(Assertions::assertNotNull)
                .value(returnedOrder -> {
                    assertNotNull(returnedOrder.getReference());
                    assertEquals("desc1", returnedOrder.getDescription());
                    assertEquals("providerapi", returnedOrder.getProviderCompany());
                    assertNotNull(returnedOrder.getOpeningDate());
                    assertNotNull(returnedOrder.getOrderLinesList());
                    assertEquals(2, returnedOrder.getOrderLinesList().size());
                });
    }


    @Test
    void testReadByReference() {
        Order order = this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ORDERS + REFERENCE_ID, "ref2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .value(Assertions::assertNotNull)
                .value(returnedOrder -> {
                    assertEquals("ref2", returnedOrder.getReference());
                    assertEquals("desc2", returnedOrder.getDescription());
                    assertEquals("pro2", returnedOrder.getProviderCompany());
                    assertEquals(2, returnedOrder.getOrderLinesList().size());
                    assertNotNull(returnedOrder.getOpeningDate());
                    assertNull(returnedOrder.getClosingDate());
                })
                .returnResult()
                .getResponseBody();
        assertNotNull(order);
    }

    @Test
    void testReadByReferenceNotFoundException() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ORDERS + REFERENCE_ID, "kk")
                .exchange()
                .expectStatus().isNotFound();
    }
}
