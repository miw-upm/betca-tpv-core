package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

}