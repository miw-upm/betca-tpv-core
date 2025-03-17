package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class StockAlarmServiceIT {

    @Autowired
    private StockAlarmService stockAlarmService;

    @Test
    void testCreate() {
        StockAlarm stockAlarm = StockAlarm.builder()
                .name("AlarmService")
                .description("Descripcion Service")
                .warning(6)
                .critical(2)
                .build();
        StepVerifier.create(stockAlarmService.create(stockAlarm))
                .expectNextMatches(stockAlarm1 -> {
                   assertNotNull(stockAlarm1.getName());
                   assertNotNull(stockAlarm1.getDescription());
                   return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindAll() {
        StepVerifier.create(stockAlarmService.findAll())
                .recordWith(ArrayList::new)
                .thenConsumeWhile(stockAlarm -> true)
                .consumeRecordedWith(stockAlarm -> {
                    assertThat(stockAlarm).isNotNull();
                    assertThat(stockAlarm.size()).isGreaterThanOrEqualTo(2);
                }).verifyComplete();
    }

    @Test
    void testRead() {
        StepVerifier.create(stockAlarmService.read("Alarma1"))
                .assertNext(stockAlarm -> {
                    assertEquals(5, stockAlarm.getWarning());
                    assertEquals(3, stockAlarm.getCritical());
                })
                .verifyComplete();
    }

    @Test
    void testUpdate() {
        StockAlarm stockAlarm = StockAlarm.builder().name("AlarmaActualizar").description("Actualizando")
                        .warning(8).critical(7).build();
        StepVerifier.create(stockAlarmService.create(stockAlarm)
                .flatMap(createdStockAlarm -> {
                    createdStockAlarm.setDescription("Descripcion actualizada");
                    return this.stockAlarmService.update(createdStockAlarm.getName(), createdStockAlarm);
                }))
                .expectNextMatches(stockAlarmUpdated -> {
                    assertNotNull(stockAlarmUpdated.getName());
                    assertNotNull(stockAlarmUpdated.getDescription());
                    assertEquals("Descripcion actualizada", stockAlarmUpdated.getDescription());
                    return true;
                })
                .expectComplete()
                .verify();
    }
}
