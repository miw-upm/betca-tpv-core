package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
public class StockAlarmPersistenceMongodbIT {

    @Autowired
    private StockAlarmPersistenceMongodb stockAlarmPersistenceMongodb;

    @Test
    void testCreate() {
        StepVerifier
                .create(this.stockAlarmPersistenceMongodb.create(
                        StockAlarm.builder().name("AlarmaTest").description("Descripcion")
                                .warning(7).critical(9).build()))
                .assertNext(createdStockAlarm -> {
                    assertNotNull(createdStockAlarm);
                    assertEquals("AlarmaTest", createdStockAlarm.getName());
                    assertEquals("Descripcion", createdStockAlarm.getDescription());
                    assertEquals(7, createdStockAlarm.getWarning());
                    assertEquals(9, createdStockAlarm.getCritical());
                })
                .verifyComplete();
    }

    @Test
    void testCreateExistingName() {
        StepVerifier
                .create(this.stockAlarmPersistenceMongodb.create(
                        StockAlarm.builder().name("Alarma1").description("error")
                                .warning(7).critical(9).build()))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void testFindByName() {
        StepVerifier
                .create(this.stockAlarmPersistenceMongodb.readByName("Alarma2"))
                .expectNextMatches(stockAlarm -> {
                    assertEquals("Alarma2", stockAlarm.getName());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testUpdate() {
        StockAlarm stockAlarm = StockAlarm.builder().name("AlarmaUpdate").description("Sin actualizar")
                .warning(3).critical(1).build();
        StepVerifier.create(this.stockAlarmPersistenceMongodb.create(stockAlarm))
                .assertNext(createdStockAlarm -> {
                    assertNotNull(createdStockAlarm);
                    assertEquals("AlarmaUpdate", createdStockAlarm.getName());
                    assertEquals("Sin actualizar", createdStockAlarm.getDescription());
                    assertEquals(3, createdStockAlarm.getWarning());
                    assertEquals(1, createdStockAlarm.getCritical());
                })
                .expectComplete()
                .verify();
        StockAlarm stockAlarmUpdated = StockAlarm.builder().name("AlarmaUpdate").description("Actualizado")
                .warning(5).critical(2).build();
        StepVerifier.create(this.stockAlarmPersistenceMongodb.update("AlarmaUpdate", stockAlarmUpdated))
                .expectNextMatches(updated -> {
                    assertNotNull(updated.getName());
                    assertNotNull(updated.getDescription());
                    assertEquals(5, updated.getWarning());
                    assertEquals(2, updated.getCritical());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testUpdateNotFound() {
        StockAlarm stockAlarm = StockAlarm.builder().name("AlarmaNoUpdate").description("Sin actualizar")
                .warning(3).critical(1).build();
        StepVerifier.create(this.stockAlarmPersistenceMongodb.update("AlarmaNoUpdate", stockAlarm))
                .expectError(NotFoundException.class)
                .verify();
    }
}
