package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.OfferResource.REFERENCE_ID;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.OrderResource.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        Order order = Order.builder()
                .description("desc1")
                .providerCompany("providerapi")
                .openingDate(LocalDateTime.now())
                .orderLinesList(List.of(orderLines))
                .closingDate(null)
                .build();

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

    @Test
    void testReadByReferenceAndUpdate() {
        Order order = this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ORDERS + REFERENCE_ID, "ref1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .value(Assertions::assertNotNull)
                .value(returnOrder -> {
                    assertEquals("ref1", returnOrder.getReference());
                    assertEquals("desc1", returnOrder.getDescription());
                })
                .returnResult()
                .getResponseBody();
        assertNotNull(order);

        OrderLine[] orderLines = {
                OrderLine.builder().articleBarcode("barcode1").requiredAmount(1).finalAmount(2).build(),
                OrderLine.builder().articleBarcode("barcode2").requiredAmount(0).finalAmount(0).build()
        };

        order.setOrderLinesList(List.of(orderLines));

        order = this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(ORDERS + REFERENCE_ID, "ref1")
                .body(Mono.just(order), Order.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .value(Assertions::assertNotNull)
                .value(returnOrder ->{
                    assertNotNull(returnOrder.getClosingDate());
                    assertNotNull(returnOrder.getOrderLinesList().getFirst().getFinalAmount());
                    assertNotNull(returnOrder.getOrderLinesList().getLast().getFinalAmount());
                    })
                .returnResult()
                .getResponseBody();
        assertNotNull(order);
        order.setReference("ref-order123");
        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(ORDERS + REFERENCE_ID, "ref1")
                .body(Mono.just(order), Order.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testDelete() {
        OrderLine[] orderLines = {
                OrderLine.builder().articleBarcode("barcode44api").requiredAmount(1).finalAmount(null).build(),
                OrderLine.builder().articleBarcode("barcode55api").requiredAmount(2).finalAmount(null).build()
        };

        Order order = Order.builder()
                .description("desc1todelete")
                .providerCompany("providerapi")
                .openingDate(LocalDateTime.now())
                .orderLinesList(List.of(orderLines))
                .closingDate(null)
                .build();

        AtomicReference<String> orderReturnReference = new AtomicReference<>();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ORDERS)
                .body(Mono.just(order), Order.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .value(Assertions::assertNotNull)
                .value(returnedOrder -> {
                    orderReturnReference.set(returnedOrder.getReference());
                    assertNotNull(returnedOrder.getReference());
                    assertEquals("desc1todelete", returnedOrder.getDescription());
                    assertEquals("providerapi", returnedOrder.getProviderCompany());
                    assertNotNull(returnedOrder.getOpeningDate());
                    assertNotNull(returnedOrder.getOrderLinesList());
                    assertEquals(2, returnedOrder.getOrderLinesList().size());
                });

        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(ORDERS + REFERENCE_ID, orderReturnReference.get())
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ORDERS + REFERENCE_ID, orderReturnReference.get())
                .exchange()
                .expectStatus().isNotFound();
    }
}
