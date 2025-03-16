package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticlePersistence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

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
        StepVerifier.create(stockAuditService.findAll())
                .recordWith(ArrayList::new)
                .thenConsumeWhile(stockAudit -> true)
                .consumeRecordedWith(stockAudit -> {
                    assertThat(stockAudit).isNotNull();
                    assertThat(stockAudit.size()).isGreaterThanOrEqualTo(5);
                }).verifyComplete();
    }

    @Test
    void testRead() {
        StepVerifier.create(stockAuditService.read("AUDIT001"))
                .assertNext(stockAudit -> {
                    assertEquals("AUDIT001", stockAudit.getId());
                    assertNotNull(stockAudit.getCloseDate());
                    assertNotNull(stockAudit.getCreationDate());
                    assertEquals(50, stockAudit.getLossValue().intValue());
                    assertEquals("BARCODE001", stockAudit.getLosses().getFirst().getBarcode());
                    assertEquals("BARCODE001", stockAudit.getArticlesWithoutAudit().getFirst().getBarcode());
                })
                .verifyComplete();
    }

    @Test
    void testCreate() {
        StepVerifier
                .create(stockAuditService.create()
                        .then(Mono.defer(() -> this.stockAuditService.findAll().last())))
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
                .create(stockAuditService.create()
                        .then(Mono.defer(() -> stockAuditService.findAll().last()))
                        .flatMap(stockAudit -> articlePersistence.readByBarcode(stockAudit.getArticlesWithoutAudit()
                                        .getFirst().
                                        getBarcode())
                                .flatMap(article -> {
                                    if (article.getStock() > 0) {
                                        article.setStock(article.getStock() + 5);
                                    }
                                    return articlePersistence.update(article.getBarcode(), article);
                                })
                                .thenReturn(stockAudit.getId()))
                        .flatMap(stockAuditService::close)
                        .then(Mono.defer(() -> stockAuditService.findAll().last())))
                .assertNext(stockAudit -> {
                    assertNotNull(stockAudit.getCloseDate());
                    assertTrue(stockAudit.getLossValue().intValue() > 0);
                    assertFalse(stockAudit.getLosses().isEmpty());
                    assertTrue(stockAudit.getArticlesWithoutAudit().isEmpty());
                })
                .verifyComplete();
    }
}
