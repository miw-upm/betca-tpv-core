package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class StockAlarmPersistenceMongodbIT {

    @Autowired
    private StockAlarmPersistenceMongodb stockAlarmPersistenceMongodb;
    @Autowired
    private ArticlePersistenceMongodb articlePersistenceMongodb;

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

    @Test
    void testUpdateConLines() {
        Article article1 = Article.builder().barcode("8400000000017").description("Articulo1").build();
        Article article2 = Article.builder().barcode("8400000000024").description("Articulo2").build();

        StockAlarmLine stockAlarmLine = StockAlarmLine.builder()
                .article(article1)
                .warning(2)
                .critical(1)
                .build();

        StockAlarm stockAlarm = StockAlarm.builder()
                .name("AlarmaUpdateLines")
                .description("Sin actualizar")
                .warning(3)
                .critical(1)
                .stockAlarmLines(List.of(stockAlarmLine))
                .build();

        StepVerifier.create(this.stockAlarmPersistenceMongodb.create(stockAlarm))
                .assertNext(createdStockAlarm -> {
                    assertNotNull(createdStockAlarm);
                    assertEquals("AlarmaUpdateLines", createdStockAlarm.getName());
                    assertEquals("Sin actualizar", createdStockAlarm.getDescription());
                    assertEquals(3, createdStockAlarm.getWarning());
                    assertEquals(1, createdStockAlarm.getCritical());
                    assertNotNull(createdStockAlarm.getStockAlarmLines());
                    assertFalse(createdStockAlarm.getStockAlarmLines().isEmpty());
                    assertEquals(1, createdStockAlarm.getStockAlarmLines().size());
                })
                .expectComplete()
                .verify();

        StockAlarmLine updatedStockAlarmLine = StockAlarmLine.builder()
                .article(article2)
                .warning(5)
                .critical(3)
                .build();

        StockAlarm stockAlarmUpdated = StockAlarm.builder()
                .name("AlarmaUpdateLines")
                .description("Actualizado")
                .warning(5)
                .critical(2)
                .stockAlarmLines(List.of(updatedStockAlarmLine))
                .build();

        StepVerifier.create(this.stockAlarmPersistenceMongodb.updateLines("AlarmaUpdateLines", stockAlarmUpdated))
                .expectNextMatches(updated -> {
                    assertNotNull(updated.getName());
                    assertEquals("AlarmaUpdateLines", updated.getName());
                    assertNotNull(updated.getDescription());
                    assertEquals("Actualizado", updated.getDescription());
                    assertEquals(5, updated.getWarning());
                    assertEquals(2, updated.getCritical());

                    assertNotNull(updated.getStockAlarmLines());
                    assertFalse(updated.getStockAlarmLines().isEmpty());
                    assertEquals(1, updated.getStockAlarmLines().size());

                    StockAlarmLine updatedLine = updated.getStockAlarmLines().getFirst();
                    assertEquals(article2.getBarcode(), updatedLine.getArticle().getBarcode());
                    assertEquals(article2.getBarcode(), updatedLine.getArticle().getBarcode());
                    assertEquals(5, updatedLine.getWarning());
                    assertEquals(3, updatedLine.getCritical());

                    return true;
                })
                .expectComplete()
                .verify();
    }
}
