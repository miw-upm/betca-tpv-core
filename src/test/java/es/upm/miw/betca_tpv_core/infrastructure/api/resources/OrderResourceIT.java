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
import java.time.format.DateTimeFormatter;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CreditResource.SEARCH;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.OfferResource.REFERENCE;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.OrderResource.ORDERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@RestTestConfig
class OrderResourceIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;


    @Test
    void testOrderFindByDescriptionAndOpeningDateBetween() {
        LocalDateTime startDate = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2021, 3, 31, 23, 59, 59);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        startDate.format(formatter);
        endDate.format(formatter);
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ORDERS + SEARCH)
                        .queryParam("description", "order 1")
                        .queryParam("fromDate", startDate)
                        .queryParam("toDate", endDate)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Order.class)
                .value(orders -> {
                    assertNotNull(orders);
                    assertEquals(1, orders.size());
                    assertEquals("ref-01", orders.get(0).getReference());
                    assertEquals("order 1", orders.get(0).getDescription());
                    assertEquals("pro1", orders.get(0).getProviderCompany());
                    assertEquals(LocalDateTime.of(2021, 3, 1, 15, 30, 0), orders.get(0).getOpeningDate());
                });
    }


    @Test
    void testOrderFindByReference() {

        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ORDERS + REFERENCE, "ref-01")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .value(order -> {
                    assertEquals("ref-01", order.getReference());
                    assertEquals("order 1", order.getDescription());
                    assertEquals("pro1", order.getProviderCompany());
                    assertEquals(LocalDateTime.of(2021, 3, 1, 15, 30, 0), order.getOpeningDate());
                    assertEquals(3, order.getOrderLines().size());
                });

    }

    @Test
    void testOrderFindByReferenceNotFound() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ORDERS + REFERENCE, "ref-not-found")
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void testOrderCreate() {
        Order order = Order.builder().reference("ref-11")
                .providerCompany("pro3")
                .description("order 5")
                .openingDate(LocalDateTime.of(2021, 2, 20, 9, 30, 0))
                .orderLines(List.of(OrderLine.builder().articleBarcode("8400000000024")
                                .requireAmount(30)
                                .finalAmount(25)
                                .build(),
                        OrderLine.builder().articleBarcode("8400000000031")
                                .requireAmount(50)
                                .finalAmount(30)
                                .build()
                ))
                .closingDate(LocalDateTime.of(2021, 2, 25, 9, 30, 0))
                .build();
        Order orderDB = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ORDERS)
                .body(Mono.just(order), Order.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .value(Assertions::assertNotNull)
                .value(outOrder -> {
                    assertEquals("ref-11", outOrder.getReference());
                    assertEquals("order 5", outOrder.getDescription());
                    assertEquals("pro3", outOrder.getProviderCompany());
                    assertEquals(LocalDateTime.of(2021, 2, 20, 9, 30, 0), outOrder.getOpeningDate());
                    assertEquals(2, outOrder.getOrderLines().size());
                }).returnResult().getResponseBody();
        assertNotNull(orderDB);
    }

    @Test
    void testOrderCreateNotExistBarcode() {
        Order order = Order.builder().reference("ref-11")
                .providerCompany("pro3")
                .description("order 5")
                .openingDate(LocalDateTime.of(2021, 2, 20, 9, 30, 0))
                .orderLines(List.of(OrderLine.builder().articleBarcode("8800000000024")
                        .requireAmount(30)
                        .finalAmount(25)
                        .build()
                ))
                .closingDate(LocalDateTime.of(2021, 2, 25, 9, 30, 0))
                .build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ORDERS)
                .body(Mono.just(order), Order.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testOrderUpdate() {
        Order orderUpdate = Order.builder().reference("ref-02")
                .providerCompany("pro3")
                .description("order update")
                .closingDate(LocalDateTime.of(2021, 2, 20, 9, 30, 0))
                .orderLines(List.of(OrderLine.builder().articleBarcode("8400000000017")
                        .finalAmount(8)
                        .build()))
                .build();

        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(ORDERS + REFERENCE, "ref-02")
                .body(Mono.just(orderUpdate), Order.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .value(Assertions::assertNotNull)
                .value(outOrder -> {
                            assertEquals("order update", outOrder.getDescription());
                            assertEquals(LocalDateTime.of(2021, 2, 20, 9, 30, 0), outOrder.getClosingDate());
                            assertEquals(outOrder.getOrderLines().size(), 1);
                            assertNotNull(outOrder.getOrderLines().get(0));
                            assertEquals("8400000000017", outOrder.getOrderLines().get(0).getArticleBarcode());
                            assertEquals(8, outOrder.getOrderLines().get(0).getFinalAmount());
                        }
                );
    }

    @Test
    void testOrderUpdateNotExistReference() {

        Order orderUpdate = Order.builder().reference("ref-15")
                .providerCompany("pro3")
                .description("order update")
                .closingDate(LocalDateTime.of(2021, 2, 20, 9, 30, 0))
                .orderLines(List.of(OrderLine.builder().articleBarcode("8400000000017")
                        .finalAmount(8)
                        .build()))
                .build();

        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(ORDERS + REFERENCE, "ref-15")
                .body(Mono.just(orderUpdate), Order.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testOrderUpdateNotExisBarcode() {

        Order orderUpdate = Order.builder().reference("ref-02")
                .providerCompany("pro3")
                .description("order update")
                .closingDate(LocalDateTime.of(2021, 2, 20, 9, 30, 0))
                .orderLines(List.of(OrderLine.builder().articleBarcode("9900000000017")
                        .finalAmount(8)
                        .build()))
                .build();

        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(ORDERS + REFERENCE, "ref-02")
                .body(Mono.just(orderUpdate), Order.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDelete() {

        Order orderDel =
                Order.builder().reference("ref-77")
                        .providerCompany("pro3")
                        .description("order 5")
                        .openingDate(LocalDateTime.of(2021, 2, 20, 9, 30, 0))
                        .orderLines(List.of(OrderLine.builder().articleBarcode("8400000000024")
                                .requireAmount(30)
                                .finalAmount(25)
                                .build()))
                        .closingDate(LocalDateTime.of(2021, 2, 25, 9, 30, 0))
                        .build();

        Order orderDB = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ORDERS)
                .body(Mono.just(orderDel), Order.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .value(Assertions::assertNotNull)
                .returnResult().getResponseBody();
        assertNotNull(orderDB);

        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(ORDERS + REFERENCE, orderDB.getReference())
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void testDeleteNotFound() {
        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(ORDERS + REFERENCE, "order-notfound")
                .exchange()
                .expectStatus().isNotFound();
    }


}
