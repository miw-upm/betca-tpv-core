package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAlarmPersistence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class StockAlarmMongodbIT {

    @Autowired
    private StockAlarmPersistence stockAlarmPersistence;


    @Test
    void testCreateWithoutAlarmLines() {
        StockAlarm stockAlarm = StockAlarm.builder().name("alarmTest1").warning(5).critical(3).build();
        stockAlarm.setStockAlarmLines(null);
        StepVerifier
                .create(this.stockAlarmPersistence.create(
                        stockAlarm))
                .expectNextMatches(stockAlarm1 -> {
                    assertNotNull(stockAlarm1.getName());
                    assertTrue(stockAlarm1.getStockAlarmLines().size() == 0);
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testCreateWithAlarmLines() {
        StockAlarmLine stockAlarmLine = StockAlarmLine.builder().barcode("8400000000017").warning(1).critical(1).build();
        StockAlarm stockAlarm = StockAlarm.builder().name("alarmTest1").alarmLine(stockAlarmLine).warning(5).critical(3).build();
        StepVerifier
                .create(this.stockAlarmPersistence.create(
                        stockAlarm))
                .expectNextMatches(stockAlarm1 -> {
                    assertNotNull(stockAlarm1.getName());
                    assertTrue(stockAlarm1.getStockAlarmLines().size() == 1);
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testCreateErrorBarcodeAlarmLine() {
        StockAlarmLine stockAlarmLine = StockAlarmLine.builder().barcode("asdsad").warning(1).critical(1).build();
        StockAlarm stockAlarm = StockAlarm.builder().name("alarmTest1").alarmLine(stockAlarmLine).warning(5).critical(3).build();
        StepVerifier
                .create(this.stockAlarmPersistence.create(
                        stockAlarm))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testFindByNameExpectedError() {
        StepVerifier
                .create(this.stockAlarmPersistence.readByName("asdasdasdnotFound"))
                .expectError()
                .verify();
    }


    @Test
    void testUpdateWithAlarmLineList() {
        StockAlarmLine stockAlarmLine = StockAlarmLine.builder().barcode("8400000000017").warning(11).critical(22).build();
        StockAlarm stockAlarm = StockAlarm.builder().name("alarm-pac-2").description("cambio").alarmLine(stockAlarmLine).warning(55).critical(33).build();

        StepVerifier
                .create(this.stockAlarmPersistence.update("alarm-pac-2", stockAlarm))
                .expectNextMatches(stockAlarmReturned -> {
                    assertEquals("cambio", stockAlarmReturned.getDescription());
                    assertEquals(55, stockAlarmReturned.getWarning());
                    assertEquals(33, stockAlarmReturned.getCritical());
                    assertEquals(1, stockAlarm.getStockAlarmLines().size());
                    stockAlarm.getStockAlarmLines()
                            .forEach(stockAlarmLine1 -> {
                                assertEquals(11, stockAlarmLine1.getWarning());
                                assertEquals(22, stockAlarmLine1.getCritical());
                            });
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindAllStockAlarmLinesWithoutNull() {
        StepVerifier
                .create(this.stockAlarmPersistence.findAllStockAlarmLinesWithoutNull())
                .thenConsumeWhile(
                        stockAlarmLine -> {
                            assertTrue(stockAlarmLine.getWarning() > 0);
                            assertTrue(stockAlarmLine.getCritical() > 0);
                            return true;
                        }
                )
                .expectComplete()
                .verify();
    }
}
