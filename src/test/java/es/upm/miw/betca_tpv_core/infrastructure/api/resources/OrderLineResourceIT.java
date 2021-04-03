package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.OrderLineResource.ID;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.OrderLineResource.ORDER_LINES;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
}