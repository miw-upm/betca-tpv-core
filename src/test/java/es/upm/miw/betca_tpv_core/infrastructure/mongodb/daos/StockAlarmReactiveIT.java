package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class StockAlarmReactiveIT {

    @Autowired
    private StockAlarmReactive stockAlarmReactive;

    @Test
    void testFindAll() {
        StepVerifier
                .create(this.stockAlarmReactive.findAll())
                .thenConsumeWhile(stockAlarm -> {
                    assertNotNull(stockAlarm);
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindByName() {
        String nameFromSeeder = "alarm-pac-2";
        StepVerifier
                .create(this.stockAlarmReactive.findByName(nameFromSeeder))
                .thenConsumeWhile(stockAlarm -> {
                    assertNotNull(stockAlarm);
                    System.out.println(stockAlarm);
                    return true;
                })
                .expectComplete()
                .verify();
    }
}
