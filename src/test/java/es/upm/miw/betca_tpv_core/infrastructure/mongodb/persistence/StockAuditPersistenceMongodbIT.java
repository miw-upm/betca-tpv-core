package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
                    assertEquals(50, stockAudit.getLossValue());
                    assertEquals("BARCODE001", stockAudit.getLosses().getFirst().getBarcode());
                    assertEquals("BARCODE001", stockAudit.getArticlesWithoutAudit().getFirst().getBarcode());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testSave() {
        StepVerifier
                .create(this.stockAuditPersistenceMongodb
                        .save(getInstanceStockAudit())
                        .then(Mono.defer(() -> this.stockAuditPersistenceMongodb.read(ID_STOCK_AUDI)))
                ).assertNext(stockAudit -> {
                            assertEquals(0, stockAudit.getLossValue());
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
                .lossValue(0)
                .losses(List.of())
                .build();
    }

}