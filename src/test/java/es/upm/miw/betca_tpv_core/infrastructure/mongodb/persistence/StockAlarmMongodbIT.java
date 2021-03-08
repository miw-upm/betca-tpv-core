package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import es.upm.miw.betca_tpv_core.domain.persistence.StockAlarmPersistence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class StockAlarmMongodbIT {

    @Autowired
    private StockAlarmPersistence stockAlarmPersistence;


    @Test
    void testCreate() {
        StockAlarmLine stockAlarmLine = StockAlarmLine.builder().barcode("8400000000017").warning(1).critical(1).build();
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
}
