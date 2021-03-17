package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockManagerDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.time.Month;

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
                .value(stocks -> stocks.stream()
                        .allMatch(stockManagerDto -> stockManagerDto.getStock().compareTo(10) < 0));
    }
    @Test
    void testSearchSoldProducts(){
        LocalDateTime dateIni = LocalDateTime.of(2019, Month.JANUARY, 01, 00, 00, 00);
        LocalDateTime dateEnd = LocalDateTime.of(2019, Month.JANUARY, 15, 00, 00, 00);
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(StockManagerResource.STOCK_MANAGER + StockManagerResource.STOCK_SOLD)
                        .queryParam("initial", dateIni)
                        .queryParam("end", dateEnd)
                        .build())
                .exchange()
                .expectStatus().isOk();
           //     .expectBodyList(StockManagerDto.class);
          //      .value(Assertions::assertNotNull);
        //                 .value(stocks -> stocks.stream()
        //                        .allMatch(stockManagerDto -> stockManagerDto.getDateSell().isBefore(dateEnd) && stockManagerDto.getDateSell().isAfter(dateIni) ));
    }
    @Test
    void testSearchFutureStock(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(StockManagerResource.STOCK_MANAGER + StockManagerResource.STOCK_FUTURE)
                        .queryParam("barcode", "8400000000024")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockManagerDto.class)
                .value(Assertions::assertNotNull)
                .value(returnStockManager -> {
                    assertEquals("8400000000024", returnStockManager.getBarcode());
                    // assertEquals(2, returnStockManager.getStock());
                    assertEquals("Zarzuela - Falda T4", returnStockManager.getDescription());
                });

    }
    @Test
    void testSearchFutureStockNotFound(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(StockManagerResource.STOCK_MANAGER + StockManagerResource.STOCK_FUTURE)
                        .queryParam("barcode", "8400000000029")
                        .build())
                .exchange()
                .expectStatus().isNotFound();
    }
}
