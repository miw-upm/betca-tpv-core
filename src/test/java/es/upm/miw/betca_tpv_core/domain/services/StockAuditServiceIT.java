package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
public class StockAuditServiceIT {

    @Autowired
    private StockAuditService stockAuditService;

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
                    assertEquals(50, stockAudit.getLossValue());
                    assertEquals("BARCODE001", stockAudit.getLosses().getFirst().getBarcode());
                    assertEquals("BARCODE001", stockAudit.getArticlesWithoutAudit().getFirst().getBarcode());
                })
                .verifyComplete();
    }


}
