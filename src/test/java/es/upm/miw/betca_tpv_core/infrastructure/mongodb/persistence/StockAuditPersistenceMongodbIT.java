package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class StockAuditPersistenceMongodbIT {

    @Autowired
    private StockAuditPersistenceMongodb stockAuditPersistenceMongodb;
    @Test
    void testFindAll(){
        StepVerifier
                .create(this.stockAuditPersistenceMongodb.findAll())
                .expectNextMatches(stockAudit -> {
                    assertNotNull(stockAudit.getId());
                    assertFalse(stockAudit.getArticlesWithoutAudit().isEmpty());
                    assertFalse(stockAudit.getLosses().isEmpty());
                    assertNotNull(stockAudit.getCloseDate());
                    assertNotNull(stockAudit.getCreationDate());
                    assertNotNull(stockAudit.getLossValue());
                    return true;
                })
                .expectNextCount(4)
                .verifyComplete();
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

}