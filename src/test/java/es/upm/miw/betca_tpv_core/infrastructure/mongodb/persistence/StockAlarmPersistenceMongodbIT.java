package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
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
        StockAlarm stockAlarm = StockAlarm.builder()
                .name("AlarmaDuplicada")
                .description("Descripcion inicial")
                .warning(5)
                .critical(3)
                .build();

        StepVerifier
                .create(this.stockAlarmPersistenceMongodb.create(stockAlarm)) // Primero, creamos la alarma
                .expectNextCount(1) // Verificamos que se creó correctamente
                .verifyComplete();

        StepVerifier
                .create(this.stockAlarmPersistenceMongodb.create(
                        StockAlarm.builder().name("AlarmaDuplicada").description("error").warning(7).critical(9).build())) // Segundo intento
                .expectError(ConflictException.class) // Debe lanzar la excepción
                .verify();
    }
}
