package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
class StockAlarmResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        StockAlarm stockAlarm = StockAlarm.builder().name("AlarmResource").warning(7).critical(6).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(StockAlarmResource.STOCK_ALARMS)
                .body(Mono.just(stockAlarm), StockAlarm.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAlarm.class)
                .value(Assertions::assertNotNull)
                .value(returnStockAlarm -> {
                    assertEquals("AlarmResource", returnStockAlarm.getName());
                    assertEquals(7, returnStockAlarm.getWarning());
                    assertEquals(6, returnStockAlarm.getCritical());
                });
    }

    @Test
    void testFindByName() {
        StockAlarm stockAlarm = this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(StockAlarmResource.STOCK_ALARMS + StockAlarmResource.STOCK_ALARM_ID, "Alarma2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAlarm.class)
                .value(Assertions::assertNotNull)
                .value(returnStockAlarm -> {
                    assertEquals("Alarma2", returnStockAlarm.getName());
                    assertEquals("Descripcion 2", returnStockAlarm.getDescription());
                })
                .returnResult()
                .getResponseBody();
        assertNotNull(stockAlarm);
    }

    @Test
    void testUpdate() {
        StockAlarm stockAlarm = StockAlarm.builder().name("AlarmaResource").description("Actualizando")
                .warning(9).critical(5).build();

        StockAlarm stockAlarmDb = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(StockAlarmResource.STOCK_ALARMS)
                .body(Mono.just(stockAlarm), StockAlarm.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAlarm.class)
                .value(Assertions::assertNotNull)
                .returnResult()
                .getResponseBody();
        assertNotNull(stockAlarmDb);

        StockAlarm stockAlarmToUpdate = StockAlarm.builder().name("AlarmaResource").description("Resource")
                .warning(9).critical(5).build();

        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(StockAlarmResource.STOCK_ALARMS + StockAlarmResource.STOCK_ALARM_ID, stockAlarmDb.getName())
                .body(Mono.just(stockAlarmToUpdate), StockAlarm.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAlarm.class)
                .value(updatedStockAlarm -> {
                    assertNotNull(updatedStockAlarm.getName());
                    assertEquals("Resource", updatedStockAlarm.getDescription());
                });
    }

    @Test
    void testUpdateLines() {
        Article article1 = Article.builder().barcode("8400000000017").description("Articulo1").build();
        Article article2 = Article.builder().barcode("8400000000024").description("Articulo2").build();

        StockAlarmLine stockAlarmLine = StockAlarmLine.builder()
                .article(article1)
                .warning(2)
                .critical(1)
                .build();

        StockAlarm stockAlarm = StockAlarm.builder()
                .name("AlarmaUpdateLinesResource")
                .description("Sin actualizar")
                .warning(3)
                .critical(1)
                .stockAlarmLines(List.of(stockAlarmLine))
                .build();

        StockAlarm stockAlarmDb = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(StockAlarmResource.STOCK_ALARMS)
                .body(Mono.just(stockAlarm), StockAlarm.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAlarm.class)
                .value(Assertions::assertNotNull)
                .returnResult()
                .getResponseBody();
        assertNotNull(stockAlarmDb);

        StockAlarmLine updatedStockAlarmLine = StockAlarmLine.builder()
                .article(article2)
                .warning(5)
                .critical(3)
                .build();

        StockAlarm stockAlarmUpdated = StockAlarm.builder()
                .name("AlarmaUpdateLinesResource")
                .description("Actualizado")
                .warning(5)
                .critical(2)
                .stockAlarmLines(List.of(updatedStockAlarmLine))
                .build();

        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(StockAlarmResource.STOCK_ALARMS + StockAlarmResource.STOCK_ALARM_ID
                        + StockAlarmResource.STOCK_ALARM_LINES, stockAlarmUpdated.getName())
                .body(Mono.just(stockAlarmUpdated), StockAlarm.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAlarm.class)
                .value(updatedStockAlarm -> {
                    assertNotNull(updatedStockAlarm.getName());
                    assertEquals("Actualizado", updatedStockAlarm.getDescription());
                    assertNotNull(updatedStockAlarm.getStockAlarmLines());
                });
    }

    @Test
    void testSearchWarnings() {
        Article article1 = Article.builder().barcode("8400000000017").description("Articulo1").build();

        StockAlarmLine stockAlarmLine = StockAlarmLine.builder()
                .article(article1)
                .warning(900)
                .critical(1)
                .build();

        StockAlarm stockAlarm = StockAlarm.builder()
                .name("AlarmaSearchWarningResource")
                .description("Search")
                .warning(3)
                .critical(1)
                .stockAlarmLines(List.of(stockAlarmLine))
                .build();

        StockAlarm stockAlarmDb = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(StockAlarmResource.STOCK_ALARMS)
                .body(Mono.just(stockAlarm), StockAlarm.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAlarm.class)
                .value(Assertions::assertNotNull)
                .returnResult()
                .getResponseBody();
        assertNotNull(stockAlarmDb);

        StockAlarmLine[] stockAlarmLinesDb = this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(StockAlarmResource.STOCK_ALARMS + StockAlarmResource.STOCK_ALARM_SEARCH_WARNING)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAlarmLine[].class)
                .value(Assertions::assertNotNull)
                .returnResult()
                .getResponseBody();
        assertNotNull(stockAlarmLinesDb
        );
    }
}
