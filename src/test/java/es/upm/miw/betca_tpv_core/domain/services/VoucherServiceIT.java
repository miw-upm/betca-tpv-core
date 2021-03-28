package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

@TestConfig
public class VoucherServiceIT {

    @Autowired
    private VoucherService voucherService;

    @Test
    void testReadAll() {
        StepVerifier
                .create(voucherService.readAll())
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }
}
