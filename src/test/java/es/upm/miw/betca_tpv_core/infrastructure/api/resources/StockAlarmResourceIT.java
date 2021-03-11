package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.StockAlarmResource.*;
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
                .uri(STOCK_ALARMS)
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

    @Test
    void testFindByNameLike() {
        String nameAlarmFromSeederMultiple = "pack";
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(STOCK_ALARMS + NAME)
                        .queryParam("name", nameAlarmFromSeederMultiple)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StockAlarm.class)
                .value(Assertions::assertNotNull)
                .value(list -> assertEquals(2, list.size()));
    }

    @Test
    void testFindByNameFound() {
        String nameFromSeeder = "alarm-pac-2";
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(STOCK_ALARMS + NAME_ID, nameFromSeeder)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAlarm.class)
                .value(Assertions::assertNotNull)
                .value(stockAlarm -> assertEquals(nameFromSeeder, stockAlarm.getName()));
    }

    @Test
    void testUpdateAlarm() {
        String nameFromSeeder = "alarm-pac-2";
        StockAlarmLine stockAlarmLine = StockAlarmLine.builder().barcode("8400000000017").warning(11).critical(22).build();
        StockAlarm stockAlarm = StockAlarm.builder().name("alarm-pac-2").description("cambio").alarmLine(stockAlarmLine).warning(55).critical(33).build();

        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(STOCK_ALARMS + NAME_ID, nameFromSeeder)
                .body(Mono.just(stockAlarm), StockAlarm.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAlarm.class)
                .value(Assertions::assertNotNull)
                .value(stockAlarmReturned -> {
                            assertEquals("cambio", stockAlarmReturned.getDescription());
                            assertEquals(55, stockAlarmReturned.getWarning());
                            assertEquals(33, stockAlarmReturned.getCritical());

                            stockAlarmReturned.getStockAlarmLines()
                                    .forEach(stockAlarmLine1 -> {
                                        assertEquals(11, stockAlarmLine1.getWarning());
                                        assertEquals(22, stockAlarmLine1.getCritical());
                                    });
                        }
                );
    }

    @Test
    void testDelete() {
        String nameFromSeeder = "alarm-delete";

        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(STOCK_ALARMS + NAME_ID, nameFromSeeder)
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(STOCK_ALARMS + NAME_ID, nameFromSeeder + "asdsad")
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(STOCK_ALARMS + NAME_ID, nameFromSeeder)
                .exchange()
                .expectStatus().isNotFound();
    }

}
