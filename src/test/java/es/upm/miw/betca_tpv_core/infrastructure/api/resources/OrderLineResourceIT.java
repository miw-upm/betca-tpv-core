package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.OrderLineResource.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RestTestConfig
class OrderLineResourceIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testFindById() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ORDER_LINES + ID, "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderLine.class)
                .value(Assertions::assertNotNull)
                .value(orderLine -> {
                            assertEquals("8400000000017", orderLine.getArticleBarcode());
                            assertEquals(10, orderLine.getRequireAmount());
                        }
                );
    }

    @Test
    void testFindByIdNotFound() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ORDER_LINES + ID, "100")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreate() {

        OrderLine orderLine = OrderLine.builder().articleBarcode("8400000000017").requireAmount(10).finalAmount(15).build();
        OrderLine orderLineDB = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ORDER_LINES)
                .body(Mono.just(orderLine), OrderLine.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderLine.class)
                .value(Assertions::assertNotNull)
                .value(outOrderLine -> {
                    assertEquals("8400000000017", outOrderLine.getArticleBarcode());
                    assertEquals(10, outOrderLine.getRequireAmount());
                    assertEquals(15, outOrderLine.getFinalAmount());
                }).returnResult().getResponseBody();
        assertNotNull(orderLineDB);
    }

    @Test
    void testCreateNotExistBarcode() {
        OrderLine orderLine = OrderLine.builder().articleBarcode("9900000000017").requireAmount(10).finalAmount(15).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ORDER_LINES)
                .body(Mono.just(orderLine), OrderLine.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdate() {
        OrderLine orderLineUpdate = OrderLine.builder().articleBarcode("8400000000017").requireAmount(55).finalAmount(50).build();

        OrderLine orderLineDB = this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(ORDER_LINES + BARCODE, "8400000000017")
                .body(Mono.just(orderLineUpdate), OrderLine.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderLine.class)
                .value(Assertions::assertNotNull)
                .value(outOrderLine -> {
                            assertEquals("8400000000017", outOrderLine.getArticleBarcode());
                            assertEquals(55, outOrderLine.getRequireAmount());
                            assertEquals(50, outOrderLine.getFinalAmount());
                        }
                ).returnResult().getResponseBody();
        assertNotNull(orderLineDB);
    }

    @Test
    void testUpdateNotExistBarcode() {

        OrderLine orderLineUpdate = OrderLine.builder().articleBarcode("9900000000017").requireAmount(55).finalAmount(50).build();


        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(ORDER_LINES + BARCODE, "9900000000017")
                .body(Mono.just(orderLineUpdate), OrderLine.class)
                .exchange()
                .expectStatus().isNotFound();
    }
}