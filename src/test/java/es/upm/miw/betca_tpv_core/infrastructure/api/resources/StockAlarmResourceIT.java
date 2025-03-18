package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

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
}
