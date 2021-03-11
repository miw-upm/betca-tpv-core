package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockManagerDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RestTestConfig
public class StockManagerResourceIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;


    @Test
    void testSearchProductsByStock() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(StockManagerResource.STOCK_MANAGER + StockManagerResource.STOCK)
                        .queryParam("stock", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StockManagerDto.class)
                .value(Assertions::assertNotNull)
                .value(stocks -> assertTrue(stocks.stream().count() == 7))
                .value(stocks -> stocks.stream()
                        .allMatch(stockManagerDto -> stockManagerDto.getStock().compareTo(10) < 0));
    }

}
