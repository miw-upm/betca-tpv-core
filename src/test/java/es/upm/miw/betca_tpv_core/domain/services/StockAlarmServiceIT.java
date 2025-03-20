package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarm;
import es.upm.miw.betca_tpv_core.domain.model.StockAlarmLine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    void testUpdateLines() {
        Article article1 = Article.builder().barcode("8400000000017").description("Articulo1").build();
        Article article2 = Article.builder().barcode("8400000000024").description("Articulo2").build();

        StockAlarmLine stockAlarmLine = StockAlarmLine.builder()
                .article(article1)
                .warning(2)
                .critical(1)
                .build();

        StockAlarm stockAlarm = StockAlarm.builder()
                .name("AlarmaUpdateLinesService")
                .description("Sin actualizar")
                .warning(3)
                .critical(1)
                .stockAlarmLines(List.of(stockAlarmLine))
                .build();

        StepVerifier.create(this.stockAlarmService.create(stockAlarm))
                .assertNext(createdStockAlarm -> {
                    assertNotNull(createdStockAlarm);
                    assertEquals("AlarmaUpdateLinesService", createdStockAlarm.getName());
                    assertEquals("Sin actualizar", createdStockAlarm.getDescription());
                    assertEquals(3, createdStockAlarm.getWarning());
                    assertEquals(1, createdStockAlarm.getCritical());
                })
                .expectComplete()
                .verify();

        StockAlarmLine updatedStockAlarmLine = StockAlarmLine.builder()
                .article(article2)
                .warning(5)
                .critical(3)
                .build();

        StockAlarm stockAlarmUpdated = StockAlarm.builder()
                .name("AlarmaUpdateLinesService")
                .description("Actualizado")
                .warning(5)
                .critical(2)
                .stockAlarmLines(List.of(updatedStockAlarmLine))
                .build();

        StepVerifier.create(this.stockAlarmService.updateLines("AlarmaUpdateLinesService", stockAlarmUpdated))
                .expectNextMatches(updated -> {
                    assertNotNull(updated.getName());
                    assertEquals("AlarmaUpdateLinesService", updated.getName());
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

    @Test
    void testSearchWarning() {
        Article article1 = Article.builder().barcode("8400000000017").description("Articulo1").build();

        StockAlarmLine stockAlarmLine = StockAlarmLine.builder()
                .article(article1)
                .warning(900)
                .critical(1)
                .build();

        StockAlarm stockAlarm = StockAlarm.builder()
                .name("AlarmaSearchWarningService")
                .description("Search")
                .warning(3)
                .critical(1)
                .stockAlarmLines(List.of(stockAlarmLine))
                .build();

        StepVerifier.create(this.stockAlarmService.create(stockAlarm))
                .assertNext(createdStockAlarm -> {
                    assertNotNull(createdStockAlarm);
                    assertEquals("AlarmaSearchWarningService", createdStockAlarm.getName());
                    assertEquals("Search", createdStockAlarm.getDescription());
                    assertEquals(3, createdStockAlarm.getWarning());
                    assertEquals(1, createdStockAlarm.getCritical());
                    assertNotNull(createdStockAlarm.getStockAlarmLines());
                    assertFalse(createdStockAlarm.getStockAlarmLines().isEmpty());
                    assertEquals(1, createdStockAlarm.getStockAlarmLines().size());
                })
                .expectComplete()
                .verify();

        StepVerifier.create(this.stockAlarmService.searchWarnings())
                .assertNext(Assertions::assertNotNull)
                .expectComplete()
                .verify();
    }
}
