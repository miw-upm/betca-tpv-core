package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

@TestConfig
public class VoucherReactiveIT {

    @Autowired
    private VoucherReactive voucherReactive;

    @Test
    void testFindUnconsumedVouchersBetweenDates() {
        LocalDateTime from = LocalDateTime.now().minusDays(3);
        LocalDateTime to = LocalDateTime.now().plusDays(1);
        StepVerifier
                .create(this.voucherReactive.findByCreationDateBetweenAndDateOfUseIsNull(from, to))
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }
}
