package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RestTestConfig
class StockAlarmResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        StockAlarmLine stockAlarmLine = StockAlarmLine.builder().barcode("8400000000048").warning(1).critical(1).build();
        StockAlarm stockAlarm = StockAlarm.builder().name("alarmTest1").warning(5).critical(3).alarmLine(stockAlarmLine).build();

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(StockAlarmResource.STOCK_ALARMS)
                .body(Mono.just(stockAlarm), StockAlarm.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAlarm.class)
                .value(Assertions::assertNotNull)
                .value(stockAlarm1 -> {
                    assertEquals("alarmTest1", stockAlarm1.getName());
                    assertEquals(5, stockAlarm1.getWarning());
                    assertEquals(3, stockAlarm1.getCritical());
                    assertNotNull(stockAlarm1.getStockAlarmLines().stream()
                            .filter(stockAlarmLine1 -> stockAlarmLine1.getBarcode().equals(stockAlarmLine.getBarcode()))
                    );
                });
    }
}