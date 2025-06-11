package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.ArticleLoss;
import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class StockAuditPersistenceMongodbIT {

    public static final String ID_STOCK_AUDI = "ID001";
    @Autowired
    private StockAuditPersistenceMongodb stockAuditPersistenceMongodb;
    @Test
    void testFindAll(){
        StepVerifier
                .create(this.stockAuditPersistenceMongodb.findAll())
                .expectNextMatches(stockAudit -> {
                    assertNotNull(stockAudit.getId());
                    assertNotNull(stockAudit.getCreationDate());
                    return true;
                }) .thenCancel()
                .verify();
    }

    @Test
    void testRead(){
        StepVerifier
                .create(this.stockAuditPersistenceMongodb.read("AUDIT001"))
                .expectNextMatches(stockAudit -> {
                    assertEquals("AUDIT001", stockAudit.getId());
                    assertNotNull(stockAudit.getCloseDate());
                    assertNotNull(stockAudit.getCreationDate());
                    assertEquals(50, stockAudit.getLossValue().intValue());
                    assertEquals("BARCODE001", stockAudit.getLosses().getFirst().getBarcode());
                    assertEquals("BARCODE001", stockAudit.getArticlesAudited().getFirst().getBarcode());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testSave() {
        StepVerifier
                .create(this.stockAuditPersistenceMongodb
                        .create(getInstanceStockAudit())
                        .then(Mono.defer(() -> this.stockAuditPersistenceMongodb.read(ID_STOCK_AUDI)))
                ).assertNext(stockAudit -> {
                            assertEquals(0, stockAudit.getLossValue().intValue());
                            assertNotNull(stockAudit.getCreationDate());
                            assertNull(stockAudit.getCloseDate());
                            assertTrue(stockAudit.getLosses().isEmpty());
                        })
                .verifyComplete();
    }

    StockAudit getInstanceStockAudit(){
        return StockAudit.builder()
                .id(ID_STOCK_AUDI)
                .creationDate(LocalDateTime.now())
                .lossValue(BigDecimal.valueOf(0))
                .losses(List.of())
                .build();
    }

    @Test
    void testClose() {
        StockAudit stockAudit = new StockAudit();
        stockAudit.setId("AUDIT003");
        stockAudit.setCloseDate(null);
        stockAudit.setLossValue(BigDecimal.valueOf(100));
        stockAudit.setLosses(List.of(new ArticleLoss("12345", 2.0)));

        StepVerifier
                .create(stockAuditPersistenceMongodb.close(stockAudit)
                        .then(stockAuditPersistenceMongodb.read(stockAudit.getId())))
                .assertNext(updatedAudit -> {
                    assertEquals(stockAudit.getLossValue(), updatedAudit.getLossValue());
                    assertEquals(1, updatedAudit.getLosses().size());
                    assertNull(updatedAudit.getCloseDate());
                })
                .verifyComplete();
    }

    @Test
    void testUpdate() {
        StockAudit stockAudit = new StockAudit();
        stockAudit.setId("AUDIT" + System.currentTimeMillis());

        StepVerifier
                .create(stockAuditPersistenceMongodb.create(stockAudit)
                        .then(stockAuditPersistenceMongodb.update(stockAudit))
                        .then(stockAuditPersistenceMongodb.read(stockAudit.getId())))
                .assertNext(updatedAudit -> {
                    assertNotNull(updatedAudit.getUpdateDate());
                    assertNull(updatedAudit.getCloseDate());
                })
                .verifyComplete();
    }
}