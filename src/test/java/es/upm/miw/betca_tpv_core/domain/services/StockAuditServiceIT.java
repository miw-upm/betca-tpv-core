package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import es.upm.miw.betca_tpv_core.domain.model.ArticleAudit;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ArticleAuditDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockAuditUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@TestConfig
public class StockAuditServiceIT {

    @Autowired
    private StockAuditService stockAuditService;

    @Autowired
    private ArticlePersistence articlePersistence;

    @Test
    void testFindAll() {
        StepVerifier.create(stockAuditService.readAll())
                .recordWith(ArrayList::new)
                .thenConsumeWhile(stockAudit -> true)
                .consumeRecordedWith(stockAudits -> {
                    assertThat(stockAudits).isNotNull();
                    assertThat(stockAudits.size()).isGreaterThanOrEqualTo(5);
                }).verifyComplete();
    }

    @Test
    void testRead() {
        StepVerifier.create(stockAuditService.readOne("AUDIT001"))
                .assertNext(stockAudit -> {
                    assertEquals("AUDIT001", stockAudit.getId());
                    assertNotNull(stockAudit.getCloseDate());
                    assertNotNull(stockAudit.getCreationDate());
                    assertEquals(50, stockAudit.getLossValue().intValue());
                    assertEquals("BARCODE001", stockAudit.getLosses().getFirst().getBarcode());
                    assertEquals("BARCODE001", stockAudit.getArticlesAudited().getFirst().getBarcode());
                })
                .verifyComplete();
    }

    @Test
    void testCreate() {
        StepVerifier
                .create(
                        stockAuditService.create()
                                .flatMap(createdStockAudit -> stockAuditService.readOne(createdStockAudit.getId()))
                )
                .assertNext(stockAudit -> {
                    assertEquals(0, stockAudit.getLossValue().intValue());
                    assertNotNull(stockAudit.getCreationDate());
                    assertNull(stockAudit.getCloseDate());
                    assertTrue(stockAudit.getLosses().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void testClose() {
        StepVerifier
                .create(
                        stockAuditService.create()
                                .flatMap(createdStockAudit ->
                                        stockAuditService.readOne(createdStockAudit.getId())
                                                .flatMap(stockAudit -> {
                                                    // AUDITAR TODOS LOS ARTÍCULOS, simulando una pérdida en el primero
                                                    List<ArticleAuditDto> auditedDtos = new ArrayList<>();
                                                    boolean lossSimulated = false;
                                                    for (ArticleAudit toAudit : stockAudit.getArticlesWithoutAudit()) {
                                                        Integer stock = toAudit.getStock();
                                                        Integer real;
                                                        if (!lossSimulated && stock != null && stock > 0) {
                                                            real = stock - 1; // Simula pérdida en el primer artículo
                                                            lossSimulated = true;
                                                        } else {
                                                            real = stock;
                                                        }
                                                        auditedDtos.add(new ArticleAuditDto(
                                                                toAudit.getBarcode(),
                                                                stock,
                                                                real,
                                                                toAudit.getDescription(),
                                                                (toAudit.getRetailPrice() != null) ? toAudit.getRetailPrice() : BigDecimal.ONE
                                                        ));
                                                    }

                                                    return stockAuditService.update(
                                                            stockAudit.getId(),
                                                            new StockAuditUpdateDto(auditedDtos)
                                                    ).thenReturn(stockAudit.getId());
                                                })
                                )
                                .flatMap(id -> stockAuditService.close(id).thenReturn(id))
                                .flatMap(closedAuditId -> stockAuditService.readOne(closedAuditId))
                )
                .assertNext(stockAudit -> {
                    assertNotNull(stockAudit.getCloseDate());
                    assertTrue(stockAudit.getLossValue().intValue() > 0); // Hay pérdidas reales
                    assertFalse(stockAudit.getLosses().isEmpty());        // Hay al menos una pérdida
                    assertTrue(stockAudit.getArticlesWithoutAudit().isEmpty()); // Todos auditados
                })
                .verifyComplete();
    }
}