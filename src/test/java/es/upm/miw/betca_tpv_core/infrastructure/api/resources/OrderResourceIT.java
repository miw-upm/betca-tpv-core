package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Order;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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


}
