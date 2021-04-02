package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockManagerDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
class StockManagerResourceIT {
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
                .value(stocks -> assertTrue(stocks.stream()
                        .allMatch(stockManagerDto -> stockManagerDto.getStock().compareTo(10) < 0)));

    }

    @Test
    void testSearchSoldProducts() {
        LocalDateTime dateIni = LocalDateTime.of(2019, Month.JANUARY, 1, 0, 0, 0);
        LocalDateTime dateEnd = LocalDateTime.of(2019, Month.JANUARY, 15, 0, 0, 0);
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(StockManagerResource.STOCK_MANAGER + StockManagerResource.STOCK_SOLD)
                        .queryParam("initial", dateIni)
                        .queryParam("end", dateEnd)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StockManagerDto.class)
                .value(Assertions::assertNotNull)
                .value(stocks -> assertTrue(stocks.stream()
                        .allMatch(stockManagerDto -> stockManagerDto.getDateSell().isAfter(dateIni.toLocalDate()))))
                .value(stocks -> assertTrue(stocks.stream()
                        .allMatch(stockManagerDto -> stockManagerDto.getDateSell().isBefore(dateEnd.toLocalDate()))));
    }

    @Test
    void testSearchFutureStock() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(StockManagerResource.STOCK_MANAGER + StockManagerResource.STOCK_FUTURE)
                        .queryParam("barcode", "8400000000017")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockManagerDto.class)
                .value(Assertions::assertNotNull)
                .value(returnStockManager -> {
                    assertEquals("8400000000017", returnStockManager.getBarcode());
                    assertEquals("Zarzuela - Falda T2", returnStockManager.getDescription());
                });

    }

    @Test
    void testSearchFutureStockNotFound() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(StockManagerResource.STOCK_MANAGER + StockManagerResource.STOCK_FUTURE)
                        .queryParam("barcode", "8400000000029")
                        .build())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testSearchEmptyStock() {
        LocalDate end = LocalDate.now().plusDays(1);
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(StockManagerResource.STOCK_MANAGER + StockManagerResource.STOCK_EMPTY)
                        .queryParam("barcode", "8400000000555")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockManagerDto.class)
                .value(Assertions::assertNotNull)
                .value(returnStockManager -> {
                    assertEquals("8400000000555", returnStockManager.getBarcode());
                    // assertEquals(7, returnStockManager.getStock());
                    assertEquals("without provider", returnStockManager.getDescription());
                    assertEquals(end, returnStockManager.getDateStockEmpty());

                });
    }

    @Test
    void testSearchEmptyStockWithoutDateEmptyStock() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(StockManagerResource.STOCK_MANAGER + StockManagerResource.STOCK_EMPTY)
                        .queryParam("barcode", "8400000000048")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockManagerDto.class)
                .value(Assertions::assertNotNull)
                .value(returnStockManager -> {
                    assertEquals("8400000000048", returnStockManager.getBarcode());
                    assertEquals("descrip-a4", returnStockManager.getDescription());
                    assertNull(returnStockManager.getDateStockEmpty());

                });
    }
}
