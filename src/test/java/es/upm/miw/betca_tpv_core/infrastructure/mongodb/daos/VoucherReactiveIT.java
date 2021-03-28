package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

@TestConfig
public class VoucherReactiveIT {

    @Autowired
    private VoucherReactive voucherReactive;

    @Test
    void testReadAll() {
        StepVerifier
                .create(voucherReactive.findAll())
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }

}
